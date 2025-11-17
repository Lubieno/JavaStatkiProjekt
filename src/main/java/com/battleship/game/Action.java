package com.battleship.game;

import com.battleship.board.Position;

/**
 * @Author Student
 *
 * Reprezentuje akcję w grze, np. oddanie strzału w pozycję.
 */
public class Action {
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
