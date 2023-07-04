package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class MessageQueueTest extends BaseTest {
    private RBlockingQueueReactive<Object> messageQueue;
    @BeforeAll
    public void setupQueue(){
        messageQueue = this.client.getBlockingQueue("message-queue", LongCodec.INSTANCE);
    }

    @Test
    public void consumer1(){
        this.messageQueue.takeElements()
                .doOnNext( i -> System.out.println("Received message "+i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void consumer2(){
        this.messageQueue.takeElements()
                .doOnNext( i -> System.out.println("Received message "+i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void producer(){
        Mono<Void> then = Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println("Pushed =" + i))
                .flatMap(i -> this.messageQueue.add(Long.valueOf(i)))
                .then();

        StepVerifier.create(then).verifyComplete();
    }



}
