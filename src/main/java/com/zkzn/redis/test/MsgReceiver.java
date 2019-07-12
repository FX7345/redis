package com.zkzn.redis.test;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MsgReceiver.class);

    private CountDownLatch latch;

    @Autowired
    public MsgReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) {
        logger.info("消息接收:" + message);
        latch.countDown();
    }
}
