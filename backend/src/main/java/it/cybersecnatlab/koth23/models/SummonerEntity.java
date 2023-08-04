package it.cybersecnatlab.koth23.models;

import it.cybersecnatlab.koth23.Config;

/**
 * The SummonerEntity is the player entity, runs from user commands.
 */
public final class SummonerEntity extends Entity {
    private final String summonerName;
    private final String serverHost;
    private int mana;
    private SummonerEntity lastHitBy = null;
    private int score = 0;

    public SummonerEntity(String summonerName, String serverHost, int currentX, int currentY) {
        super(Type.Summoner, null);
        this.summonerName = summonerName;
        this.serverHost = serverHost;
        this.life = Config.MAX_SUMMONER_LIFE;
        this.mana = Config.MAX_SUMMONER_MANA;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    public SummonerEntity getLastHitBy() {
        return this.lastHitBy;
    }

    public void setLastHitBy(SummonerEntity e) {
        this.lastHitBy = e;
    }

    public int getMana() {
        return this.mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public String getSummonerName() {
        return this.summonerName;
    }

    public String getServerHost() {
        return serverHost;
    }

    @Override
    public boolean isPunchable() {
        return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    public void addScore(int score){
        this.score+=score;
    }

    public int getScore(){
        return this.score;
    }
}
