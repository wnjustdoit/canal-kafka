package com.caiya.ck.booter.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka的属性配置类.
 *
 * @author wangnan
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {

    private Map<String, Object> producerConfig;

    private Map<String, Object> consumerConfig;

    private String canalTopic;


}
