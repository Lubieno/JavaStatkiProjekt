package com.battleship.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generator unikalnych identyfikatorów numerycznych dla obiektów domenowych lub zdarzeń.
 * Klasa wykorzystuje mechanizm `AtomicLong`, który gwarantuje atomowość operacji inkrementacji
 * na poziomie procesora (instrukcje CAS - Compare-And-Swap).
 *
 * Dzięki temu generator jest bezpieczny wątkowo (thread-safe) i może być współdzielony
 * między wątkami logiki gry a wątkami sieciowymi bez ryzyka wystąpienia hazardu (race condition).
 */
public class IdGenerator {
    private static final AtomicLong gen = new AtomicLong();

    /**
     * Pobiera aktualną wartość i atomowo ją inkrementuje.
     *
     * @return Unikalny identyfikator typu long.
     */
    public static long next() { return gen.incrementAndGet(); }
}