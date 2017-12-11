package com.haristimuno.movies.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haristimuno.movies.R;
import com.haristimuno.movies.activity.MainActivity;
import com.haristimuno.movies.model.Movie;
import com.haristimuno.movies.rest.ApiClient;
import com.haristimuno.movies.rest.ApiService;
import com.squareup.picasso.Picasso;

/**
 * @author Hector Aristimuño
 * this class shows the movie content
 */

public class ContentFragment extends Fragment {
    public static final String CONTENTFRAGMENT_TAG = "CONTENT_FRAGMENT";
    public static final String ARG_CATEGORY = "category";
    private static final String TAG = ContentFragment.class.getSimpleName();
    private View view;
    private Activity activity;

    private ImageView itemImage;
    private TextView overview;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;

    public ContentFragment() {
        // Empty Constructor
    }

    public static ContentFragment newInstance(String newsId) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("argument_news_id", newsId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ContentFragment newInstance(Movie movie) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        bundle.putParcelable("movie", movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_fragment, container, false);

        activity = getActivity();
        view = rootView;

        Movie movie = getArguments().getParcelable("movie");
        initComponents(view, movie);
        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * components initialization
     * @param v: view content
     * @param movie: item to show
     */
    private void initComponents(View v, Movie movie) {

        itemImage = (ImageView) view.findViewById(R.id.itemImage);
        String s = movie.getPosterPath(); //w185
        Picasso.with(activity.getApplicationContext()).load("http://image.tmdb.org/t/p/w185"+s).into(itemImage);

        title = (TextView) view.findViewById(R.id.posttittle);
        title.setText(validateString(movie.getTitle()));

        overview = (TextView) view.findViewById(R.id.overview);
        overview.setText(validateString(movie.getOverview()));

        releaseDate = (TextView) view.findViewById(R.id.releaseDate);
        releaseDate.setText(validateString(movie.getReleaseDate().toString()));

        rating = (TextView) view.findViewById(R.id.rating);
        rating.setText(validateString(movie.getVoteAverage().toString()));

        //movie = getData(movie.getId());
    }

    /**
     * get movie detail
     * @param movieId: ID
     * @return Movie
     */
    public Movie getData(int movieId) {
        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        return apiService.getMovieDetails(movieId ,MainActivity.API_KEY);
    }

    /**
     * method to replace null values
     * @param str: string to evaluate
     * @return if isn't valid string returns -
     */
    public String validateString(String str) {
        return str==null || str.equals("") ? "-" : str;
    }
}
