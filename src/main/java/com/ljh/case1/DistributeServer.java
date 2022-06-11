package com.ljh.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * Explain:
 * Author: linjianhai
 * Date: 2022/6/11 11:54
 */
public class DistributeServer {

    private String connectString = "192.168.91.31:2181,192.168.91.32:2181,192.168.91.33:2181";
    private int sessionTimeOut = 1000000;
    private ZooKeeper zkClient;


    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        DistributeServer server = new DistributeServer();
        server.getConnection();
        server.regist(args[0]);
        server.business();
    }

    private void business() throws InterruptedException {
       Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostName) throws KeeperException, InterruptedException {
        zkClient.create("/servers/" + hostName, hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostName + "is online");
    }


    private void getConnection() throws IOException {
            zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

}
