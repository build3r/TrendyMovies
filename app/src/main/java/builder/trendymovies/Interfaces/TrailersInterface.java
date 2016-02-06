package builder.trendymovies.Interfaces;

import builder.trendymovies.Models.Trailers;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Shabaz on 06-Feb-16.
 */
public interface TrailersInterface
{
    @GET("/3/movie/{id}/videos")
    Observable<Trailers> getTrailers(@Path("id") String movieId, @Query("api_key") String api_key);
}
