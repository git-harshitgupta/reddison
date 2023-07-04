package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

public class EventListenerTest extends BaseTest {

    @Test
    public void expireEventTest(){
        RBucketReactive<String> bucket =this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Sam",5, TimeUnit.SECONDS);
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        Mono<Void> then = bucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String s) {
                System.out.println("Expired key " + s);
            }
        }).then();
        StepVerifier.create(set.concatWith(get).concatWith(then))
                .verifyComplete();

        sleep(7000);
    }

    @Test
    public void deletedEventTest(){
        RBucketReactive<String> bucket =this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Sam");
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        Mono<Void> then = bucket.addListener(new DeletedObjectListener() {
            @Override
            public void onDeleted(String s) {
                System.out.println("Deleted key " + s);
            }
        }).then();
        StepVerifier.create(set.concatWith(get).concatWith(then))
                .verifyComplete();

        //key deleted manually from cli

        sleep(60000);
    }
}
