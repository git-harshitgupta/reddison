package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class SortedSetTest extends BaseTest {

    @Test
    public void sortedTest(){
        RScoredSortedSetReactive<Object> scoredSortedSet = this.client.getScoredSortedSet("student:score", StringCodec.INSTANCE);
        //add score - if sam is not available it will create that and if present it will add the number to the exist one
        // vs
        //add - it simply replace or create the new one
        Mono<Void> then = scoredSortedSet.addScore("sam", 12)
                .then(scoredSortedSet.add(23, "mike"))
                .then(scoredSortedSet.addScore("jake", 10))
                .then();

        StepVerifier.create(then)
                .verifyComplete();

        scoredSortedSet.entryRange(0,1)
                .flatMapIterable(Function.identity())
                .map(se -> se.getScore()+":"+se.getValue())
                .doOnNext(System.out::println)
                .subscribe();

    }

}
