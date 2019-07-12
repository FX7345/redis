package com.zkzn.redis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MsgController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/sendMsg")
    public String sendMsg(){
        stringRedisTemplate.convertAndSend("topic", "Hello world!");
        stringRedisTemplate.convertAndSend("topic", "Hello world!");
        stringRedisTemplate.convertAndSend("topic", "Hello word!");
        stringRedisTemplate.convertAndSend("topic", "Hello world!");
        stringRedisTemplate.convertAndSend("topic", "Hello world!");
        stringRedisTemplate.convertAndSend("topic", "Hello word!");
        stringRedisTemplate.convertAndSend("topic", "Hello world!");
        stringRedisTemplate.convertAndSend("topic", "Hello word!");
        return "ok";
    }
}
