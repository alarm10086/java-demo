package com.alarm10086.java9.concurrent;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author alarm10086
 *
 */
public class RWSample {
    private final Map<String, String> m = new TreeMap<>();
    /**
     * 如果读锁试图锁定时，写锁是被某个线程持有，读锁将无法获得，
     * 而只好等待对方操作结束，这样就可以自动保证不会读取到有争议的数据。
     *
     * ReentrantReadWriteLock 的开销还是比较大。如果读多写好，并且读的时候不经常在写，建议使用 StampedSample
     */
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public String get(String key) {
        r.lock();
        System.out.println(" 读锁锁定！");
        try {
            return m.get(key);
        } finally {
            r.unlock();
        }
    }

    public String put(String key, String entry) {
        w.lock();
        System.out.println(" 写锁锁定！");
        try {
            return m.put(key, entry);
        } finally {
            w.unlock();
        }
    }
// …
}


