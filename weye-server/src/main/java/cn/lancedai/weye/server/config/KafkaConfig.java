package cn.lancedai.weye.server.config;

import cn.lancedai.weye.common.model.record.BaseRecord;
import lombok.val;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    private final ServerConfig serverConfig;

    public KafkaConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Bean
    public KafkaTemplate<String, BaseRecord> recordKafkaSender(KafkaProperties properties) {
        val producerProps = properties.getProducer().buildProperties();
        // display
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverConfig.kafkaBootstrapServers);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps), false);
    }
}
