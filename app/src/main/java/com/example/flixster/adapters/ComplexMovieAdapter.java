package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.MovieDetailsActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

public class ComplexMovieAdapter extends RecyclerView.Adapter<ComplexMovieAdapter.ViewHolder>{
    private final int POPULAR_MOVIE = 1;
    Context context;
    List<Movie> movies;

    public ComplexMovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Inflates a layout from XML and returns the ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case POPULAR_MOVIE:
                View backdropView = inflater.inflate(R.layout.item_backdrop, parent, false);
                viewHolder = new ViewHolder(backdropView);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.item_movie, parent, false);
                viewHolder = new ViewHolder(defaultView);
                break;
        }
        return viewHolder;
    }

    @Override
    // Populates data into ViewHolder
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder " + position);
        Movie movie;
        switch(viewHolder.getItemViewType()) {
            case POPULAR_MOVIE:
                ViewHolder backdropHolder = (ViewHolder) viewHolder;
                movie = movies.get(position);
                backdropHolder.bindBackdrop(movie);
                break;
            default:
                ViewHolder defaultHolder = (ViewHolder) viewHolder;
                movie = movies.get(position);
                defaultHolder.bind(movie);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (movies.get(position).getVoteAverage() > 5) {
            return POPULAR_MOVIE;
        }
        return -1;
    }

    @Override
    public int getItemCount() { return movies.size(); }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl;

            // if phone is in landscape, then imageUrl = backdrop image, else imageUrl = poster image
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
            } else {
                imageUrl = movie.getPosterPath();
            }


            Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(ivPoster);
        }

        public void bindBackdrop(Movie movie) {
            String imageUrl = movie.getBackdropPath();
            Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(ivPoster);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                context.startActivity(intent);
            }
        }
    }
}
