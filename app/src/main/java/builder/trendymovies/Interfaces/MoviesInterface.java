package builder.trendymovies.Interfaces;

import builder.trendymovies.Models.Movies;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Shabaz on 19-Dec-15.
 */
public interface MoviesInterface
{
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter
    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=d3fa04813f90375f6e05bac82b0c6ba9
    @GET("/3/discover/movie")
    Observable<Movies> getMoviesList(@Query("sort_by") String sort, @Query("api_key") String api_key);


}
