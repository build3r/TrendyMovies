package builder.trendymovies;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import builder.trendymovies.Models.Result;
import builder.trendymovies.Utils.Constants;
import builder.trendymovies.Utils.MyApplication;
import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class MoviesDetialActivity extends AppCompatActivity
{
    /*
    original title
    movie poster image thumbnail
    A plot synopsis (called overview in the api)
    user rating (called vote_average in the api)
    release date
    */
    @Bind(R.id.movie_name) TextView movieName;
    @Bind(R.id.release_date) TextView releaseDate;
    @Bind(R.id.user_rating) TextView userRating;
    @Bind(R.id.plot) TextView plot;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.backdrop) RelativeLayout backDrop;
    @State Result movieDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_movies_detial);
        ButterKnife.bind(this);
        movieDetails = this.getIntent().getParcelableExtra("DETAILS");
        getSupportActionBar().setTitle(movieDetails.getTitle());
        movieName.setText(movieDetails.getTitle());
        releaseDate.setText(movieDetails.getReleaseDate());
        userRating.setText(movieDetails.getVoteAverage()+"");
        plot.setText(movieDetails.getOverview());
        Picasso.with(this).load(Constants.POSTER_BASE+movieDetails.getPosterPath()).into(moviePoster);
        Picasso.with(this).load(Constants.BACKDROP_BASE+movieDetails.getBackdropPath()).into(new Target(){

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            backDrop.setBackground(new BitmapDrawable(MyApplication.context.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(final Drawable errorDrawable) {
            Log.d("TAG", "FAILED");
        }

        @Override
        public void onPrepareLoad(final Drawable placeHolderDrawable) {
            Log.d("TAG", "Prepare Load");
        }
    });

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
