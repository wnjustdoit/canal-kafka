package com.caiya.ck.booter.configuration;

import com.alibaba.otter.canal.protocol.Message;
import com.caiya.ck.common.kafka.MessageDeserializer;
import com.caiya.ck.common.kafka.MessageSerializer;
import com.caiya.ck.booter.component.KafkaProperties;
import com.caiya.kafka.*;
import com.caiya.kafka.spring.annotation.EnableKafka;
import com.caiya.kafka.spring.config.ConcurrentKafkaListenerContainerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Kafka相关配置.
 *
 * @author wangnan
 * @since 1.0
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Resource
    private KafkaProperties kafkaProperties;


    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.getProducerConfig(),
                new StringSerializer(), new MessageSerializer());
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        KafkaTemplate<String, Message> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaProperties.getCanalTopic());
        return kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, Message> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.getConsumerConfig(),
                new StringDeserializer(), new MessageDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


}
