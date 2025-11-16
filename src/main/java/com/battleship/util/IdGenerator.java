package com.battleship.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong id = new AtomicLong(1);

    public static long next() {
        return id.getAndIncrement();
    }
}
