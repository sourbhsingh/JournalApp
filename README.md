//This is Readme.md file
## Redis caching used in this project

This project uses Redis as a distributed cache integrated with Spring Boot to reduce database load and improve response times.

1. Integration:
   1. Dependency: `spring-boot-starter-data-redis` (Lettuce).
   2. Enable caching by adding `@EnableCaching` on a configuration class.
2. Configuration:
   1. Set `spring.redis.host` and `spring.redis.port` in `application.properties` or `application.yml`.
   2. Define a `RedisCacheManager` bean with serializers and a default TTL.
3. Usage:
   1. Cache read methods with `@Cacheable(value = "items", key = "#id")`.
   2. Use `@CachePut` to update and `@CacheEvict` to remove entries.
4. Notes:
   1. Use `StringRedaisSerializer` fssor keys and `Jackson2JsonRedisSerializer` for values.
   2. Configure sensible TTLs and monitor hit/miss rates.
   3. Mitigate cache stampede with locking or request coalescing when needed.
5. Working: 
