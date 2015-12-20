package builder.trendymovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import builder.trendymovies.Adapters.MoviesAdapter;
import builder.trendymovies.Interfaces.MoviesInterface;
import builder.trendymovies.Models.Movies;
import builder.trendymovies.Utils.BuilderLogger;
import builder.trendymovies.Utils.Constants;
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

public class MoviesActivity extends AppCompatActivity
{
    @Bind(R.id.movies_grid) RecyclerView moviesGrid;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.loading_layout) RelativeLayout loadingLayout;
    String SORT_POPULAR = "popularity.desc";
    //vote_average doesn't seems to be a good metric, movies with 1 vote of 10 are list on the top :-|
    String SORT_VOTE = "vote_count.desc";
    @State String CURRENT_SORT = "popularity.desc";
    RecyclerView.LayoutManager mLayoutManager;
    BuilderLogger mLog = new BuilderLogger(MoviesActivity.class.getSimpleName());
    Toast sortType ;
    Retrofit retrofit;
    MoviesInterface apiService;
    MoviesAdapter mAdapter;
    private Subscription subscription;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Trendy Movies-Popular");
        sortType = new Toast(this);
        mLayoutManager = new GridLayoutManager(this, 2);
        moviesGrid.setLayoutManager(mLayoutManager);
        initializeRetrofit();
        if(isNetworkConnected())
        {
            populateMovies(CURRENT_SORT);
        }
        else
        {
            Snackbar.make(fab, "Internet Connection Unavailable", Snackbar.LENGTH_LONG).show();
        }
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isNetworkConnected())
                {
                    moviesGrid.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    if(CURRENT_SORT.equals(SORT_POPULAR))
                    {
                        CURRENT_SORT = SORT_VOTE;
                        populateMovies(CURRENT_SORT);
                        toolbar.setTitle("Trendy Movies-Vote Count");
                        Snackbar.make(view, "Highest Vote Count", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    else
                    {
                        CURRENT_SORT = SORT_POPULAR;
                        toolbar.setTitle("Trendy Movies-Popular");
                        populateMovies(CURRENT_SORT);
                        Snackbar.make(view, "Popular Order", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                else
                {
                    Snackbar.make(fab, "Internet Connection Unavailable", Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }
    void initializeRetrofit()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(interceptor);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(MoviesInterface.class);
    }


    void populateMovies(String sortOrder)
    {
        mLog.d(sortOrder);
        Observable<Movies> call = apiService.getMoviesList(sortOrder,Constants.MOVIE_API_KEY);
        subscription = call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Movies>()
        {
            /**
             * Notifies the Observer that the {@link Observable} has finished sending push-based notifications.
             * <p/>
             * The {@link Observable} will not call this method if it calls {@link #onError}.
             */
            @Override
            public void onCompleted()
            {
                mLog.d("Receive Complete");
            }

            /**
             * Notifies the Observer that the {@link Observable} has experienced an error condition.
             * <p/>
             * If the {@link Observable} calls this method, it will not thereafter call {@link #onNext} or
             * {@link #onCompleted}.
             *
             * @param e the exception encountered by the Observable
             */
            @Override
            public void onError(Throwable e)
            {
                mLog.d("There was a error");
                loadingLayout.findViewById(R.id.progressBar).setVisibility(View.GONE);
                ((TextView)loadingLayout.findViewById(R.id.textView)).setText("There was an Error :-(");
                e.printStackTrace();
            }

            /**
             * Provides the Observer with a new item to observe.
             * <p/>
             * The {@link Observable} may call this method 0 or more times.
             * <p/>
             * The {@code Observable} will not call this method again after it calls either {@link #onCompleted} or
             * {@link #onError}.
             *
             * @param movies the item emitted by the Observable
             */
            @Override
            public void onNext(Movies movies)
            {
                if(mAdapter==null)
                {
                    mAdapter = new MoviesAdapter(movies.getResults());
                    moviesGrid.setAdapter(mAdapter);
                }
                else
                {
                    mAdapter.changeDataSet(movies.getResults());
                    mAdapter.notifyDataSetChanged();
                    //Reset the scroll to first item
                    moviesGrid.scrollToPosition(0);
                }
                moviesGrid.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
            }
        });
        /*.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Response<Movies> response, Retrofit retrofit) {

                int statusCode = response.code();
                mLog.d("StatusCode = "+statusCode);
                if(mAdapter==null)
                {
                    mAdapter = new MoviesAdapter(response.body().getResults());
                    moviesGrid.setAdapter(mAdapter);
                }
                else
                {
                    mAdapter.changeDataSet(response.body().getResults());
                    mAdapter.notifyDataSetChanged();
                    //Reset the scroll to first item
                    moviesGrid.scrollToPosition(0);
                }
                moviesGrid.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                mLog.e("Failed to Retrieve the List");
                t.printStackTrace();
            }
        });*/
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
    @Override
    protected void onDestroy()
    {
        this.subscription.unsubscribe();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}