package it.cybersecnatlab.koth23.models;

import it.cybersecnatlab.koth23.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * MapWithMoves is a map that is being played or has been played.
 */
public final class MapWithUpdates {
    public final GameMap baseMap;
    public final List<Update> updates;

    public MapWithUpdates(GameMap baseMap) {
        this.baseMap = new GameMap(baseMap);
        this.updates = new LinkedList<>();
    }

    public void addUpdate(Update u) {
        updates.add(u);
    }

    public void serialize(OutputStream out) throws IOException {
        JSONArray mapWithUpdates = new JSONArray();
        JSONObject mapJsonObject = new JSONObject();
        mapJsonObject.put("type", "fullMap");
        mapJsonObject.put("width", baseMap.width);
        mapJsonObject.put("height", baseMap.height);
        JSONArray columns = new JSONArray();
        for (Tile[] mapColumn : baseMap.tiles) {
            JSONArray items = new JSONArray();
            for (Tile tile : mapColumn) {
                JSONObject tileObject = new JSONObject();
                tileObject.put("background", tile.bgTile.type.ordinal());
                if (tile.entity != null) {
                    JSONObject entityObject = new JSONObject();
                    if (tile.entity instanceof SummonerEntity) {
                        entityObject.put("type", "summoner");
                        entityObject.put("name", ((SummonerEntity) tile.entity).getSummonerName());
                        entityObject.put("id", tile.entity.entityId);
                        entityObject.put("mana", Config.MAX_SUMMONER_MANA);
                        entityObject.put("life", Config.MAX_SUMMONER_LIFE);
                    } else if (tile.entity instanceof VMEntity) {
                        entityObject.put("type", "VMentity");
                        entityObject.put("VMType", tile.entity.type.ordinal());
                        entityObject.put("id", tile.entity.entityId);
                        entityObject.put("summonerID", tile.entity.owner.entityId);
                        entityObject.put("life", tile.entity.life);
                    }
                    tileObject.put("entity", entityObject);
                }
                items.put(tileObject);
            }
            columns.put(items);
        }
        mapJsonObject.put("map", columns);
        mapWithUpdates.put(mapJsonObject);

        for (Update u : updates) {
            JSONObject update = new JSONObject();
            update.put("type", u.getUpdateType().name);
            update.put("data", u.getUpdateData());
            mapWithUpdates.put(update);
        }

        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeBytes(mapWithUpdates.toString());
        }
    }
}
