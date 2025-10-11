package practice.app.journalapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisServiceTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testSet(){
        redisTemplate.opsForValue().set("Car","Maruti" );
    }

    @Test
    public void testGet(){
      Object obj=  redisTemplate.opsForValue().get("Car");

      System.out.print(obj.toString());
    }
}
