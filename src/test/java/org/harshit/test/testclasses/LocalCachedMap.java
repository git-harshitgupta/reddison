package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.harshit.test.config.RedissonConfig;
import org.harshit.test.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class LocalCachedMap extends BaseTest {

    //localchachedmap uses pub/sub internally to update the value based on the configuration that we give
    private RLocalCachedMap<Integer,Student> studentMap;

    @BeforeAll
    public void setupClient(){
        RedissonConfig config = new RedissonConfig();
        RedissonClient redissonClient = config.getClient();
        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);
        this.studentMap = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions
        );
    }

    @Test
    public void appServer1(){
        Student student = new Student("sam", 10, "bilaspur", List.of(1, 2, 3));
        Student student1 = new Student("mike", 20, "bilaspur", List.of(1, 2, 3));
        studentMap.put(1,student);
        studentMap.put(2,student1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i+" => "+studentMap.get(1)))
                .subscribe();

        sleep(600000);
    }

    @Test
    public void appServer2(){
        Student student = new Student("sam-updated", 10, "bilaspur", List.of(1, 2, 3));
        studentMap.put(1,student);

    }


}
