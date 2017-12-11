package com.haristimuno.movies.adapter;

/**
 * Created by hector on 03-12-2017.
 * Adapter to show list of movies
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haristimuno.movies.R;
import com.haristimuno.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> implements View.OnClickListener, Filterable {

    public List<Movie> movies;
    public List<Movie> moviesFiltered;
    private int rowLayout;
    private Context context;
    private View.OnClickListener listener;
    private MoviesAdapterListener mListener;

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.moviesFiltered = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context, MoviesAdapterListener mListener) {
        this.movies = movies;
        this.moviesFiltered = movies;
        this.rowLayout = rowLayout;
        this.context = context;
        this.mListener = mListener;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        view.setOnClickListener(this);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        String tittle = moviesFiltered.get(position).getTitle();
        holder.movieTitle.setText(moviesFiltered.get(position).getTitle());
        String dateF = moviesFiltered.get(position).getReleaseDate().substring(0,4);
        holder.date.setText(dateF);

        String overview = moviesFiltered.get(position).getOverview();

        if(tittle.concat(overview).concat(dateF).length()>121) {
            overview = overview.substring(0,85) +" ...";
        } else {
            if(overview!= null && overview.length()>103) {
                overview = overview.substring(0,103) +" ...";
            }
        }

        holder.movieDescription.setText(overview);
        holder.rating.setText(moviesFiltered.get(position).getVoteAverage().toString());

        String s = moviesFiltered.get(position).getPosterPath(); //w185
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+s).error(R.drawable.no_image).into(holder.itemImage);
    }

    public Movie get(int position) {
        return moviesFiltered.get(position);
    }

    public void setArray(List<Movie> l) {
        movies = l;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return moviesFiltered.size();
    } //TODO: ver esto

    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.onClick(v);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    moviesFiltered = movies;
                } else {
                    List<Movie> filteredList = new ArrayList<>();
                    for (Movie row : movies) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    moviesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = moviesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                moviesFiltered = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView date;
        TextView movieDescription;
        TextView rating;
        ImageView itemImage;


        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            movieTitle = (TextView) v.findViewById(R.id.title);
            date = (TextView) v.findViewById(R.id.date);
            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.rating);
            itemImage = (ImageView) v.findViewById(R.id.itemImage);
        }
    }

    public interface MoviesAdapterListener {
        void onMovieSelected(Movie movie);
    }
}