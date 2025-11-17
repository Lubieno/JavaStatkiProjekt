package com.battleship.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong gen = new AtomicLong();
    public static long next() { return gen.incrementAndGet(); }
}
