//package ru.yandex.practicum.config;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import ru.yandex.practicum.SensorSnapshotDeserializer;
//import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
//
//import java.util.Properties;
//
//@Configuration
//@RequiredArgsConstructor
//public class KafkaConsumerSnapshotConfig {
//
//    @Value("${kafka.bootstrap.servers}")
//    private String bootstrapServers;
//
//    @Bean
//    public KafkaConsumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
//        Properties config = new Properties();
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-consumer");
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorSnapshotDeserializer.class);
//
//        return new KafkaConsumer<>(config);
//    }
//}
