package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import it.cybersecnatlab.koth23.vm.VMExecutionEngine;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

public final class CatEntity extends VMEntity {
    public CatEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Cat, summoner, code);
        this.life = Config.CAT_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return false;
    }

    @Override
    public int getManaCost() {
        return Config.CAT_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.CAT_VISION_RADIUS;
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
