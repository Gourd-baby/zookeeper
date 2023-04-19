package com.it.zk;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/19 17:26
 * @注释 测试zookeeper客户端
 */
public class ZKClient {
    private String connectionString = "redis100:2181,redis101:2181,redis102:2181";
    private int sessionTimeout = 2000;
    ZooKeeper zkClient;

    @Before
    public void init() throws Exception{
         zkClient = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //监听器
                try {
                    System.out.println("*****************");
                    List<String> children = zkClient.getChildren("/", true);
                    for (String child : children) {
                        System.out.println(child);
                    }
                    System.out.println("*****************");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 测试创建节点
     * @throws Exception
     */
    @Test
    public void create() throws Exception{
        String zkNode = zkClient.create("/crj", "crj".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 测试创建监听器，在上面的zookeeper里面
     * @throws Exception
     */
    @Test
    public void getChildren() throws Exception{
        Thread.sleep(Long.MAX_VALUE);
    }


}
