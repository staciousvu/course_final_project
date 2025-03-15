package com.example.courseapplicationproject.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RedisService {
    RedisTemplate<String, Object> template;

    public void save(String key, Object value) {
        template.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = template.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    public void delete(String key) {
        template.delete(key);
    }
}
