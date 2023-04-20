package com.it.example2;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/20 17:36
 * @注释
 */
public class CuratorTest {
    private static String connectionString = "redis100:2181,redis101:2181,redis102:2181";
    private static int sessionTimeout = 2000;
    private static int connectionTimeoutMs = 2000;

    public static void main(String[] args) {
        InterProcessMutex mutex = new InterProcessMutex(getCuratorFramework(), "/locks");
        InterProcessMutex mutex2 = new InterProcessMutex(getCuratorFramework(), "/locks");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mutex.acquire();
                    System.out.println("线程 1 获取锁");
                    // 测试锁重入
                    mutex.acquire();
                    System.out.println("线程 1 再次获取锁");
                    Thread.sleep(5 * 1000);
                    mutex.release();
                    System.out.println("线程 1 释放锁");
                    mutex.release();
                    System.out.println("线程 1 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mutex2.acquire();
                    System.out.println("线程 2 获取锁");
                    // 测试锁重入
                    mutex2.acquire();
                    System.out.println("线程 2 再次获取锁");
                    Thread.sleep(5 * 1000);
                    mutex2.release();
                    System.out.println("线程 2 释放锁");
                    mutex2.release();
                    System.out.println("线程 2 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 分布式锁初始化
    private static CuratorFramework getCuratorFramework() {
        //重试策略，初试时间 3 秒，重试 3 次
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 3);
        //通过工厂创建 Curator
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectionString)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(retry).build();
        //开启连接
        client.start();
        System.out.println("zookeeper 初始化完成...");
        return client;

    }
}
