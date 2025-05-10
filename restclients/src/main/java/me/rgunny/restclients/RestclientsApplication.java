package me.rgunny.restclients;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestclientsApplication {

    @Bean
    ApplicationRunner init() {
        return args -> {
            MyRestClients myRestClients = new MyRestClients();
            myRestClients.myRestTemplate();
            myRestClients.myWebClient();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(RestclientsApplication.class, args);
    }

}
