package com.zkzn.redis.utils;
 
import com.zkzn.redis.utils.StringUtils;
import com.zkzn.redis.utils.MyJedisFactory;
import redis.clients.jedis.Jedis;
 
/**
 * 消息发布方
 * @author yamikaze
 */
public class Publisher {
 
//    public static final String CHANNEL_KEY = "channel:message";
//    private Jedis jedis;
// 
//    public Publisher() {
//        jedis = MyJedisFactory.getLocalJedis();
//    }
// 
//    public void publishMessage(String message) {
//        if(StringUtils.isBlank(message)) {
//            return;
//        }else{
////        	jedis.lpush(CHANNEL_KEY, message);
//        	long abc=jedis.publish(CHANNEL_KEY, message);
//        	System.out.println("获取数据成功"+abc);
//        }
//        
//    }
	public  final static String CHANNEL_KEY = "channel:message";
    private Jedis jedis;
    public boolean sendMessage(String message) {
    	boolean isTrue=false;
    	jedis = MyJedisFactory.getLocalJedis();
        if(StringUtils.isBlank(message)) {
        	isTrue=false;
        }else{
        	long abc =jedis.publish(CHANNEL_KEY, message);
        	System.out.println("获取数据成功"+abc);
        	isTrue=true;
        }
        return isTrue;
    }
	
	
    public static void main(String[] args) {
    	SubscribeClient client = new SubscribeClient();
        client.subscribe(Publisher.CHANNEL_KEY);
        Publisher publisher = new Publisher();
        for(int i=0;i<100;i++){
        	if(i%2==0){
        		publisher.sendMessage("Hello Redis!");        		
        	}else{
        		publisher.sendMessage("Good Redis!");
        	}        	
        }
//    	 Publisher publisher = new Publisher();
//       publisher.publishMessage("Hello Redis!");
    }
}