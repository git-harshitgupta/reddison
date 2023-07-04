package org.harshit.test.testclasses;

import org.harshit.test.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.LongCodec;
import reactor.test.StepVerifier;

public class BatchTest extends BaseTest {

    @Test
    public void batchTest(){
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
        RListReactive<Object> numbersList = batch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Object> numbersSet = batch.getSet("numbers-set", LongCodec.INSTANCE);

        for (long i=0 ; i<20000; i++){
            numbersList.add(i);
            numbersSet.add(i);
        }

        StepVerifier.create(batch.execute().then()).verifyComplete();

    }

}
