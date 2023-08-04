package it.cybersecnatlab.koth23.models;

import java.util.UUID;

public abstract class Entity implements Cell {
    public final Entity.Type type;
    public final String entityId;
    public final SummonerEntity owner;

    public int currentX;
    public int currentY;
    public int life = 0;

    protected Entity(Type type, SummonerEntity owner) {
        this.type = type;
        this.owner = owner;
        this.entityId = UUID.randomUUID().toString();
    }

    public boolean isAlive() {
        return this.life > 0;
    }

    public abstract boolean isPunchable();
    public abstract boolean isMovable();

    public enum Type {
        Summoner,
        Dragon,
        Mole,
        Cat,
        Spectre,
        MagicArrow,
        Wolf,
        Mouse,
        Armadillo
    }
}
