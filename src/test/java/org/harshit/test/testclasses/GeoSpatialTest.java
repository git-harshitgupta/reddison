package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.harshit.test.dto.GeoLocation;
import org.harshit.test.dto.Resturant;
import org.harshit.test.util.ResturantUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class GeoSpatialTest extends BaseTest {

    private RGeoReactive<Resturant> geo;
    private RMapReactive<String, GeoLocation> map;

    @BeforeAll
    public void setGeo(){
        geo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Resturant.class));
        map = client.getMap("us:texas", new TypedJsonJacksonCodec(String.class,GeoLocation.class));
    }

    @Test
    public void add(){
        Mono<Void> then = Flux.fromIterable(ResturantUtil.getRestaurants())
                .flatMap(r -> geo.add(r.getLongitude(), r.getLatitude(), r).thenReturn(r))
                .flatMap(r -> map.fastPut(r.getZip(),new GeoLocation(r.getLongitude(), r.getLatitude())))
                .then();

        StepVerifier.create(then).verifyComplete();
    }

    @Test
    public void search(){
        Mono<Void> then = map.get("75224")
                .map(gl -> GeoSearchArgs.from(gl.getLongitude(),
                        gl.getLatitude()).radius(3, GeoUnit.KILOMETERS))
                .flatMap(r -> geo.search(r))
                .flatMapIterable(Function.identity())
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(then).verifyComplete();

    }


}
