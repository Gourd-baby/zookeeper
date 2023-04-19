package com.it.zk;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

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
            }
        });
    }
    @Test
    public void create() throws Exception{
        String zkNode = zkClient.create("/crj", "crj".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
