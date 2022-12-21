package io.chiayungluk.community;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private String postsKey = "posts";
    private Duration postDuration = Duration.ofDays(7) ;
    private String postsCountKey = "postCount";
    private int maxPostsCount = 100;
    private String jwtSecret = "sdfjsf";
}
