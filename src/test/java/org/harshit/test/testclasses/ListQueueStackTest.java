package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ListQueueStackTest extends BaseTest {

    @Test
    public void listTest(){

        RListReactive<Object> list = this.client.getList("number-input", LongCodec.INSTANCE);

        List<Long> collect = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());



//this is going to be async call so the 1 to 10 might get inserted in order
        // since all the 10 calls are going to redis at once
//        Mono<Void> then = Flux.range(1, 10)
//                .map(Long::valueOf)
//                .flatMap(list::add)
//                .then();
//        StepVerifier.create(then).verifyComplete();
        StepVerifier.create(list.addAll(collect).then()).verifyComplete();
        StepVerifier.create(list.size()).expectNext(10).verifyComplete();
    }

    @Test
    public void queueTest(){

        RQueueReactive<Object> queue = this.client.getQueue("number-input", LongCodec.INSTANCE);

        Mono<Void> queuePoll = queue.poll().repeat(3)
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(queuePoll).verifyComplete();
        StepVerifier.create(queue.size()).expectNext(8).verifyComplete();
    }

}
