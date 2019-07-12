package com.zkzn.redis.utils;
 
import org.springframework.beans.factory.annotation.Autowired;

import com.zkzn.redis.utils.MyJedisFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
 
/**
 * 消息订阅方客户端
 * @author yamikaze
 */
public class SubscribeClient {
 
    private static Jedis jedis;
    private static final String EXIT_COMMAND = "exit";
    @Autowired
    private static JedisPool jedisPool;
 
    public SubscribeClient() {
        jedis = MyJedisFactory.getLocalJedis();
    }
 
    public void subscribe(String ...channel) {
        if(channel == null || channel.length <= 0) {
            return;
        }
        //消息处理,接收到消息时如何处理
        JedisPubSub jps = new JedisPubSub() {
            /**
             * JedisPubSub类是一个没有抽象方法的抽象类,里面方法都是一些空实现
             * 所以可以选择需要的方法覆盖,这儿使用的是SUBSCRIBE指令，所以覆盖了onMessage
             * 如果使用PSUBSCRIBE指令，则覆盖onPMessage方法
             * 当然也可以选择BinaryJedisPubSub,同样是抽象类，但方法参数为byte[]
             */
            @Override
            public void onMessage(String channel, String message) {
                if(Publisher.CHANNEL_KEY.equals(channel)) {
                    System.out.println("接收到消息: channel : " + message);
                    //接收到exit消息后退出
                    if(EXIT_COMMAND.equals(message)) {
                        System.exit(0);
                    }
 
                }
            }
 
            /**
             * 订阅时
             */
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                if(Publisher.CHANNEL_KEY.equals(channel)) {
                    System.out.println("订阅了频道:" + channel);
                }
            }
        };
        //可以订阅多个频道 当前线程会阻塞在这儿
        jedis.subscribe(jps, channel);
    }
 
    public static void main(String[] args) {
    	SubscribeClient client = new SubscribeClient();
        client.subscribe(Publisher.CHANNEL_KEY);
        //并没有 unsubscribe方法
        //相应的也没有punsubscribe方法

        
//        String msg = jedis.brpoplpush(Publisher.CHANNEL_KEY, "message", 0);
//        System.out.println("接收消息："+msg);
//        jedis.lrem("message", 1, msg);    	 
//    	    long time = 0;
//    	    for (int i = 0; i < 100; i++) {
//    	      try (Jedis jedis = MyJedisFactory.getLocalJedis()) {
//    	        // 1、推送
//    	        long time1 = System.currentTimeMillis();
//    	        if(i%2==0){
//            		jedis.lpush("jedis-mq", "abc");
//            	}else{
//            		jedis.lpush("jedis-mq", "def");
//            	} 
//    	        
//    	        // 2、接收
//    	        String msg = jedis.brpoplpush("jedis-mq", "jedis-mq_bak", 0);
//    	        System.out.println("接收消息："+msg);
//    	        jedis.lrem("jedis-mq_bak", 1, msg);
//    	        long time2 = System.currentTimeMillis();
//    	        time += time2 - time1;
//    	      } catch (Exception e) {
//    	        e.printStackTrace();
//    	      }
//    	    }
//    	    System.out.println("总时间：" + time);
    }
}