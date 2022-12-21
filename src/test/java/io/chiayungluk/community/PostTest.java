package io.chiayungluk.community;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostTest {
    public static final GenericContainer redis;

    static {
        redis = new GenericContainer(DockerImageName.parse("redis:7.0.5-bullseye"))
                .withExposedPorts(6379);
        redis.start();
    }

    @DynamicPropertySource
    public static void setRedisProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_ONE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJ1c2VybmFtZSI6ImpvaG4yIiwiaWQiOiI2MzgyYTIwOWY5MWM0M2FhMDdkYzJk" +
            "YjUiLCJpYXQiOjE2NzA0OTgyNTAsImV4cCI6MzE1NTI3MDQ5ODI1MH0.FncQ4jn3dsGJpuGyCcW-ZzUDNfdiJkSmgGGtWscuplQ";
    private static final String TOKEN_TWO = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJ1c2VybmFtZSI6ImpvaG4zIiwiaWQiOiI2M2E5OWU1NDVjMzM5NGRmN2E0ODU3ZTMiL" +
            "CJpYXQiOjE2NzIwNjA4OTQsImV4cCI6MTY3MjM3NjI1NH0.pAnv8dcnvWcyaVq8HVfGcaAPWN3MgT-hMGx5gORmbwI";
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testAddPost() {
        PostDto postDto = new PostDto();
        postDto.setPayload("testContent");
        webTestClient
                .post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + TOKEN_ONE)
                .bodyValue(postDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetPost() throws InterruptedException {
        for (int i = 0; i < 3; ++i) {
            PostDto postDto = new PostDto();
            postDto.setPayload("testContent" + i);
            webTestClient
                    .post().uri("/posts")
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + TOKEN_ONE)
                    .bodyValue(postDto)
                    .exchange()
                    .expectStatus().isOk();
        }
        webTestClient
                .get().uri("/posts?limit=3")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + TOKEN_TWO)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PostDto.class)
                .value(postDtos -> {
                    Assertions.assertEquals(3, postDtos.size());
                });
    }
}