package it.cybersecnatlab.koth23.models.entities;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.GameManager;
import it.cybersecnatlab.koth23.models.*;
import it.cybersecnatlab.koth23.models.updates.AbilityUpdate;
import it.cybersecnatlab.koth23.models.updates.BackgroundTileChangeUpdate;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public final class MoleEntity extends VMEntity {
    public MoleEntity(SummonerEntity summoner, int currentX, int currentY, JSONArray code) throws JSONException {
        super(Type.Mole, summoner, code);
        this.life = Config.MOLE_LIFE;
        this.currentX = currentX;
        this.currentY = currentY;
        this.abilityUsesRemaining = Config.MOLE_ABILITY_COUNT;
    }

    @Override
    public java.util.Map<Entity, Integer> executeAbility(int[] abilityInfo, GameMap gameMap, MapWithUpdates mapWithUpdates, List<SummonerEntity> scoreboard, java.util.Map<SummonerEntity, List<VMEntity>> summonerSummons) {
        if (this.abilityUsesRemaining <= 0) {
            return null;
        }
        if (abilityInfo.length == 1 && abilityInfo[0] >= 0 && abilityInfo[0] < AbilityUpdate.Direction.values().length) {
            AbilityUpdate.Direction d = AbilityUpdate.Direction.values()[abilityInfo[0]];
            int[] data = GameManager.getTileAtDirection(currentX, currentY, d);
            Tile t = gameMap.tiles[data[0]][data[1]];
                if (t.bgTile != BackgroundTile.LAVA && t.bgTile != BackgroundTile.WATER && t.bgTile != BackgroundTile.MOUNTAIN) {
                    t.bgTile = BackgroundTile.MOUNTAIN;
                    this.abilityUsesRemaining -= 1;
                    Update abilityUsageUpdate = new AbilityUpdate(this.entityId, this.abilityUsesRemaining, "orogeny", d);
                    mapWithUpdates.addUpdate(abilityUsageUpdate);
                    Update bgTileChangeUpdate = new BackgroundTileChangeUpdate(t.x, t.y, BackgroundTile.Type.Mountain);
                    mapWithUpdates.addUpdate(bgTileChangeUpdate);
                }
        }
        return null;
    }

    @Override
    public boolean canFlyOverLiquids() {
        return false;
    }

    @Override
    public int getManaCost() {
        return Config.MOLE_MANA_COST;
    }

    @Override
    public int getVisionRadius() {
        return Config.MOLE_VISION_RADIUS;
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
