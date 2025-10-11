package practice.app.journalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public <T> T get( String key, Class<T> entityClass){
        try {
            Object object = redisTemplate.opsForValue().get(key);
            if (object == null) return null;
            String json = object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            return objectMapper.readValue(json, entityClass);
        } catch (Exception e) {
            log.error("Exception occur at fetching cache",e);
            throw new RuntimeException(e);
        }
    }

    public void set(String key , Object o , Long ttl){
        try {

            String json = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Exception occur at setting cache",e);
            throw new RuntimeException(e);
        }
    }

}
