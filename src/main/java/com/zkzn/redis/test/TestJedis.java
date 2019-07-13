package com.zkzn.redis.test;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zkzn.redis.utils.MyJedisFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestJedis {
	@Autowired
	private static JedisPool jedisPool;
	public static void testJedis() throws IOException {

	 
	    long time = 0;
	    for (int i = 0; i < 100; i++) {
	      try (Jedis jedis = MyJedisFactory.getLocalJedis()) {
	        // 1、推送
	        long time1 = System.currentTimeMillis();
	        jedis.lpush("jedis-mq", "abc");
	        // 2、接收
	        String msg = jedis.brpoplpush("jedis-mq", "jedis-mq_bak", 0);
	        System.out.println(msg);
	        jedis.lrem("jedis-mq_bak", 1, msg);
	        long time2 = System.currentTimeMillis();
	        time += time2 - time1;
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    }
	    System.out.println("总共时chang：" + time);
	  }
	public static void main(String[] args) throws IOException {
		testJedis();
	}

}

