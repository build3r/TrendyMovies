package builder.trendymovies.Interfaces;

import builder.trendymovies.Models.Reviews;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Shabaz on 06-Feb-16.
 */
public interface ReviewsInterface
{
    @GET("/3/movie/{id}/reviews")
    Observable<Reviews> getReviews(@Path("id") String movieId, @Query("api_key") String api_key);
}
