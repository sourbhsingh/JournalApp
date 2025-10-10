package practice.app.journalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisService {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public <T> T get( String key, Class<T> entityclass){
        try {
            Object object = redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(object.toString(), entityclass);
        } catch (Exception e) {
            log.info("Exception occur at fetching cache",e);
            throw new RuntimeException(e);
        }
    }

    public void set(String key , Object o , Long ttl){
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,json);
        } catch (Exception e) {
            log.info("Exception occur at setting cache",e);
            throw new RuntimeException(e);
        }
    }

}
