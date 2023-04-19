package com.it.example1;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/19 21:13
 * @注释
 */
public class DistributeLuckTest {
    public static void main(String[] args) throws Exception {
        DistributeLuck lock1 = new DistributeLuck();
        DistributeLuck lock2 = new DistributeLuck();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.lock();
                    System.out.println("线程 1 获取锁");
                    Thread.sleep(5 * 1000);
                    lock1.zkUnlock();
                    System.out.println("线程 1 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.lock();
                    System.out.println("线程 2 获取锁");
                    Thread.sleep(5 * 1000);
                    lock2.zkUnlock();
                    System.out.println("线程 2 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
