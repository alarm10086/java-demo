package com.alarm10086.java9.reference.referencequeue;

import javax.management.timer.Timer;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alarm10086
 *
 */
public class PhantomReferenceDemo2 {
    private static ReferenceQueue<byte[]> refQueue = new ReferenceQueue<byte[]>();
    private static int _1M = 1024 * 1024;

    /**
     * JVM 参数：-Xms6m -Xmx6m
     *
     * @param args
     */
    public static void main(String[] args) {
        Thread daemonThread = new PhantomReferenceCleanThread();
        daemonThread.start();

        Map<Integer, PhantomReference> cache = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            // bytes 引用出了 for 循环作用域，就失效。
            byte[] bytes = new byte[_1M];
            /**
             * 创建引用并关联到响应对象时，可以选择是否需要关联引用队列，JVM 会在特定时机将引用 enqueue 到队列里。
             * 我们可以从队列里获取引用（remove方法在这里实际是有获取的意思）进行相关后续逻辑。
             * 利用引用队列，可以在对象处于相应状态时（对于幻象引用，就是被 finalize 了，处于幻象可达状态），执行后期处理逻辑。
             */
            PhantomReference<byte[]> weakReference = new PhantomReference<byte[]>(bytes, refQueue);
            // 注意：这里 weakReference 对象的作用域需要大于for循环，不然 weakReference 对象出了作用就已经被 JVM 回收（还没来得及进refQueue）
            cache.put(i, weakReference);
        }


        // 强行把内存耗尽
        String a = "a";
        while (true) {
            a += a;
//            System.out.println(a);
        }
    }

    private static class PhantomReferenceCleanThread extends Thread {
        public PhantomReferenceCleanThread() {
            super();
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                int cnt = 0;
                PhantomReference<byte[]> k;
                while ((k = (PhantomReference) refQueue.remove(Timer.ONE_SECOND)) != null) {
                    // 对于幻象引用，对象被 finalize 后（处于幻象可达状态），执行后期处理逻辑
                    System.out.println((cnt++) + "回收了:" + k);
                }
            } catch (InterruptedException e) {
                // Handle it
            }
        }
    }
}
