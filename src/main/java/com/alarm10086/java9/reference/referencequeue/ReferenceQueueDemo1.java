package com.alarm10086.java9.reference.referencequeue;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alarm10086
 *
 */
public class ReferenceQueueDemo1 {
    private static int KEY_COUNT = 40000;

    /**
     * JVM 参数：-Xms8m -Xmx8m
     * 建议用 debug 启动，调整 Xms、Xmx 观察输出结果。
     *
     * @param args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String[] args) {
        /*创建引用队列*/
        ReferenceQueue<SoftReference<String[]>> rq = new ReferenceQueue<>();

        /*创建一个软引用数组，每一个对象都是软引用类型*/
        int[] a = null, b = null, c = null;
        // 持有3个强引用
        Map<Integer, SoftReference<int[]>> cache = new HashMap<>();
        for (int i = 0; i < KEY_COUNT; i++) {
            /*创建一些小对象，尽可能增加内存碎片*/
            int[] intArrayRef = new int[10];
            if (i == 100) {
                a = intArrayRef;
            } else if (i == 200) {
                b = intArrayRef;
            } else if (i == 1002) {
                c = intArrayRef;
            }
            cache.put(i, new SoftReference(intArrayRef, rq));
        }

        totalSurvivalObjectCount(cache);

        /*创建占用大内存的对象，触发gc（内存不够用），使得软引用对象被部分（或全部）回收*/
        int[] strongRef = new int[20700];
        strongRef = null;
        strongRef = new int[30300];
        strongRef = null;

        totalSurvivalObjectCount(cache);

        inSoftReferenceQueueObjectCount(cache);
    }

    private static void totalSurvivalObjectCount(Map<Integer, SoftReference<int[]>> cache) {
        assert (cache.size() == KEY_COUNT);
        int total = 0;
        for (int i = 0; i < KEY_COUNT; i++) {
            assert (cache.get(i) != null);
            int[] value = cache.get(i).get();
            if (value != null) {
                // do something。比如，可以把软引用重新指回强引用。
//                System.out.println("survival. key is " + i);
                total++;
            }
        }
        System.out.println("survival Object count: " + total);
    }

    /**
     * 通过isEnqueued()判断一个软引用对象是否在 SoftReference 队列中
     * 注意：
     * 不是 new SoftReference(intArrayRef, rq) 就已经在队列中了。
     * JVM 会在特定时机将引用 enqueue 到队列里。
     *
     * @param cache
     */
    private static void inSoftReferenceQueueObjectCount(Map<Integer, SoftReference<int[]>> cache) {
        assert (cache.size() == KEY_COUNT);
        int n = 0;
        for (int i = 0; i < KEY_COUNT; i++) {
            if (cache.get(i).isEnqueued()) {
                n++;
            }
        }
        System.out.println("in SoftReferenceQueue Object count: " + n);
    }
}