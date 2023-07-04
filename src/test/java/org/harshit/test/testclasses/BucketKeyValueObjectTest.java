package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.harshit.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Array;
import java.util.Arrays;

public class BucketKeyValueObjectTest extends BaseTest {

    @Test
    public void keyValueObjectTest(){
        Student student = new Student("Harshit",21,"Durg", Arrays.asList(1,2,3));
        RBucketReactive<Student> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set =bucket.set(student);
        Mono<Void> get =bucket.get()
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }


}
