package com.alarm10086.java9.reference;

import java.lang.ref.WeakReference;

/**
 * @author alarm10086
 *
 */
public class WeakReferenceDemo {
    public static void main(String[] args) {
        /*若引用对象中指向了一个长度为1000个元素的整形数组*/
        WeakReference<String[]> weakReference = new WeakReference<String[]>(new String[1000]);

        /*未执行gc,目前仅被弱引用指向的对象还未被回收，所以结果不是null*/
        System.out.println(weakReference.get());

        /*执行一次gc,即使目前JVM的内存够用,但还是回收仅被弱引用指向的对象*/
        System.gc();
        System.out.println(weakReference.get());
    }
}
