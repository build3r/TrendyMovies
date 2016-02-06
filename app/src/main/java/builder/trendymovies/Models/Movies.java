
package builder.trendymovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Movies {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    /**
     * 
     * @return
     *     The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * 
     * @param page
     *     The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     * 
     * @param totalResults
     *     The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    /**
     * 
     * @return
     *     The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * 
     * @param totalPages
     *     The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    public static class Result implements Parcelable,Serializable
    {

        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        @SerializedName("adult")
        @Expose
        private Boolean adult;
        @SerializedName("overview")
        @Expose
        private String overview;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds = new ArrayList<Integer>();
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("original_title")
        @Expose
        private String originalTitle;
        @SerializedName("original_language")
        @Expose
        private String originalLanguage;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        @SerializedName("popularity")
        @Expose
        private Double popularity;
        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;
        @SerializedName("video")
        @Expose
        private Boolean video;
        @SerializedName("vote_average")
        @Expose
        private Double voteAverage;
        @SerializedName("reviews")
        @Expose
        private List<Reviews.Result> mReviews;

        @SerializedName("trailers")
        @Expose
        private  List<Trailers.Result> mTrailers;

        public  List<Reviews.Result> getReviews()
        {
            return mReviews;
        }

        public void setReviews( List<Reviews.Result> mReviews)
        {
            this.mReviews = mReviews;
        }

        public List<Trailers.Result> getTrailers()
        {
            return mTrailers;
        }

        public void setTrailers(List<Trailers.Result> mTrailers)
        {
            this.mTrailers = mTrailers;
        }
        public Result(String posterPath, Boolean adult, String overview, String releaseDate, List<Integer> genreIds, Integer id, String originalTitle, String originalLanguage, String title, String backdropPath, Double popularity, Integer voteCount, Boolean video, Double voteAverage)
        {
            this.posterPath = posterPath;
            this.adult = adult;
            this.overview = overview;
            this.releaseDate = releaseDate;
            this.genreIds = genreIds;
            this.id = id;
            this.originalTitle = originalTitle;
            this.originalLanguage = originalLanguage;
            this.title = title;
            this.backdropPath = backdropPath;
            this.popularity = popularity;
            this.voteCount = voteCount;
            this.video = video;
            this.voteAverage = voteAverage;
        }

        protected Result(Parcel in)
        {
            this.posterPath = in.readString();
            this.adult = in.readByte() != 0;
            this.overview = in.readString();
            this.releaseDate = in.readString();
            this.id = in.readInt();
            this.originalTitle = in.readString();
            this.originalLanguage = in.readString();
            this.title = in.readString();
            this.backdropPath =in.readString();
            this.popularity = in.readDouble();
            this.voteCount = in.readInt();
            this.video = in.readByte() != 0;
            this.voteAverage = in.readDouble();

        }

        public static final Creator<Result> CREATOR = new Creator<Result>()
        {
            @Override
            public Result createFromParcel(Parcel in)
            {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size)
            {
                return new Result[size];
            }
        };

        /**
         *
         * @return
         *     The posterPath
         */
        public String getPosterPath() {
            return posterPath;
        }

        /**
         *
         * @param posterPath
         *     The poster_path
         */
        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        /**
         *
         * @return
         *     The adult
         */
        public Boolean getAdult() {
            return adult;
        }

        /**
         *
         * @param adult
         *     The adult
         */
        public void setAdult(Boolean adult) {
            this.adult = adult;
        }

        /**
         *
         * @return
         *     The overview
         */
        public String getOverview() {
            return overview;
        }

        /**
         *
         * @param overview
         *     The overview
         */
        public void setOverview(String overview) {
            this.overview = overview;
        }

        /**
         *
         * @return
         *     The releaseDate
         */
        public String getReleaseDate() {
            return releaseDate;
        }

        /**
         *
         * @param releaseDate
         *     The release_date
         */
        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        /**
         *
         * @return
         *     The genreIds
         */
        public List<Integer> getGenreIds() {
            return genreIds;
        }

        /**
         *
         * @param genreIds
         *     The genre_ids
         */
        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        /**
         *
         * @return
         *     The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         *     The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         *
         * @return
         *     The originalTitle
         */
        public String getOriginalTitle() {
            return originalTitle;
        }

        /**
         *
         * @param originalTitle
         *     The original_title
         */
        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        /**
         *
         * @return
         *     The originalLanguage
         */
        public String getOriginalLanguage() {
            return originalLanguage;
        }

        /**
         *
         * @param originalLanguage
         *     The original_language
         */
        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        /**
         *
         * @return
         *     The title
         */
        public String getTitle() {
            return title;
        }

        /**
         *
         * @param title
         *     The title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         *
         * @return
         *     The backdropPath
         */
        public String getBackdropPath() {
            return backdropPath;
        }

        /**
         *
         * @param backdropPath
         *     The backdrop_path
         */
        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        /**
         *
         * @return
         *     The popularity
         */
        public Double getPopularity() {
            return popularity;
        }

        /**
         *
         * @param popularity
         *     The popularity
         */
        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }

        /**
         *
         * @return
         *     The voteCount
         */
        public Integer getVoteCount() {
            return voteCount;
        }

        /**
         *
         * @param voteCount
         *     The vote_count
         */
        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }

        /**
         *
         * @return
         *     The video
         */
        public Boolean getVideo() {
            return video;
        }

        /**
         *
         * @param video
         *     The video
         */
        public void setVideo(Boolean video) {
            this.video = video;
        }

        /**
         *
         * @return
         *     The voteAverage
         */
        public Double getVoteAverage() {
            return voteAverage;
        }

        /**
         *
         * @param voteAverage
         *     The vote_average
         */
        public void setVoteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
        }

        /**
         * Describe the kinds of special objects contained in this Parcelable's
         * marshalled representation.
         *
         * @return a bitmask indicating the set of special object types marshalled
         * by the Parcelable.
         */
        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public String toString()
        {
            return "Result{" +
                    "posterPath='" + posterPath + '\'' +
                    ", adult=" + adult +
                    ", overview='" + overview + '\'' +
                    ", releaseDate='" + releaseDate + '\'' +
                    ", genreIds=" + genreIds +
                    ", id=" + id +
                    ", originalTitle='" + originalTitle + '\'' +
                    ", originalLanguage='" + originalLanguage + '\'' +
                    ", title='" + title + '\'' +
                    ", backdropPath='" + backdropPath + '\'' +
                    ", popularity=" + popularity +
                    ", voteCount=" + voteCount +
                    ", video=" + video +
                    ", voteAverage=" + voteAverage +
                    '}';
        }

        /**
         * Flatten this object in to a Parcel.
         *
         * @param dest  The Parcel in which the object should be written.
         * @param flags Additional flags about how the object should be written.
         *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
         */
        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(posterPath);
            //Hack for writing boolean to parcel
            dest.writeByte((byte) (adult ? 1 : 0));
            dest.writeString(overview);
            dest.writeString(releaseDate);
            dest.writeInt(id);
            dest.writeString(originalTitle);
            dest.writeString(originalLanguage);
            dest.writeString(title);
            dest.writeString(backdropPath);
            dest.writeDouble(popularity);
            dest.writeInt(voteCount);
            dest.writeByte((byte) (video ? 1 : 0));
            dest.writeDouble(voteAverage);

        }
    }
}
