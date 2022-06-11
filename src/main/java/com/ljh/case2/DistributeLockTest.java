package com.ljh.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Explain:
 * Author: linjianhai
 * Date: 2022/6/12 0:05
 */
public class DistributeLockTest {


    public static void main(String[] args) {
        DistributeLock lock01 = new DistributeLock();
        Runnable task01 = () -> {
            lock01.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock01.unlock();
        };

        DistributeLock lock02 = new DistributeLock();
        Runnable task02 = () -> {
            lock02.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock02.unlock();
        };

        for (int i = 0; i < 10; i++) {
            new Thread(task01, "server01-thread-" + i).start();
            new Thread(task02, "server02-thread-" + i).start();
        }
    }

}
