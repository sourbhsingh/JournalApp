package practice.app.journalapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get an object from Redis cache.
     * Returns null if the key does not exist.
     */
    public <T> T get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.info("GET:REDIS Data Loaded from Redis Successfully");

            return (T) value; // safe because GenericJackson2JsonRedisSerializer handles conversion
        } catch (ClassCastException e) {
            log.error("Type mismatch when fetching key {} from Redis: {}", key, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error fetching key {} from Redis: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Set an object in Redis cache with TTL (time-to-live in seconds).
     */
    public void set(String key, Object value, Long ttlSeconds) {
        try {
            if (ttlSeconds != null && ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            log.info("SET:REDIS Data Loaded to Redis Successfully");

        } catch (Exception e) {
            log.error("Error setting key {} in Redis: {}", key, e.getMessage());
        }
    }

    /**
     * Delete a key from Redis.
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting key {} from Redis: {}", key, e.getMessage());
        }
    }
}
