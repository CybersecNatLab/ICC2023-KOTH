package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import org.json.JSONArray;
import org.json.JSONException;

public final class DragonEntity extends VMEntity {
    public DragonEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Dragon, summoner, code);
        this.life = Config.DRAGON_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return true;
    }

    @Override
    public int getManaCost() {
        return Config.DRAGON_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.DRAGON_VISION_RADIUS;
    }

    @Override
    public int givenVisionRadius() {
        return Config.DRAGON_GIVEN_VISION_RADIUS;
    }

    @Override
    public boolean isPunchable() {
        return false;
    }

    @Override
    public boolean isMovable() {
        return false;
    }
}
