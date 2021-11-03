package com.learnkafka.libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Example of how to create a topic by code, using kafka clients admin library
 * It's not recommended using this tools in production environments
 */
@Configuration
@Profile("local")
public class AutoCreateConfig {
    @Value("${topic.name}")
    private String topicName;

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
