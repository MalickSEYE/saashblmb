package com.mouride.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

// ── RabbitMQ Config ───────────────────────────────────────
@Configuration
class RabbitMqConfig {

    public static final String NOTIFICATION_EXCHANGE = "mouride.notifications";
    public static final String SMS_QUEUE     = "mouride.sms";
    public static final String EMAIL_QUEUE   = "mouride.email";
    public static final String WHATSAPP_QUEUE= "mouride.whatsapp";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue smsQueue() { return QueueBuilder.durable(SMS_QUEUE).build(); }

    @Bean
    public Queue emailQueue() { return QueueBuilder.durable(EMAIL_QUEUE).build(); }

    @Bean
    public Queue whatsappQueue() { return QueueBuilder.durable(WHATSAPP_QUEUE).build(); }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with("sms");
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationExchange).with("email");
    }

    @Bean
    public Binding whatsappBinding(Queue whatsappQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(whatsappQueue).to(notificationExchange).with("whatsapp");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate t = new RabbitTemplate(factory);
        t.setMessageConverter(converter);
        return t;
    }
}

// ── Redis Cache Config ────────────────────────────────────
@Configuration
@EnableCaching
class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withCacheConfiguration("membres",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
            .withCacheConfiguration("dahiras",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
            .withCacheConfiguration("dashboard",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(2)))
            .build();
    }
}
