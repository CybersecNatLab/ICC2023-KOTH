package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import org.json.JSONArray;
import org.json.JSONException;

public final class MouseEntity extends VMEntity {
    public MouseEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Mouse, summoner, code);
        this.life = Config.MOUSE_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return false;
    }

    @Override
    public int getManaCost() {
        return Config.MOUSE_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.MOUSE_VISION_RADIUS;
    }

    @Override
    public boolean isPunchable() {
        return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}
