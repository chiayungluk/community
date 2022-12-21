package io.chiayungluk.community;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class PostHandler {

    private final PostRepository postRepository;
    private final UserLimitRepository userLimitRepository;

    public PostHandler(PostRepository postRepository, UserLimitRepository userLimitRepository) {
        this.postRepository = postRepository;
        this.userLimitRepository = userLimitRepository;
    }

    public Mono<ServerResponse> getPosts(ServerRequest request) {
        int limit = Integer.parseInt(request.queryParam("limit").orElse("10"));
        return getUser(request)
                .flatMap(userDto -> refreshLimit(userDto.getId(), 1).mapNotNull(r -> r ? userDto : null))
                .flatMap(userDto -> {
                    if (userDto != null) {
                        return postRepository.getPosts(userDto.getId(), limit)
                                .collectList()
                                .flatMap(postDtos -> ServerResponse.ok().bodyValue(postDtos));
                    } else {
                        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build();
                    }
                });

    }

    public Mono<ServerResponse> addPost(ServerRequest request) {
        return getUser(request)
                .flatMap(userDto -> refreshLimit(userDto.getId(), 1).map(r -> userDto))
                .flatMap(userDto -> request.bodyToMono(PostDto.class)
                        .map(postDto -> {
                            postDto.setId(UUID.randomUUID().toString());
                            postDto.setUserId(userDto.getId());
                            postDto.setUsername(userDto.getUsername());
                            postDto.setTimestamp(System.currentTimeMillis());
                            return postDto;
                        })
                        .flatMap(postRepository::addPost)
                        .flatMap(r -> r != null ? ServerResponse.ok().build()
                                : ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    private Mono<Boolean> refreshLimit(String userId, long delta) {
        return userLimitRepository.refreshLimit(userId, delta);
    }

    private Mono<UserDto> getUser(ServerRequest request) {
        return request.principal()
                .map(principal -> {
                    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
                    return new UserDto((String) token.getPrincipal(), (String) token.getCredentials());
                });
    }

}
