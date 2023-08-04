package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import it.cybersecnatlab.koth23.models.updates.AbilityUpdate;
import org.json.JSONArray;
import org.json.JSONException;

public final class SpectreEntity extends VMEntity {
    public AbilityUpdate.Direction direction;

    public SpectreEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code, AbilityUpdate.Direction d) throws JSONException {
        super(Type.Spectre, summoner, code);
        this.life = Config.SPECTRE_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
        this.direction = d;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return true;
    }

    @Override
    public int getManaCost() {
        return Config.SPECTRE_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.SPECTRE_VISION_RADIUS;
    }

    @Override
    public int givenVisionRadius() {
        return Config.SPECTRE_GIVEN_VISION_RADIUS;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean isPunchable() {
        return false;
    }
}
