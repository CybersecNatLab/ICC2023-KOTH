package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.VMEntity;
import org.json.JSONArray;
import org.json.JSONException;

public final class ArrowEntity extends VMEntity {
    public int speed = 3;

    public ArrowEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.MagicArrow, summoner, code);
        this.life = Config.ARROW_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return true;
    }

    @Override
    public int getManaCost() {
        return Config.ARROW_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.ARROW_VISION_RADIUS;
    }

    @Override
    public int getSpeed() {
        // Arrow moves very fast but slows down

        int currentSpeed = this.speed;
        if (this.speed > 1) {
            this.speed -= 1;
        }

        return currentSpeed;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean isPunchable() {
        return false;
    }

    @Override
    public int getDamage() {
        return Config.ARROW_DAMAGE;
    }
}
