package com.battleship.game;

import com.battleship.board.Position;
import java.io.Serializable;

/**
 * Implementacja wzorca projektowego Command (Polecenie).
 * Obiekt tej klasy enkapsuluje intencję gracza (np. "Strzel w pole B5") wraz ze wszystkimi
 * parametrami niezbędnymi do jej wykonania.
 *
 * Implementuje `Serializable`, ponieważ obiekty Action są przesyłane przez sieć
 * do drugiego gracza w celu synchronizacji stanu gry.
 */
public class Action implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Type { SHOOT, PLACE, OTHER }

    private final Type type;
    private final Position target;

    public Action(Type type, Position target) {
        this.type = type;
        this.target = target;
    }

    public Type type() { return type; }
    public Position target() { return target; }

    @Override
    public String toString() {
        return "Action{" + "type=" + type + ", target=" + target + '}';
    }
}