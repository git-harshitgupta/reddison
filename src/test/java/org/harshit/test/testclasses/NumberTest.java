package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class NumberTest extends BaseTest {

    @Test
    public void keyValueIncreaseTest(){

        RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:1:visitCount");
        Mono<Void> then = Flux.range(1, 300)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(i -> atomicLong.incrementAndGet())
                .then();

        StepVerifier.create(then)
                .verifyComplete();
    }

}
