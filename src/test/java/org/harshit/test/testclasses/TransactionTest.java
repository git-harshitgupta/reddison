package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TransactionTest extends BaseTest {

    RBucketReactive<Long> userBalance1;
    RBucketReactive<Long> userBalance2;

    @BeforeAll
    public void accountSetup(){
        userBalance1 = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        userBalance2 = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> mono = userBalance1.set(100L)
                .then(userBalance2.set(0L))
                .then();
        StepVerifier.create(mono).verifyComplete();
    }

    @AfterAll
    public void accountBalanceStatus(){
        Mono<Void> then = Flux.zip(this.userBalance1.get(), this.userBalance2.get())
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(then).verifyComplete();
    }


    @Test
    public void nonTransactionTest(){
        this.transfer(userBalance1,userBalance2,50)
                .thenReturn(0)
                .map(i -> (5/i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(1000);
    }

    @Test
    public void transactionTest(){
        RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);
        this.transfer(user1Balance,user2Balance,50)
                .thenReturn(0)
               // .map(i -> (5/i))
                .then(transaction.commit())
                .doOnError(System.out::println)
                .onErrorResume(ex -> transaction.rollback())
                .subscribe();
        sleep(1000);
    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount , RBucketReactive<Long> toAccount , int ammount){
        return Flux.zip(fromAccount.get(),toAccount.get())
                .filter(t->t.getT1()>=ammount)
                .flatMap(t -> fromAccount.set(t.getT1() - ammount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2()+ammount))
                .then();
    }

}
