package edu.universidad.trading.feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FeedProperties.class)
public class FeedApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeedApplication.class, args);
    }
}
