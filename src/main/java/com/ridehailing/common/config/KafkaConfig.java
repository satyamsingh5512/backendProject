package com.ridehailing.common.config;

import com.ridehailing.common.util.Constants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic tripRequestedTopic() {
        return TopicBuilder.name(Constants.TOPIC_TRIP_REQUESTED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tripAcceptedTopic() {
        return TopicBuilder.name(Constants.TOPIC_TRIP_ACCEPTED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tripStartedTopic() {
        return TopicBuilder.name(Constants.TOPIC_TRIP_STARTED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tripCompletedTopic() {
        return TopicBuilder.name(Constants.TOPIC_TRIP_COMPLETED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
