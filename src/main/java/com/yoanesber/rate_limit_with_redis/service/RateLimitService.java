package com.yoanesber.rate_limit_with_redis.service;

import java.util.concurrent.TimeUnit;

public interface RateLimitService {
    // Check if a key is allowed to make a request
    boolean isAllowed(String key, int maxRequests, long duration, TimeUnit unit);
}
