package com.it.example;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/19 18:55
 * @注释 模拟服务器上线，客户端能进行实时监测到服务器节点上线
 */
public class ZkClient {
    private ZooKeeper zk;
    private String connectionString = "redis100:2181,redis101:2181,redis102:2181";
    private int sessionTimeout = 2000;
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZkClient zkClient = new ZkClient();
        //1、连接zk
        zkClient.getConnectionZkServer();
        //2、监控服务器节点
        zkClient.monitiorServerNode();
        //3、实际业务（这里让它实现Thread.sleep()以保证main方法能一直运行下去，能更直观的观看效果）
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 连接zk集群
     */
    private void getConnectionZkServer() throws IOException {
        zk = new ZooKeeper(connectionString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    monitiorServerNode();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 监控服务器节点
     */
    private void monitiorServerNode() throws InterruptedException, KeeperException {
        List<String> list = new ArrayList<>();
        List<String> zkServerNode = zk.getChildren("/servers", true);
        for (String serverNode : zkServerNode) {
            byte[] data = zk.getData("/servers/" + serverNode, false, null);
            list.add(new String(data));
        }
        System.out.println("以下服务器上线："+list);
    }

}
