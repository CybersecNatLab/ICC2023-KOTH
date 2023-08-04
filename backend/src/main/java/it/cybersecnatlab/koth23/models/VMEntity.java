package it.cybersecnatlab.koth23.models;

import it.cybersecnatlab.koth23.Config;
import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMExecutionEngine;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A VM entity is an entity with a running virtual machine.
 */
public abstract class VMEntity extends Entity {
    private final GameMap map = new GameMap(Config.MAP_WIDTH, Config.MAP_HEIGHT, null);
    public final VMExecutionEngine engine;
    private int tickAlive = 0;
    protected int abilityUsesRemaining = 0;

    // FIXME: expand on this concept or rewrite it
    public List<String> logs = new ArrayList<>();

    public VMEntity(Type type, SummonerEntity owner, JSONArray code) {
        super(type, owner);
        this.engine = new VMExecutionEngine(code, this.map, this);
    }

    public void setMapTiles(Tile[][] tiles) {
        // FIXME: can we avoid making a copy everytime?
        this.map.setTiles(tiles);
    }

    public int[] executeCode() throws JSONException, VMException {
        this.tickAlive += 1;
        return this.engine.executeCode();
    }

    public Map<Entity, Integer> executeAbility(int[] abilityInfo, GameMap gameMap, MapWithUpdates mapWithUpdates, List<SummonerEntity> scoreboard, Map<SummonerEntity, List<VMEntity>> summonerSummons) {
        return null;
    }

    public int getTickAlive() {
        return tickAlive;
    }

    public int getAbilityUsesRemaining() {
        return abilityUsesRemaining;
    }

    public abstract boolean canFlyOverLiquids();

    public abstract int getManaCost();

    public abstract int getVisionRadius();

    public int givenVisionRadius() {
        return 0;
    }

    public int getSpeed() {
        return 1;
    }

    public int getDamage() {
        return 0;
    }
}
