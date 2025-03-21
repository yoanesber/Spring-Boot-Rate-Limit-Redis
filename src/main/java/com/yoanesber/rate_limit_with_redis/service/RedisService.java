package com.yoanesber.rate_limit_with_redis.service;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Set;

public interface RedisService {
    // Save a key-value pair to Redis with an optional expiration time
    boolean set(String key, Object value, long timeout, TimeUnit unit);

    // Save a list to Redis with an optional expiration time
    boolean setList(String key, List<?> list, long timeout, TimeUnit unit);

    // Retrieve a value from Redis by key
    Object get(String key, Class<?> clazz);

    // Retrieve a list from Redis by key
    <T> List<T> getList(String key, Class<T> clazz);

    // Retrieve all keys from Redis by pattern
    Set<String> getKeysContaining(String pattern);

    // Delete a key from Redis
    boolean delete(String key);

    // Check if a key exists in Redis
    boolean exists(String key);

    // Set an expiration time for a key in Redis
    void expire(String key, long timeout, TimeUnit unit);

    // Increment a key in Redis by a value
    Long increment(String key, long value);
}
