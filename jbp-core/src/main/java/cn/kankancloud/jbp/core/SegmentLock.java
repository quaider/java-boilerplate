package cn.kankancloud.jbp.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SegmentLock {

    Logger log = Logger.getLogger(SegmentLock.class.getName());

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private final int mask;
    private final Lock[] locks;

    public SegmentLock(int concurrency) {
        int size = formatSize(concurrency);
        mask = size - 1;
        locks = new Lock[size];
        for (int i = 0; i < size; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    /**
     * 阻塞获取锁，可被打断
     *
     * @param lockId 锁ID
     * @throws InterruptedException 线程被中断异常
     */
    public void lockInterruptible(int lockId) throws InterruptedException {
        Lock lock = locks[lockId & mask];
        lock.lockInterruptibly();
    }

    public void lockInterruptibleSafe(int lockId) {
        try {
            lockInterruptible(lockId);
        } catch (InterruptedException ex) {
            // empty ignore exception
            log.log(Level.WARNING, "lock thread is Interrupted  when lock `{}`", lockId);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 释放锁
     *
     * @param lockId 锁ID
     */
    public void unlock(int lockId) {
        Lock lock = locks[lockId & mask];
        lock.unlock();
    }

    private int formatSize(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;

        if (n < 0) {
            return 1;
        }

        return n >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : n + 1;
    }
}
