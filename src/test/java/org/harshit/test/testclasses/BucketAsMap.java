package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class BucketAsMap extends BaseTest {

    @Test
    public void bucketsAsMap(){

        Mono<Void> then = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name","user:4:name")
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(then)
                .verifyComplete();

    }

}
