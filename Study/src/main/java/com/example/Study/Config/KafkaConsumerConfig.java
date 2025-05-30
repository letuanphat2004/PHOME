package com.example.Study.Config;


import com.example.Study.Model.DTO.EmailDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Cấu hình số lần retry và thời gian chờ giữa các lần retry
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new FixedBackOff(1000L, 3L));  // Retry tối đa 3 lần, mỗi lần cách nhau 1 giây

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, EmailDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Sử dụng ErrorHandlingDeserializer cho key và value
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Chỉ định lớp giải mã cho các lỗi deserialization
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());

        // Cấu hình JsonDeserializer cho EmailDTO
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.btl_oop.model.dto");

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
