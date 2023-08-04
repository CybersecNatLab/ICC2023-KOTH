package it.cybersecnatlab.koth23.models;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A Map is the base game map, immutable.
 */
public final class GameMap {
    public final int width, height;
    public Tile[][] tiles;

    public GameMap(int width, int height, Tile[][] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public GameMap(GameMap other) {
        this.width = other.width;
        this.height = other.height;
        this.tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(other.tiles[x][y]);
            }
        }
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public GameMap getFogged() {
        Tile[][] tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(this.tiles[x][y]);
                tiles[x][y].entity = null;
                tiles[x][y].bgTile = (tiles[x][y].bgTile == BackgroundTile.LAVA || tiles[x][y].bgTile == BackgroundTile.WATER) ? BackgroundTile.UNKNOWN_LIQUID : BackgroundTile.UNKNOWN;
            }
        }

        return new GameMap(width, height, tiles);
    }

    public JSONObject toJson() {
        JSONObject mapJsonObject = new JSONObject();
        mapJsonObject.put("type", "fullMap");
        mapJsonObject.put("width", this.width);
        mapJsonObject.put("height", this.height);
        JSONArray columns = new JSONArray();
        for (Tile[] mapColumn : this.tiles) {
            JSONArray items = new JSONArray();
            for (Tile tile : mapColumn) {
                JSONObject tileObject = new JSONObject();
                tileObject.put("background", tile.bgTile.type.ordinal());
                if (tile.entity != null) {
                    JSONObject entityObject = new JSONObject();
                    if (tile.entity instanceof SummonerEntity) {
                        entityObject.put("type", "summoner");
                        entityObject.put("id", tile.entity.entityId);
                        entityObject.put("mana", ((SummonerEntity) tile.entity).getMana());
                        entityObject.put("life", tile.entity.life);
                    } else if (tile.entity instanceof VMEntity) {
                        entityObject.put("type", "VMentity");
                        entityObject.put("VMType", tile.entity.type.ordinal());
                        entityObject.put("id", tile.entity.entityId);
                        entityObject.put("summonerID", tile.entity.owner.entityId);
                        entityObject.put("life", tile.entity.life);
                    }
                    tileObject.put("entity", entityObject);
                }
                tileObject.put("isDamaging", tile.isDamaging);
                items.put(tileObject);
            }
            columns.put(items);
        }
        mapJsonObject.put("map", columns);
        return mapJsonObject;
    }
}
