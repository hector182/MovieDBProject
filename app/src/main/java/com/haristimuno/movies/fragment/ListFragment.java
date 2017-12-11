package com.haristimuno.movies.fragment;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haristimuno.movies.R;
import com.haristimuno.movies.activity.MainActivity;
import com.haristimuno.movies.adapter.MoviesAdapter;
import com.haristimuno.movies.cache.CacheStorageImp;
import com.haristimuno.movies.model.Movie;
import com.haristimuno.movies.model.MoviesResponse;
import com.haristimuno.movies.rest.ApiClient;
import com.haristimuno.movies.rest.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author Hector Aristimuño
 * this class shows the List of movies
 */
public class ListFragment extends Fragment implements MoviesAdapter.MoviesAdapterListener,
                 MenuItem.OnActionExpandListener {
    public static final String LISTFRAGMENT_TAG = "LIST_FRAGMENT";
    public static final String ARG_POSITION = "position";
    public static final String ARG_CATEGORY = "category";

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    private TextView generalError;

    private static final String TAG = ContentFragment.class.getSimpleName();
    private View view;
    private Activity activity;
    private int selectedPosition = 0;
    FragmentManager fragmentManager = getFragmentManager();
    private SearchView searchView;
    private List<Movie> movies;
    public String category;

    public ListFragment() {
        // Empty Constructor
    }

    public static ListFragment newInstance(String newsId) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("argument_news_id", newsId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        selectedPosition = getArguments().getInt(ARG_POSITION);
        category=getArguments().getString(ListFragment.ARG_CATEGORY);
        activity = getActivity();
        view = rootView;
        initComponents(view, selectedPosition);

        return rootView;
    }

    /**
     * components initialization
     * @param v: view content
     * @param categ: category movie
     */
    private void initComponents(final View v, int categ) {
        progressBar = (ProgressBar) v.findViewById(R.id.pbHeaderProgress);
        progressBar.setVisibility(View.INVISIBLE);
        generalError = (TextView) v.findViewById(R.id.generalError);
        generalError.setVisibility(View.GONE);
        recyclerView = (RecyclerView) v.findViewById(R.id.movies_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        if (MainActivity.API_KEY.isEmpty()) {
            Toast.makeText(v.getContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return;
        }

        if(isOnline()) {
            loadServiceData(categ);
        } else {
            loadlocalData();
        }
    }

    /**
     * method to load the data from API
     * @param categ: data ID
     */
    public void loadServiceData(final int categ) {
        progressBar.setVisibility(View.VISIBLE);
        generalError.setVisibility(View.GONE);

        Call<MoviesResponse> call = getData(categ);
        movies = new ArrayList<>();

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                movies = response.body().getResults();
                Log.d(TAG, "Number of movies received: " + movies.size());
                int statusCode = response.code();

                CacheStorageImp c = new CacheStorageImp(getContext());
                String json = new Gson().toJson(movies);
                c.save(category, json);

                adapter = new MoviesAdapter(movies, R.layout.adapter_movies, view.getContext());
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                addListListener();
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                generalError.setVisibility(View.VISIBLE);
                Snackbar.make(view, " Error to load data", Snackbar.LENGTH_SHORT)
                        .show();
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    /**
     * method to load the data from cache
     */
    public void loadlocalData() {
        progressBar.setVisibility(View.VISIBLE);
        generalError.setVisibility(View.GONE);
        CacheStorageImp c = new CacheStorageImp(getContext());
        movies = new Gson().fromJson(c.get(category), new TypeToken<List<Movie>>(){}.getType());

        if(movies==null || movies.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            generalError.setVisibility(View.VISIBLE);
            return;
        }

        adapter = new MoviesAdapter(movies, R.layout.adapter_movies, view.getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        addListListener();

        progressBar.setVisibility(View.GONE);
    }

    /**
     * method to load the data from cache
     * @return if the application has intenet connection
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * method to get the data
     * @param categ
     * @return Array of movies
     */
    public Call<MoviesResponse> getData(int categ) {
        Call<MoviesResponse> call = null;

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        switch (selectedPosition) {
            case 0:
                call = apiService.getPopularMovies(MainActivity.API_KEY);
                break;
            case 1:
                call = apiService.getTopRatedMovies(MainActivity.API_KEY);
                break;
            case 2:
                call = apiService.getUpcomingMovies(MainActivity.API_KEY);
                break;
        }

        return call;
    }

    @Override
    public void onMovieSelected(Movie movie) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    resetSearch();
                    return false;
                }

                List<Movie> filteredValues = new ArrayList<>(movies);
                for (Movie value : movies) {
                    if (!value.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filteredValues.remove(value);
                    }
                }

                adapter = new MoviesAdapter(filteredValues, R.layout.adapter_movies, view.getContext());
                recyclerView.setAdapter(adapter);
                addListListener();
                adapter.notifyDataSetChanged();

                return false;
            }
        });

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();

                adapter = new MoviesAdapter(movies, R.layout.adapter_movies, view.getContext());
                recyclerView.setAdapter(adapter);
                addListListener();
                adapter.notifyDataSetChanged();
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void resetSearch() {
        adapter = new MoviesAdapter(movies, R.layout.adapter_movies, view.getContext());
        addListListener();
        adapter.notifyDataSetChanged();
    }

    public void addListListener() {
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = adapter.get(recyclerView.getChildAdapterPosition(v));

                ContentFragment fragment = ContentFragment.newInstance(movie);
                fragmentManager = getFragmentManager();
                ListFragment f = (ListFragment) fragmentManager.findFragmentByTag(ListFragment.LISTFRAGMENT_TAG);
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if(f != null) {
                    transaction.hide(f);
                    resetSearch();
                    recyclerView.setAdapter(adapter);
                    addListListener();
                    adapter.notifyDataSetChanged();
                }

                transaction.add(R.id.test_fragment, fragment,ContentFragment.CONTENTFRAGMENT_TAG);
                transaction.addToBackStack(ContentFragment.CONTENTFRAGMENT_TAG);
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }
}