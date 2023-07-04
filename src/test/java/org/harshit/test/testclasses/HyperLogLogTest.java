package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class HyperLogLogTest extends BaseTest {

    @Test
    public void count(){
        //hyperloglog size 12.5kb
        RHyperLogLogReactive<Long> hyperLogLog = this.client.getHyperLogLog("user:visits", LongCodec.INSTANCE);
        List<Long> collect = LongStream.rangeClosed(1, 25000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> collect1 = LongStream.rangeClosed(25001, 50000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> collect2 = LongStream.rangeClosed(1, 5000)
                .boxed()
                .collect(Collectors.toList());
        Mono<Void> mono = Flux.just(collect, collect1, collect2)
                .flatMap(hyperLogLog::addAll)
                .then();
        StepVerifier.create(mono).verifyComplete();
        hyperLogLog.count().doOnNext(System.out::println).subscribe();
    }
}
