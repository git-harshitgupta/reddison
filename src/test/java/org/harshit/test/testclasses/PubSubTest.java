package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

public class PubSubTest extends BaseTest {

    @Test
    public void subscriber1(){
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void subscriber2(){
        RPatternTopicReactive topic = this.client.getPatternTopic("slack-room*", StringCodec.INSTANCE);
        topic.addListener(String.class, new PatternMessageListener<String>() {
                    @Override
                    public void onMessage(CharSequence pattern, CharSequence topic, String message) {
                        System.out.println(pattern+ ":" +topic+ ":" +message);
                    }
                })
                .doOnError(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();
        sleep(600000);
    }

}
