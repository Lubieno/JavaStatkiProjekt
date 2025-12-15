package com.battleship.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author Student
 */
public class IdGenerator {
    private static final AtomicLong gen = new AtomicLong();
    public static long next() { return gen.incrementAndGet(); }
}
