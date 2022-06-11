package com.ljh.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

/**
 * Explain:
 * Author: linjianhai
 * Date: 2022/6/11 21:49
 */
public class DistributeLock {

    private final String connectString = "192.168.91.31:2181,192.168.91.32:2181,192.168.91.33:2181" ;

    private ZooKeeper zkClient;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public DistributeLock() {
        try {
            int sessionTimeout = 1000000;
            zkClient = new ZooKeeper(connectString, sessionTimeout, null);
            // 判断节点 /exclusive_lock 是否存在
            if (zkClient.exists("/exclusive_lock", false) == null) {
                // 不存在则创建节点
                zkClient.create("/exclusive_lock", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        try {
            // 创建对应的临时带序号节点
            String currentLockNode = zkClient.create("/exclusive_lock/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // 判断创建的节点是否是最小的序号节点，如果是获取到锁；如果不是，监听他序号前一个节点
            List<String> children = zkClient.getChildren("/exclusive_lock", false);
            // 如果children 只有一个值，那就直接获取锁；如果有多个节点，需要判断谁最小
            if (children.size() > 1) {
                Collections.sort(children);
                // 获取节点名称 seq-00000000
                String thisNode = currentLockNode.substring("/exclusive_lock/".length());
                // 通过 seq-00000000 获取该节点在children集合的位置
                int index = children.indexOf(thisNode);
                /**
                 * 因为在zkClient.create和zkClient.getChildren("/exclusive_lock", false);可能有其它线程也创建了节点，
                 * 所以并不是说只有 children.size() == 1 这个线程才是第一个创建节点的线程
                 */
                if (index == 0) {// 如果自己就是第一个节点，那么获得锁，
                    System.out.println(Thread.currentThread().getName() + "获得锁");
                    threadLocal.set(currentLockNode);
                    return;
                }
                //
                String preNode = "/exclusive_lock/" + children.get(index - 1);
                Thread thread = Thread.currentThread();
                // 监听它前一个节点的变化，如果前一个节点删除了，会调用回调函数把自己唤醒
                zkClient.getData(preNode, watchedEvent -> LockSupport.unpark(thread), null);
                // 把自己挂起
                LockSupport.park();
            }
            threadLocal.set(currentLockNode);
            System.out.println(Thread.currentThread().getName() + "获得锁");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        try {
            System.out.println(Thread.currentThread().getName() + "释放了锁");
            zkClient.delete(threadLocal.get(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
