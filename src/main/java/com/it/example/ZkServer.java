package com.it.example;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @version 1.0
 * @Author 作者名
 * @Date 2023/4/19 18:52
 * @注释 模拟服务器上线，客户端能进行实时监听
 * 集群-》服务器（其实就是一个节点）【而且不管是客户端还是服务器节点，相较于集群来说都是 客户端】
 */
public class ZkServer {
    private ZooKeeper zk;
    private String connectionString = "redis100:2181,redis101:2181,redis102:2181";
    private int sessionTimeout = 2000;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZkServer zkServer = new ZkServer();
        //1、连接zk集群
        zkServer.getConnectionZkServer();
        //2、注册服务器节点到集群中
        zkServer.registNode(args[0]);
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
            }
        });
    }

    /**
     * 注册服务节点到集群中，这里需要服务器的节点名称来演示
     * @param nodeName
     */
    private void registNode(String nodeName) throws InterruptedException, KeeperException {
        String serverNode = zk.create("/servers/" + nodeName, nodeName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(serverNode+" is online");
    }
}
