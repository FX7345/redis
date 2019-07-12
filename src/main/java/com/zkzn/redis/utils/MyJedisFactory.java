package com.zkzn.redis.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MyJedisFactory {
 
  static Jedis jedis = null;
  public static Jedis getLocalJedis() {
    /**
     * genericObjectPoolConfig
     * 这个类里提供有Redis连接池的相关默认值，也可以自定义相关连接池的相关配置
     * 下面是常见相关默认配置
     * GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
     * 设置最大连接数为默认值的 5 倍
     * poolConfig.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL * 5);
     * poolConfig.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 3);
     * 设置最小空闲连接数为默认值的 2 倍
     * poolConfig.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE * 2);
     * 设置开启 jmx 功能
     * poolConfig.setJmxEnabled(true);
     * 设置连接池没有连接后客户端的最大等待时间 ( 单位为毫秒 )
     * poolConfig.setMaxWaitMillis(3000);
     */
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    JedisPool myPool = new JedisPool(genericObjectPoolConfig, "127.0.0.1", 6379);
    jedis = myPool.getResource();
    return jedis;
  }
 
 
}