package builder.trendymovies.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import builder.trendymovies.Models.Movies;
import builder.trendymovies.MovieDetailActivity;
import builder.trendymovies.MovieDetailFragment;
import builder.trendymovies.R;
import builder.trendymovies.Utils.BuilderLogger;
import builder.trendymovies.Utils.Constants;
import builder.trendymovies.Utils.MyApplication;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shabaz on 19-Dec-15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> implements View.OnClickListener
{
    List<Movies.Result> movieList;
    BuilderLogger mLog = new BuilderLogger(MoviesAdapter.class.getSimpleName());
    boolean mTwoPane = false;
    AppCompatActivity mActivityCompat;
    public MoviesAdapter(List<Movies.Result>  movieList, boolean twopane, AppCompatActivity mActivityCompat)
    {
        this.mActivityCompat = mActivityCompat;
        this.movieList = movieList;
        mTwoPane = twopane;
    }
    public void changeDataSet(List<Movies.Result>  movieList)
    {
        this.movieList = movieList;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(v); //create a ViewHolder and pass it
        v.setOnClickListener(this);
        return movieViewHolder;
    }
int i=0;
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position)
    {
        Movies.Result movie = movieList.get(position);
        holder.movieName.setText(movie.getTitle());
        //mLog.d("Poster Query = "+ Constants.POSTER_BASE+movie.getPosterPath());
        if(movie.getPosterPath()!=null)
        {
            String URI;
            if(movie.getPosterPath().contains("poster"))
            {

                Picasso.with(MyApplication.context).load(new File(movie.getPosterPath())).into(holder.moviePoster);
                //URI = "file:"+movie.getPosterPath();
            }
            else
            {
                URI = Constants.POSTER_BASE + movie.getPosterPath();

                mLog.d("POSTER = "+URI);
                Picasso.with(MyApplication.context).load(URI).into(holder.moviePoster);
            }
        }
    }


    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount()
    {
        return movieList.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    // RecyclerView Does not have a onItemClickListener , so had to have onclickListener for each and every item
    @Override
    public void onClick(View v)
    {
        int itemPosition = ((RecyclerView)v.getParent()).getChildLayoutPosition(v);
        if (mTwoPane)
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable("DETAILS",(Parcelable)movieList.get(itemPosition));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            mActivityCompat.getSupportFragmentManager().beginTransaction().replace(R.id.mymovie_detail_container, fragment).commit();
        } else
        {


            Intent mIntent = new Intent(v.getContext(), MovieDetailActivity.class);
            mIntent.putExtra("DETAILS",(Parcelable)movieList.get(itemPosition));
            v.getContext().startActivity(mIntent);
        }
        //Toast.makeText(v.getContext(), "clicked "+itemPosition, Toast.LENGTH_SHORT).show();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.movie_name) TextView movieName;
        @Bind(R.id.movie_poster) ImageView moviePoster;

        public MovieViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }



    }
}
