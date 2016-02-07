package builder.trendymovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import builder.trendymovies.Interfaces.ReviewsInterface;
import builder.trendymovies.Interfaces.TrailersInterface;
import builder.trendymovies.Models.Movies;
import builder.trendymovies.Models.Reviews;
import builder.trendymovies.Models.Trailers;
import builder.trendymovies.Utils.BuilderLogger;
import builder.trendymovies.Utils.Constants;
import builder.trendymovies.Utils.Helper;
import builder.trendymovies.Utils.MyApplication;
import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment representing a single MyMovie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment  implements View.OnClickListener
{
    @Bind(R.id.movie_name)
    TextView movieName;
    @Bind(R.id.release_date) TextView releaseDate;
    @Bind(R.id.user_rating) TextView userRating;
    @Bind(R.id.plot) TextView plot;
    @Bind(R.id.movie_poster)
    ImageView moviePoster;
    @Bind(R.id.backdrop)
    RelativeLayout backDrop;
    @Bind(R.id.trailer_container)
    LinearLayout trailerContaier;
    @Bind(R.id.review_container) LinearLayout reviewContaier;
    @State
    Movies.Result movieDetails;
    BuilderLogger mLog = new BuilderLogger(MovieDetailFragment.class.getSimpleName());
    Retrofit retrofit;
    ReviewsInterface reviewService;
    TrailersInterface trailerService;
    private Subscription subscription;
    boolean isFavorite = false;
    Target backDropTarget = new Target(){

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
    };
    public MovieDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.mymovie_detail, container, false);
        ButterKnife.bind(this,rootView);
        movieDetails = this.getArguments().getParcelable("DETAILS");
        movieName.setText(movieDetails.getTitle());
        releaseDate.setText(movieDetails.getReleaseDate());
        userRating.setText(movieDetails.getVoteAverage()+"");
        plot.setText(movieDetails.getOverview());
        String uri;
        if(movieDetails.getPosterPath().contains("poster"))
        {

            uri = Uri.fromFile(new File(movieDetails.getPosterPath())).toString();
            mLog.d("Loading Poster  locally "+uri);

            Picasso.with(MyApplication.context).load(new File(movieDetails.getPosterPath())).into(moviePoster);
        }
        else
        {
            uri = Constants.POSTER_BASE+movieDetails.getPosterPath();
            mLog.d("Loading Poster from  "+uri);

            Picasso.with(MyApplication.context).load(uri).into(moviePoster);
        }
        if(movieDetails.getBackdropPath().contains("back_drop"))
        {
            uri = Uri.fromFile(new File(movieDetails.getBackdropPath())).toString();
            // uri = "file:"+movieDetails.getPosterPath();
            mLog.d("Loading Back Drop locally "+uri);

            Picasso.with(MyApplication.context).load(new File(movieDetails.getBackdropPath())).into(backDropTarget);
        }
        else
        {
            uri = Constants.POSTER_BASE+movieDetails.getBackdropPath();
            mLog.d("Loading Poster from  "+uri);

            Picasso.with(MyApplication.context).load(uri).into(backDropTarget);
        }

        if(Helper.isNetworkConnected())
        {
            initializeRetrofit();
            populateTrailer(movieDetails.getId());
            populateReviews(movieDetails.getId());

        }
        else
        {
            if(movieDetails.getTrailers()!=null)
            {
                List<Trailers.Result> trailersList = movieDetails.getTrailers();
                int len = trailersList.size();
                for (int i=0;i<len;i++)
                {
                    View v = LayoutInflater.from(trailerContaier.getContext()).inflate(R.layout.trailer_layout, trailerContaier, false);
                    v.setTag(trailersList.get(i).getKey());
                    v.setOnClickListener(MovieDetailFragment.this);
                    ((TextView)v.findViewById(R.id.trailer)).setText("Trailer "+(i+1));
                    trailerContaier.addView(v);

                }
            }

            if(movieDetails.getReviews()!=null)
            {
                List<Reviews.Result> reviewsList = movieDetails.getReviews();
                movieDetails.setReviews(reviewsList);
                int len = reviewsList.size();
                for (int i=0;i<len;i++)
                {
                    View v = LayoutInflater.from(reviewContaier.getContext()).inflate(R.layout.review_layout, reviewContaier, false);
                    ((TextView)v.findViewById(R.id.author)).setText("Review "+(i+1)+" By "+reviewsList.get(i).getAuthor()+":");
                    ((TextView)v.findViewById(R.id.review)).setText(reviewsList.get(i).getContent());
                    reviewContaier.addView(v);

                }
            }

        }

        return rootView;
    }

    private void populateReviews(Integer id)
    {
        mLog.d("ID  = "+id);
        Observable<Reviews> call = reviewService.getReviews(id.toString(),Constants.MOVIE_API_KEY);
        subscription = call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Reviews>()
        {

            @Override
            public void onCompleted()
            {
                mLog.d("Receive Complete");
            }


            @Override
            public void onError(Throwable e)
            {
                mLog.d("There was a error");
                TextView mTextView = new TextView(MyApplication.context);
                mTextView.setGravity(Gravity.CENTER);
                mTextView.setText("Error Loading Reviews");
                reviewContaier.addView(mTextView);
                e.printStackTrace();
            }


            @Override
            public void onNext(Reviews reviews)
            {
                List<Reviews.Result> reviewsList = reviews.getResults();
                movieDetails.setReviews(reviewsList);
                int len = reviewsList.size();
                for (int i=0;i<len;i++)
                {
                    View v = LayoutInflater.from(reviewContaier.getContext()).inflate(R.layout.review_layout, reviewContaier, false);
                    ((TextView)v.findViewById(R.id.author)).setText("Review "+(i+1)+" By "+reviewsList.get(i).getAuthor()+":");
                    ((TextView)v.findViewById(R.id.review)).setText(reviewsList.get(i).getContent());
                    reviewContaier.addView(v);

                }
            }

        });
    }

    private void populateTrailer(Integer id)
    {
        mLog.d("ID  = "+id);
        Observable<Trailers> call = trailerService.getTrailers(id.toString(),Constants.MOVIE_API_KEY);
        subscription = call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Trailers>()
        {

            @Override
            public void onCompleted()
            {
                mLog.d("Receive Complete");
            }

            @Override
            public void onError(Throwable e)
            {
                mLog.d("There was a error");
                TextView mTextView = new TextView(MyApplication.context);
                mTextView.setGravity(Gravity.CENTER);
                mTextView.setText("Error Loading Trailers");
                trailerContaier.addView(mTextView);
                e.printStackTrace();
            }

            @Override
            public void onNext(Trailers trailers)
            {

                List<Trailers.Result> trailersList = trailers.getResults();
                movieDetails.setTrailers(trailersList);
                int len = trailersList.size();
                for (int i=0;i<len;i++)
                {
                    View v = LayoutInflater.from(trailerContaier.getContext()).inflate(R.layout.trailer_layout, trailerContaier, false);
                    v.setTag(trailersList.get(i).getKey());
                    v.setOnClickListener(MovieDetailFragment.this);
                    ((TextView)v.findViewById(R.id.trailer)).setText("Trailer "+(i+1));
                    trailerContaier.addView(v);

                }
            }

        });
    }

    void initializeRetrofit()
    {
        /*HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/
        OkHttpClient client = new OkHttpClient();
        //  client.interceptors().add(interceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        trailerService = retrofit.create(TrailersInterface.class);
        reviewService = retrofit.create(ReviewsInterface.class);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.trailer_view:
                mLog.d(v.getTag()+"");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+v.getTag())));
                break;
        }

    }


    int i=0;
    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.favorite)
        {
            mLog.d("Favorite Clicked");
            Toast.makeText(MyApplication.context,"Added to Favorite",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    mLog.d("NEW Thread Running");
                    mLog.d("Loading from  "+movieDetails.getPosterPath());
                    Picasso.with(MyApplication.context).load(Constants.POSTER_BASE+movieDetails.getPosterPath()).into(new Target()
                    {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from)
                        {
                            mLog.d("Image Loaded");
                            try
                            {
                                File file = null;

                                // judge "imgs/.nomedia"'s existance to judge whether path available
                                file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + movieDetails.getId() + "poster" + ".jpg");
                                if (file.exists())
                                {
                                    mLog.d(file.getPath()+" File Exists ");

                                }
                                else
                                {
                                    try
                                    {
                                        mLog.d("Writing to File");
                                        file.createNewFile();
                                        mLog.d(file.getPath());


                                        FileOutputStream ostream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                                        ostream.close();

                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                                movieDetails.setPosterPath(file.getPath());
                                i++;
                                mLog.d("CALLIng: "+Constants.BACKDROP_BASE+movieDetails.getBackdropPath());
                                Picasso.with(MyApplication.context).load(Constants.BACKDROP_BASE+movieDetails.getBackdropPath()).into(new Target()
                                {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                                    {
                                        try
                                        {
                                            File file = null;

                                            // judge "imgs/.nomedia"'s existance to judge whether path available
                                            file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + movieDetails.getId() + "back_drop" + ".jpg");
                                            if (file.exists())
                                            {
                                                mLog.d(file.getPath() +" Exists");
                                                movieDetails.setBackdropPath(file.getPath());

                                            }
                                            else
                                            {
                                                try
                                                {
                                                    file.createNewFile();
                                                    mLog.d(file.getPath());
                                                    FileOutputStream ostream = new FileOutputStream(file);
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                                                    ostream.close();

                                                } catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }

                                            }
                                            movieDetails.setBackdropPath(file.getPath());
                                            i++;

                                            SharedPreferences mPreferences = MyApplication.context.getSharedPreferences(Constants.OFFLINE_PREF_KEY, MyApplication.context.MODE_PRIVATE);
                                            Gson gson = new Gson();
                                            String movieDetailsString = gson.toJson(movieDetails).toString();
                                            mLog.d("Saving: " + movieDetailsString);
                                            mPreferences.edit().putString(movieDetails.getId().toString(), movieDetailsString).apply();

                                            item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }


                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable)
                                    {
                                        mLog.d("Failed ");
                                    }


                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable)
                                    {
                                        mLog.d("Prepping ");
                                    }
                                });

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable)
                        {
                            mLog.d("Failed ");
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable)
                        {
                            mLog.d("Prepping ");
                        }
                    });

                }
            }).run();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
