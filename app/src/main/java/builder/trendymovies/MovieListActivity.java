package builder.trendymovies;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import builder.trendymovies.Adapters.MoviesAdapter;
import builder.trendymovies.Interfaces.MoviesInterface;
import builder.trendymovies.Models.Movies;
import builder.trendymovies.Utils.BuilderLogger;
import builder.trendymovies.Utils.Constants;
import builder.trendymovies.Utils.Helper;
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
 * An activity representing a list of MyMovies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    @Bind(R.id.movies_grid) RecyclerView moviesGrid;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.loading_layout)
    RelativeLayout loadingLayout;
    final String SORT_POPULAR = "popularity.desc";
    //vote_average doesn't seems to be a good metric, movies with 1 vote of 10 are list on the top :-|
    final String SORT_VOTE = "vote_count.desc";
    final String SORT_FAVORITE = "FAVORITE";
    @State
    String CURRENT_SORT = "popularity.desc";
    RecyclerView.LayoutManager mLayoutManager;
    BuilderLogger mLog = new BuilderLogger(MovieListActivity.class.getSimpleName());
    Toast sortType ;
    Retrofit retrofit;
    MoviesInterface apiService;
    MoviesAdapter mAdapter;
    private Subscription subscription;
    AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_mymovie_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Trendy Movies-Popular");
        sortType = new Toast(this);
        mLayoutManager = new GridLayoutManager(this, 2);
        moviesGrid.setLayoutManager(mLayoutManager);
        initializeRetrofit();
        if(Helper.isNetworkConnected())
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
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MovieListActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View mView = inflater.inflate(R.layout.movie_selction, null);
                alertBuilder.setView(mView);

                // Set an EditText view to get user input

                alert = alertBuilder.create();

                RadioGroup rg = (RadioGroup) mView.findViewById(R.id.radio_group);
                switch (CURRENT_SORT)
                {
                    case SORT_VOTE:
                        ((RadioButton) mView.findViewById(R.id.vote_count)).setChecked(true);
                        break;

                    case SORT_POPULAR:

                        ((RadioButton) mView.findViewById(R.id.popularity)).setChecked(true);
                        break;
                    case SORT_FAVORITE:

                        ((RadioButton) mView.findViewById(R.id.sort_favorites)).setChecked(true);
                        break;
                }
                rg.setOnCheckedChangeListener(MovieListActivity.this);
                alert.show();            }
        });
        View recyclerView = findViewById(R.id.movies_grid);
        assert recyclerView != null;

        if (findViewById(R.id.mymovie_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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
                    mAdapter = new MoviesAdapter(movies.getResults(),mTwoPane,MovieListActivity.this);
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

    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
    @Override
    protected void onDestroy()
    {
        if(this.subscription!=null)
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


        return super.onOptionsItemSelected(item);
    }


    /**
     * <p>Called when the checked radio button has changed. When the
     * selection is cleared, checkedId is -1.</p>
     *
     * @param group     the group in which the checked radio button has changed
     * @param checkedId the unique identifier of the newly checked radio button
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        switch (checkedId)
        {
            case R.id.vote_count:
                CURRENT_SORT = SORT_VOTE;
                populateMovies(CURRENT_SORT);
                break;

            case R.id.popularity:
                CURRENT_SORT = SORT_POPULAR;
                populateMovies(CURRENT_SORT);
                break;
            case R.id.sort_favorites:
                populateFavMovies();
                CURRENT_SORT = SORT_FAVORITE;
                break;
        }
        alert.dismiss();
    }

    private void populateFavMovies()
    {
        SharedPreferences mPreferences = getSharedPreferences(Constants.OFFLINE_PREF_KEY,MODE_PRIVATE);
        Map<String, ?> prefsMap = mPreferences.getAll();
        int i=1;
        List<Movies.Result> moviesList = new ArrayList<>();
        Gson gson = new Gson();
        for (Map.Entry<String, ?> entry: prefsMap.entrySet())
        {
            mLog.d((i++)+" : "+ entry.getKey() + ":" + entry.getValue().toString());
            moviesList.add(gson.fromJson(entry.getValue().toString(),Movies.Result.class));
        }
        mLog.d(moviesList.toString());
        if(moviesList.size()>0)
        {
            if (mAdapter == null)
            {
                mAdapter = new MoviesAdapter(moviesList,mTwoPane,MovieListActivity.this);
                moviesGrid.setAdapter(mAdapter);
            } else
            {
                mAdapter.changeDataSet(moviesList);
                mAdapter.notifyDataSetChanged();
                //Reset the scroll to first item
                moviesGrid.scrollToPosition(0);
            }
            moviesGrid.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }
        else
        {
            Toast.makeText(this,"No favorites yet",Toast.LENGTH_LONG).show();
        }
    }
}
