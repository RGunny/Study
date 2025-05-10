package me.rgunny.restclients;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PopularMovieResponse {

    private int page;
    private List<PopularMovie> results;
    private int totalPages;
    private int totalResults;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PopularMovie {
        private boolean adult;
        private String backdropPath;
        private List<Integer> genreIds;
        private int id;
        private String originalLanguage;
        private String originalTitle;
        private String overview;
        private Double popularity;
        private String posterPath;
        private String releaseDate;
        private String title;
        private boolean video;
        private Double voteAverage;
        private int voteCount;
    }
}
