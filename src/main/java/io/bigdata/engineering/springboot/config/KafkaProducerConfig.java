package io.bigdata.engineering.springboot.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;




@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.properties.bootstrap.servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        //Kafka Producer의 Configuration
        //application.properties에서도 설정이 가능하다.
        configProps.put(
                "sasl.mechanism", "PLAIN");
        configProps.put(
                "bootstrap.servers", "pkc-gq2xn.asia-northeast3.gcp.confluent.cloud:9092");
        configProps.put(
                "sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule" +
                "   required username='5STFBFJ4USRS636C' " +
                "  password='81Xoad9PXAV8Eg2QsR8UdcV+A4SaCeAUI0U9iUQpWewyx6+PHJkwIeOuSmW3KvSJ';");
        configProps.put(
                "security.protocol", "SASL_SSL");
//        configProps.put(RETRIES_CONFIG,
//                0);
//        configProps.put(BUFFER_MEMORY_CONFIG,
//                33554432);
        // Serializer: 객체를 바이트 단위의 스트림으로 변환시키는 기능을 한다.
        // 데이터의 조건에 따라 수정해주어야 한다.
        // ex) spring,kafka.producer.key-serializer=org.apache.kafka.common.serialization.IntegerSerializer
        configProps.put(
                "key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        configProps.put(
                "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}