package com.yoanesber.rate_limit_with_redis.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yoanesber.rate_limit_with_redis.service.RateLimitService;
import com.yoanesber.rate_limit_with_redis.service.RedisService;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private static final String RATE_LIMIT_PREFIX = "rate-limit:";
    private final RedisService redisService;

    public RateLimitServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean isAllowed(String key, int maxRequests, long duration, TimeUnit unit) {
        Assert.notNull(key, "Key cannot be null");
        Assert.isTrue(maxRequests > 0, "Max requests must be greater than 0");
        Assert.isTrue(duration > 0, "Duration must be greater than 0");

        try {
            String rateLimitKey = RATE_LIMIT_PREFIX + key;

            // Create a new key if it does not exist
            if (!redisService.exists(rateLimitKey)) {
                redisService.set(rateLimitKey, 0, duration, unit);
            }

            // Increment the count
            Long count = redisService.increment(rateLimitKey, 1);

            // Reset the count after the duration has passed
            if (count == 1) {
                redisService.expire(rateLimitKey, duration, unit);
            }

            return count <= maxRequests;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
