package com.battleship.util;

import java.util.UUID;

/**
 * Generator unikalnych identyfikatorów (np. graczy lub sesji).
 */
public class IdGenerator {

    public static String generujID() {
        return UUID.randomUUID().toString();
    }
}
