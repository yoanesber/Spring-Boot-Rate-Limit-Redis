package com.yoanesber.rate_limit_with_redis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yoanesber.rate_limit_with_redis.service.RedisService;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final ObjectMapper objectMapper; // Jackson for JSON serialization
    private final RedisTemplate<String, Object> redisTemplate; // Redis template for Redis operations

    public RedisServiceImpl(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        Assert.notNull(key, "Key cannot be null");
        Assert.notNull(value, "Value cannot be null");

        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }

            return true;
        } catch (Exception e) {
            log.error("An error occurred while setting key: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean setList(String key, List<?> list, long timeout, TimeUnit unit) {
        Assert.notNull(key, "Key cannot be null");
        Assert.notNull(list, "List cannot be null");

        try {
            redisTemplate.delete(key);
            redisTemplate.opsForList().rightPushAll(key, list.toArray());
            return true;
        } catch (Exception e) {
            log.error("An error occurred while setting list: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Object get(String key, Class<?> clazz) {
        Assert.notNull(key, "Key cannot be null");

        try {
            return objectMapper.convertValue(redisTemplate.opsForValue().get(key), clazz);
        } catch (Exception e) {
            log.error("An error occurred while getting key: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        Assert.notNull(key, "Key cannot be null");
        Assert.notNull(clazz, "Class cannot be null");

        try {
            return redisTemplate.opsForList().range(key, 0, -1)
                .stream()
                .map(o -> objectMapper.convertValue(o, clazz))
                .toList();
        } catch (Exception e) {
            log.error("An error occurred while getting list: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<String> getKeysContaining(String pattern) {
        Assert.notNull(pattern, "Pattern cannot be null");

        try {
            return redisTemplate.keys("*" + pattern + "*");
        } catch (Exception e) {
            log.error("An error occurred while getting keys containing: {}", pattern, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String key) {
        Assert.notNull(key, "Key cannot be null");

        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("An error occurred while deleting key: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        Assert.notNull(key, "Key cannot be null");

        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("An error occurred while checking if key exists: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        Assert.notNull(key, "Key cannot be null");

        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("An error occurred while setting expiration time for key: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long increment(String key, long value) {
        Assert.notNull(key, "Key cannot be null");

        try {
            return redisTemplate.opsForValue().increment(key, value);
        } catch (Exception e) {
            log.error("An error occurred while incrementing key: {}", key, e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
