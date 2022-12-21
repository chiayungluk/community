package io.chiayungluk.community;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.TimeZone;

@Repository
public class UserLimitRepository {
    private final String postCountKey;
    private final int maxPostCount;

    private final ReactiveRedisOperations<String, String> userLimitsOps;

    public UserLimitRepository(ReactiveRedisOperations<String, String> userLimitsOps, AppConfig appConfig) {
        this.userLimitsOps = userLimitsOps;
        this.postCountKey = appConfig.getPostsCountKey();
        this.maxPostCount = appConfig.getMaxPostsCount();
    }

    public Mono<Boolean> refreshLimit(String userId, long delta) {
        String key = postCountKey + ":" + userId + ":" + getZeroOfToday();
        return userLimitsOps.opsForValue().setIfAbsent(key, maxPostCount + "")
                .then(userLimitsOps.expireAt(key, Instant.ofEpochMilli(getZeroOfNextDay())))
                .then(userLimitsOps.opsForValue().decrement(key, delta))
                .map(l -> l > 0);
    }

    private long getZeroOfToday() {
        return System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
    }

    private long getZeroOfNextDay() {
        return getZeroOfToday() + 24 * 60 * 60 * 1000 - 1;

    }

}
