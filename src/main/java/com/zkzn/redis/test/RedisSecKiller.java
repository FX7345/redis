package com.zkzn.redis.test;

import com.zkzn.redis.utils.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisSecKiller {

    // 模拟用户抢购最大并发数
    private static final int N_THREADS = 5;

    // jedis通过watch方法监控WATCH_KEY，一旦发生改变，事务将失败。
    public static final String WATCH_KEY = "Goods";

    // 商品总数
    private static final int GOODS_NUM = 10;

    // 用户数量
    private static final int USER_NUM = 100;

    public static void main(String[] args) {
        // 创建线程池，模拟N_THREADS位用户同时抢购的过程。
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        Jedis jedis = JedisUtil.getJedis();

        // 设置商品总数为10
        jedis.set(WATCH_KEY, String.valueOf(GOODS_NUM));
        jedis.close();

        // 模拟USER_NUM位用户在抢购商品
        for (int i = 0; i < USER_NUM; i++) {
            executorService.execute(new JedisRunnable(UUID.randomUUID().toString()));
//            System.out.println("==============循环分割线===============");
        }

        executorService.shutdown();
    }

}

class JedisRunnable implements Runnable {

    private Jedis jedis = JedisUtil.getJedis();
    private String userId;

    public JedisRunnable(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        try {
            // 事务状态，如果监控的key没有发生改变，那么应该返回OK，事务也可以正常执行。
            jedis.watch(RedisSecKiller.WATCH_KEY);

            // 获取剩余商品数
            int leftGoodsNum = Integer.valueOf(jedis.get(RedisSecKiller.WATCH_KEY));

            // 当剩余商品数大于0时，才进行剩余商品数减1的事务操作。
            if (leftGoodsNum > 0) {
                // 开启jedis事务
                Transaction tx = jedis.multi();

                // 方法一：在事务中对键Goods对应的值做减1操作，此时tx.exec()的返回值的第一个元素是Goods对应的当前值。
                tx.decrBy(RedisSecKiller.WATCH_KEY, 1);

                // 方法二：在事务中设置Goods的值为原值减1，此时tx.exec()的返回值的第一个元素是"OK"。
//                tx.set(RedisSecKiller.WATCH_KEY, String.valueOf(leftGoodsNum - 1));

                // 执行事务，得到返回值。
                List<Object> results = tx.exec();

                // leftGoodsNum比键Goods对应的值大1，因为事务中刚执行了减1操作。
                // 由此可知，在当前事务中，leftGoodsNum与Goods对应的值（真实剩余商品数量）并不同步。
//                System.out.println("剩余商品数量：" + leftGoodsNum);
//                System.out.println("真实剩余商品数量：" + results);

                // results为null或空时，表示并发情况下用户没能抢购到商品，秒杀失败。
                if (results == null || results.isEmpty()) {
                    String failUserInfo = "fail---" + userId;

                    // 此时无法通过results.get(0)获取真实剩余商品数量。
                    String failMsg = "用户" + failUserInfo + "，抢购失败，剩余商品数量："+ leftGoodsNum +
                            "，但此时无法获取真实剩余商品数量。";
                    System.out.println(failMsg);

                    // 将秒杀失败的用户信息存入Redis。
                    jedis.setnx(failUserInfo, failMsg);
                } else { // 此时tx.exec()事务执行成功，会自动提交事务。
                    for(Object succ : results) {
                        String succUserInfo ="succ" + succ.toString() + "---" + userId;
                        String succMsg= "用户" + succUserInfo + "，抢购成功，当前抢购成功人数：" +
                                (10 - Integer.parseInt(results.get(0).toString())) +
                                "，真实剩余商品数量：" + Integer.parseInt(results.get(0).toString());
                        System.out.println(succMsg);

                        // 将秒杀成功的用户信息存入Redis。
                        jedis.setnx(succUserInfo, succMsg);
                    }
                }
            } else { // 此时库存为0，秒杀活动结束。
                String overUserInfo ="over---" + userId;
                String overMsg = "用户" + overUserInfo + "，商品被抢购完毕，剩余商品数量：" + leftGoodsNum;
                System.out.println(overMsg);

                // 将秒杀活动结束后还在访问秒杀系统的用户信息存入Redis。
                jedis.setnx(overUserInfo, overMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisUtil.returnResource(jedis);
        }
    }

}
