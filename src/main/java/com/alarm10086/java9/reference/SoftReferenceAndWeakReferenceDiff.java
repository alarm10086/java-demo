package com.alarm10086.java9.reference;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author alarm10086
 *
 */
public class SoftReferenceAndWeakReferenceDiff {

    /**
     * -Xms10M -Xmx10M -Xmn5M -XX:+UseParallelGC -Xlog:gc(或 -XX:+PrintGCDetails）
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("softReference test");
        softReference();
        System.out.println("weakReference test");
        weakReference();
    }

    public static void softReference() {
        SoftReference[] softArr = new SoftReference[5];
        softArr[0] = new SoftReference<byte[]>(new byte[1024 * 1024 * 2]);
        System.out.println("GC 前===>" + softArr[0].get());
        System.gc();
        System.out.println("第一次GC后：===>" + softArr[0].get());
        softArr[1] = new SoftReference<byte[]>(new byte[1024 * 1024 * 2]);
        System.gc();
        System.out.println("第二次GC后===>" + softArr[0].get());
        softArr[2] = new SoftReference<byte[]>(new byte[1024 * 1024 * 2]);
        System.gc();
        System.out.println("第三次GC后===>" + softArr[0].get());
        softArr[3] = new SoftReference<byte[]>(new byte[1024 * 1024 * 2]);
        //System.gc();  这里都不需要显示执行，因为堆内存已经满了，虚拟机自己会执行。
        System.out.println("第四次GC后===>" + softArr[0].get());
    }

    public static void weakReference() {
        WeakReference<Integer> weak = new WeakReference<>(Integer.valueOf(100));
        System.out.println("GC 前===>" + weak.get());
        System.gc();
        System.out.println("GC 后===>" + weak.get());
    }
}
