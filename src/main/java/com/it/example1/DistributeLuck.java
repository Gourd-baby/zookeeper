package com.it.example1;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/19 20:37
 * @注释 模拟分布式锁的场景
 */
public class DistributeLuck {
    private ZooKeeper zk;
    private String connectionString = "redis100:2181,redis101:2181,redis102:2181";
    private int sessionTimeout = 2000;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private CountDownLatch waitDownLatch = new CountDownLatch(1);
    private String previousPath ;
    private String seqLock;

    //1、连接zk集群
    public DistributeLuck() throws Exception {
        zk = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 连接建立时, 打开 latch, 唤醒 wait 在该 latch 上的线程
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                // 发生了 waitPath 的删除事件
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(previousPath))
                {
                    waitDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        //判断锁是否存在，不存在则创建一个永久节点(即锁)
        Stat exists = zk.exists("/locks", false);
        if (exists == null){
            zk.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    //2、lock加锁
    public void lock()throws Exception{
        //1) 创建一个节点锁
         seqLock = zk.create("/locks/" + "seq-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        //2) 检测这个节点是否是locks节点下最小节点
        List<String> children = zk.getChildren("/locks", false);
        if (children.size() == 1){
//            System.out.println(children+" 加锁成功");
            return;
        }else {
            //2.1 对locks下的节点排序
            Collections.sort(children);
            //2.2 获取到当前节点的名称
            String currentNode = seqLock.substring("/locks/".length());
            //2.3 获取当前节点的索引
            int index = children.indexOf(currentNode);
            if (index == -1){
                System.out.println("数据异常");
                return;
            }else if (index == 0){
                return;
            }else {
                previousPath ="/locks/"+ children.get(index - 1);
                zk.getData(previousPath,true,null);
                waitDownLatch.await();
                return;
            }
        }

    }
    //3、unlock解锁
    public void zkUnlock() {
        try {
            zk.delete(this.seqLock, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
