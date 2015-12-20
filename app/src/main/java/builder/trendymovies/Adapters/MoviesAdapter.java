package builder.trendymovies.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import builder.trendymovies.Models.Result;
import builder.trendymovies.MoviesDetialActivity;
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
    List<Result> movieList;
    BuilderLogger mLog = new BuilderLogger(MoviesAdapter.class.getSimpleName());
    public MoviesAdapter(List<Result>  movieList)
    {
        this.movieList = movieList;
    }
    public void changeDataSet(List<Result>  movieList)
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
        Result movie = movieList.get(position);
        holder.movieName.setText(movie.getTitle());
        //mLog.d("Poster Query = "+ Constants.POSTER_BASE+movie.getPosterPath());
        if(movie.getPosterPath()!=null)
        Picasso.with(MyApplication.context).load(Constants.POSTER_BASE+movie.getPosterPath()).into(holder.moviePoster);
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
        Intent mIntent = new Intent(v.getContext(), MoviesDetialActivity.class);
        mIntent.putExtra("DETAILS",movieList.get(itemPosition));
        v.getContext().startActivity(mIntent);
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
