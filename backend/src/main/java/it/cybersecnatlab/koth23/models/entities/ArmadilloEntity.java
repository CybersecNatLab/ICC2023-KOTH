package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import it.cybersecnatlab.koth23.vm.VMExecutionEngine;
import org.json.JSONArray;
import org.json.JSONException;

public final class ArmadilloEntity extends VMEntity {
    public ArmadilloEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Armadillo, summoner, code);
        this.life = Config.ARMADILLO_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return false;
    }

    @Override
    public int getManaCost() {
        return Config.ARMADILLO_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.ARMADILLO_VISION_RADIUS;
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
