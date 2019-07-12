package com.zkzn.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
    // Redis服务器IP
    private static final String ADDR = "127.0.0.1";

    // Redis的端口号
    private static final int PORT = 6379;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;

    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值是8。
    private static final int MAX_IDLE = 200;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Jedis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(MAX_IDLE);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放Jedis资源
     *
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            // 注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
            jedis.close();
            // 被废弃的方法，使用close即可。
//            jedisPool.returnResource(jedis);
        }
    }
}
