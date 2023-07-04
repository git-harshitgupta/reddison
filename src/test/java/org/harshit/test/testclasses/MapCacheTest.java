package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.harshit.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapCacheTest extends BaseTest {

    @Test
    public void mapCacheTest(){
        RMapCacheReactive<Integer, Student> mapCache = this.client.getMapCache("user:cache",
                new TypedJsonJacksonCodec(Integer.class, Student.class));

        Student student = new Student("sam", 10, "bilaspur", List.of(1, 2, 3));
        Student student1 = new Student("mike", 20, "bilaspur", List.of(1, 2, 3));

        Mono<Student> put1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> put2 = mapCache.put(2, student, 10, TimeUnit.SECONDS);

        StepVerifier.create(put1.concatWith(put2).then())
                .verifyComplete();

        sleep(3000);

        mapCache.get(1)
                .doOnNext(System.out::println)
                .subscribe();
        mapCache.get(2)
                .doOnNext(System.out::println)
                .subscribe();

        sleep(3000);

        mapCache.get(1)
                .doOnNext(System.out::println)
                .subscribe();
        mapCache.get(2)
                .doOnNext(System.out::println)
                .subscribe();
    }

}
