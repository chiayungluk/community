package io.chiayungluk.community;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class PostRepository {

    private final String postsKey;
    private final long postExpiresInSec;
    private final ReactiveRedisOperations<String, PostDto> postOps;

    public PostRepository(ReactiveRedisOperations<String, PostDto> postOps, AppConfig appConfig) {
        this.postOps = postOps;
        this.postExpiresInSec = appConfig.getPostDuration().toSeconds();
        this.postsKey = appConfig.getPostsKey();
    }

    public Mono<Boolean> addPost(PostDto postDto) {
        return removeExpiredPosts()
                .then(postOps.opsForZSet().add(postsKey, postDto, postDto.getTimestamp()));
    }

    public Flux<PostDto> getPosts(String excludedUserId, int limit) {
        return removeExpiredPosts()
                .thenMany(postOps.opsForZSet().randomMembers(postsKey, limit)
                        .distinct()
                        .doOnNext(postDto -> {
                            System.out.println(postDto);
                        })
                        .filter(postDto -> !postDto.getUserId().equals(excludedUserId)));
    }

    private Mono<Long> removeExpiredPosts() {
        return postOps.opsForZSet().removeRangeByScore(postsKey,
                Range.leftUnbounded(Range.Bound.inclusive(
                        1.0 * Duration.ofMillis(System.currentTimeMillis()).toSeconds() - postExpiresInSec)));
    }
}
