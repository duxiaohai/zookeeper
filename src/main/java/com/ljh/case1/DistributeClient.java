package com.ljh.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Explain: 客户端，服务端 案例
 * Author: linjianhai
 * Date: 2022/6/11 12:04
 */
public class DistributeClient {

    private String connectString = "192.168.91.31:2181,192.168.91.32:2181,192.168.91.33:2181";
    private int sessionTimeOut = 1000000;
    private ZooKeeper zkClient;
    private String parentNode = "/servers";

    public static void main(String[] args) throws Exception {
        DistributeClient client = new DistributeClient();
        client.getConnection();
        client.getServerList();

        client.business();
    }

    // 业务功能
    public void business() throws Exception{

        System.out.println("client is working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws KeeperException, InterruptedException {
        // 1 获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zkClient.getChildren(parentNode, true);
        // 2 存储服务器信息列表
        ArrayList<String> servers = new ArrayList<>();
        // 3 遍历所有节点，获取节点中的主机名称信息
        for (String child : children) {
            byte[] data = zkClient.getData(parentNode + "/" + child,
                    false, null);
            servers.add(new String(data));
        }
        // 4 打印服务器列表信息
        System.out.println(servers);

    }

    private void getConnection() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
