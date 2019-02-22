package com.alarm10086.java9.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 死锁定位：（区分线程状态 -> 查看等待目标 -> 对比 Monitor 等持有状态）
 * 首先，可以使用 jps 或者系统的 ps 命令、任务管理器等工具，确定进程 ID。
 * 其次，调用 jstack 获取线程栈：${JAVA_HOME}\bin\jstack your_pid
 *
 * @author alarm10086
 *
 */
public class DeadLockSample extends Thread {
    private String first;
    private String second;

    public DeadLockSample(String name, String first, String second) {
        super(name);
        this.first = first;
        this.second = second;
    }

    @Override
    public void run() {
        synchronized (first) {
            System.out.println(this.getName() + " obtained: " + first);
            try {
                Thread.sleep(1000L);
                synchronized (second) {
                    System.out.println(this.getName() + " obtained: " + second);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        {// 死锁检测代码
         // 注意，对线程进行快照本身是一个相对重量级的操作，还是要慎重选择频度和时机。
            ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
            Runnable dlCheck = new Runnable() {
                @Override
                public void run() {
                    long[] threadIds = mbean.findDeadlockedThreads();
                    if (threadIds != null) {
                        ThreadInfo[] threadInfos = mbean.getThreadInfo(threadIds);
                        System.out.println("Detected deadlock threads:");
                        for (ThreadInfo threadInfo : threadInfos) {
                            System.out.println(threadInfo.getThreadName());
                        }
                    }
                }
            };

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            // 稍等 5 秒，然后每 10 秒进行一次死锁扫描
            scheduler.scheduleAtFixedRate(dlCheck, 5L, 10L, TimeUnit.SECONDS);
        }


        {// 死锁代码
            String lockA = "lockA";
            String lockB = "lockB";
            DeadLockSample t1 = new DeadLockSample("Thread1", lockA, lockB);
            DeadLockSample t2 = new DeadLockSample("Thread2", lockB, lockA);
            t1.start();
            t2.start();
            t1.join();
            t2.join();
        }
    }
}
