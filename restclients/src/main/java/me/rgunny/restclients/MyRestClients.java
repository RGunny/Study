package me.rgunny.restclients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@Slf4j
public class MyRestClients {

    private static final String API_KEY = "09a8d7cf7081c0498661adccde4477fd";
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static final String LANGUAGE = "ko-KR";

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final WebClient webClient = WebClient.create();


    public void myRestTemplate() {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        URI uri = defaultUriBuilderFactory.builder()
                .path(API_URL + "/movie/popular")
                .queryParam("api_key", API_KEY)
                .queryParam("language", LANGUAGE)
                .queryParam("page", 1)
                .build();
        System.out.println("uri = " + uri);
        PopularMovieResponse result = restTemplate.getForObject(uri, PopularMovieResponse.class);

//        PopularMovieResponse result = restTemplate.getForObject(API_URL + API_KEY, PopularMovieResponse.class);

        log.info("MyRestClients.myRestTemplate");
        log.info("result = ", result);
    }

    public void myWebClient() {
        PopularMovieResponse result = webClient.mutate()
                .baseUrl(API_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/movie/popular")
                        .queryParam("api_key", API_KEY)
                        .queryParam("language", LANGUAGE)
                        .queryParam("page", 1).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PopularMovieResponse.class)
                .block();

        log.info("MyRestClients.myWebClient");
        log.info("result = ", result);
    }

}


