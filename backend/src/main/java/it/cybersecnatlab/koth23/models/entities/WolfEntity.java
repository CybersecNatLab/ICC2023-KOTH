package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.models.*;
import it.cybersecnatlab.koth23.models.updates.AbilityUpdate;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

public final class WolfEntity extends VMEntity {
    public WolfEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Wolf, summoner, code);
        this.life = Config.WOLF_LIFE;
        this.abilityUsesRemaining = Config.WOLF_ABILITY_COUNT;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    @Override
    public java.util.Map<Entity, Integer> executeAbility(int[] abilityInfo, GameMap gameMap, MapWithUpdates mapWithUpdates, List<SummonerEntity> scoreboard, java.util.Map<SummonerEntity, List<VMEntity>> summonerSummons) {
        if (this.abilityUsesRemaining <= 0) {
            return null;
        }
        java.util.Map<Entity, Integer> damagedEntities = new HashMap<>();
        Update abilityUpdate = new AbilityUpdate(this.entityId, this.abilityUsesRemaining, "wolfAreaAttack");
        mapWithUpdates.addUpdate(abilityUpdate);
        this.abilityUsesRemaining -= 1;
        for (int i = currentX - Config.WOLF_ABILITY_RADIUS; i <= currentX + Config.WOLF_ABILITY_RADIUS; i++) { //Area damage
            for (int j = currentY - Config.WOLF_ABILITY_RADIUS; j <= currentY + Config.WOLF_ABILITY_RADIUS; j++) {
                if (i >= 0 && i < gameMap.width && j >= 0 && j < gameMap.height) { //If inbound
                    Tile t = gameMap.tiles[i][j];
                    if (t.entity != null) { //If entity
                        Entity e = t.entity;
                        if (e instanceof SummonerEntity) { //If summoner
                            if (!e.equals(this.owner)) { //And not owner
                                damagedEntities.put(e, Config.WOLF_ABILITY_DAMAGE);
                            }
                        } else if (e instanceof VMEntity) {
                            if (!e.owner.equals(this.owner)) { //If entities not from same owner
                                damagedEntities.put(e, Config.WOLF_ABILITY_DAMAGE);
                            }
                        }
                    }
                }
            }
        }
        return damagedEntities;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return false;
    }

    @Override
    public int getManaCost() {
        return Config.WOLF_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.WOLF_VISION_RADIUS;
    }

    @Override
    public boolean isPunchable() {
        return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public int getDamage() {
        return Config.WOLF_DAMAGE;
    }
}
