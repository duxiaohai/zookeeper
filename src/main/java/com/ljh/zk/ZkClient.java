package com.ljh.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Explain:
 * Author: linjianhai
 * Date: 2022/6/9 23:44
 */
public class ZkClient {

    private String connectString = "192.168.91.31:2181,192.168.91.32:2181,192.168.91.33:2181";
    private int sessionTimeOut = 1000000;
    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {
            zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
//                System.out.println("--------------------------------------");
//                List<String> childrenList = null;
//                try {
//                    childrenList = zkClient.getChildren("/", true);
//                    childrenList.forEach(children ->{
//                        System.out.println(children);
//                    });
//                    System.out.println("----------------------------------");
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Test
    public void create() throws KeeperException, InterruptedException {
        String node = zkClient.create("/atguigu", "ljh".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

    }

    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> childrenList = zkClient.getChildren("/", true);
//        childrenList.forEach(children ->{
//            System.out.println(children);
//        });
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void exit() throws KeeperException, InterruptedException {
        Stat exists = zkClient.exists("/atguigu", false);
        System.out.println(exists ==null ? "not exit": "exit");
    }


}
