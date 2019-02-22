package com.alarm10086.java9.concurrent;

import javax.xml.crypto.Data;
import java.util.concurrent.locks.StampedLock;

/**
 * @author alarm10086
 *
 */
public class StampedSample {
    /**
     * 在提供类似读写锁的同时，还支持优化读模式。
     * 优化读基于假设，大多数情况下读操作并不会和写操作冲突，其逻辑是先试着读，
     * 然后通过 validate 方法确认是否进入了写模式，
     * 如果没有进入，就成功避免了开销；如果进入，则尝试获取读锁。
     */
    private final StampedLock sl = new StampedLock();

    void mutate() {
        long stamp = sl.writeLock();
        try {
            write();
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    Data access() {
        long stamp = sl.tryOptimisticRead();
        Data data = read();
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                data = read();
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return data;
    }

    private void write() {
        // ...
    }

    private Data read() {
        // ...
        return null;
    }
}
