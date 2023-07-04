package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.harshit.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

public class MapTest extends BaseTest {

    @Test
    public void mapTest(){
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);
        Mono<String> name = map.put("name", "sam");
        Mono<String> city = map.put("city", "durg");
        Mono<String> age = map.put("age", "10");
        StepVerifier.create(name.concatWith(city).concatWith(age).then())
                .verifyComplete();
    }

    @Test
    public void mapTest2(){
        RMapReactive<String, String> map = this.client.getMap("user:2", StringCodec.INSTANCE);
        Map<String, String> maps = Map.of(
                "name", "jake",
                "age", "30",
                "city", "raipur"
        );

        StepVerifier.create(map.putAll(maps).then())
                .verifyComplete();
    }

    @Test
    public void mapTest3(){

        RMapReactive<Integer, Student> map = this.client.getMap("users", new TypedJsonJacksonCodec(Integer.class, Student.class));

        Student student = new Student("sam", 10, "bilaspur", List.of(1, 2, 3));
        Student student1 = new Student("mike", 20, "bilaspur", List.of(1, 2, 3));
        Mono<Student> mono = map.put(1,student);
        Mono<Student> mono2 = map.put(2,student1);

        StepVerifier.create(mono.concatWith(mono2).then())
                .verifyComplete();
    }

}
