package sakhno.psup.product_service.config.redis;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import sakhno.psup.product_service.dto.category.CategoryDto;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, CategoryDto> categoryRedisTemplate(
            ReactiveRedisConnectionFactory factory, @Qualifier("objectMapperForRedis") ObjectMapper objectMapperForRedis) {

        Jackson2JsonRedisSerializer<CategoryDto> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapperForRedis, CategoryDto.class);
        RedisSerializationContext<String, CategoryDto> context =
                RedisSerializationContext.<String, CategoryDto>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, List<CategoryDto>> categoriesRedisTemplate(
            ReactiveRedisConnectionFactory factory, @Qualifier("objectMapperForRedis") ObjectMapper objectMapperForRedis) {

        JavaType type = objectMapperForRedis.getTypeFactory()
                .constructCollectionType(List.class, CategoryDto.class);
        Jackson2JsonRedisSerializer<List<CategoryDto>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapperForRedis, type);
        RedisSerializationContext<String, List<CategoryDto>> context =
                RedisSerializationContext.<String, List<CategoryDto>>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean("objectMapperForRedis")
    public ObjectMapper objectMapperForRedis() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
