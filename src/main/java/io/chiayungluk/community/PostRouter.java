package io.chiayungluk.community;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class PostRouter {
    @Bean
    public RouterFunction<ServerResponse> route(PostHandler postHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/posts")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        postHandler::getPosts)
                .andRoute(RequestPredicates.POST("/posts")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        postHandler::addPost);

    }
}
