package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity{
    // movie to display
    Movie movie;

    // view objects
    TextView tvTitle;
    TextView tvOverview;
    TextView tvReleaseDate;
    RatingBar rbVoteAverage;
    ImageView ivTrailer;
    String imageUrl;
    Integer videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivTrailer = (ImageView) findViewById(R.id.ivTrailer);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        String movie_url =
                "https://api.themoviedb.org/3/movie/" + movie.getVideoId() + "/videos?api_key=" + R.string.tmdb_api_key + "&language=en-US";

        // http client to call Youtube API
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(movie_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("Youtube API call", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    // gets video id from tmdb to use in youtube api
                    final String youtubeKey = jsonObject.getJSONArray("results").getJSONObject(0).getString("key");

                    // if imageview is clicked, sends youtubeKey to MovieTrailerActivity
                    ivTrailer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // intent needed to go to MovieTrailerActivity
                            Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            i.putExtra("youtubeKey", youtubeKey);
                            startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("MovieDetailsActivity", "onFailure");
            }
        });

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText("Released on " + movie.getReleaseDate());
        imageUrl = movie.getBackdropPath();
        videoId = movie.getVideoId();
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).into(ivTrailer);

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);





    }
}