package io.bigdata.engineering.springboot.producer;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Component
public class KafkaProducer {

    //Constructor Dependency Injection
    private final KafkaTemplate<Integer, String> kafkaTemplate;


    //Field Dependency Injection
    //@Autowired
    //private KafkaTemplate<Integer, String> template;
    //Field Dependency Injection은 사용이 추천되지 않는 의존성 주입 방식이다.
    //Field Dependency Injection으로 주입된 객체는 Immutable(불변)한 상태를 만들 수 없다.
    //Field Dependency Injection으로 주입된 객체는 DI 컨테이너와 강한 결합이 발생한다.

    //메시지를 생성해주는 JavaFaker 객체
    Faker faker;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {


        faker = Faker.instance();
        //정해진 매 시간마다 메시지를 발생시키는 Kafka Publisher의 flux 객체
        //flux의 시간 간격
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));
        //flux의 메시지
        final Flux<String> quotesFlux = Flux.fromStream(Stream.generate(
                () -> faker.hobbit().quote() //호빗 대사 생성
        ));

        /*
        //두 Flux를 하나로로 합치기
       Flux.zip(interval, quotesFlux).map(new Function<Tuple2<Long, String>, Object>() {
            @Override
            public Object apply(Tuple2<Long, String> objects) {
                return template.send("hobbit", faker.random().nextInt(23), objects.getT2());
            }
        }).blockLast();
        */
        // This long long code goes into...
        Flux.zip(interval, quotesFlux)
                .map(it -> kafkaTemplate.send("hobbit", faker.random().nextInt(23), it.getT2()))
                .blockLast();

    }
}
