package it.cybersecnatlab.koth23;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Config {
    // Global configs
    public static Integer MAX_WAIT_FOR_MOVE_IN_MS = 100;
    public static String[] PLAYER_CONNECTION_URIS = new String[]{"localhost:8001", "localhost:8002"};
    public static String[] PLAYER_NAMES = new String[]{"NPC 1", "NPC 2"};
    public static int[] FINAL_SCOREBOARD_SCORES = new int[]{10, 7, 5, 4, 3, 2, 1, 0};
    public static String OUTPUT_INFO_DIRECTORY = ".";

    // Game config
    public static Integer MAP_SHRINK_START_TICK = 20; // At which tick the map starts shrinking
    public static Integer MAP_SHRINK_DELTA_TICK = 10; // When map is shrinking, ticks in between two shrinking.
    public static Integer MAP_GAS_DAMAGE = 20;
    public static Boolean SHOULD_SHRINK_MAP = Boolean.TRUE; // WARNING: setting this to false doesn't guarantee the game will end

    public static Integer POINTS_ON_KILL = 5;

    // Generation config
    public static Integer MAP_WIDTH = 40;
    public static Integer MAP_HEIGHT = 40;
    public static Float MOUNTAIN_DENSITY = 0.15F;
    public static Integer MOUNTAIN_LINKAGE = 10;
    public static Float BUSH_DENSITY = 0.15F;
    public static Integer BUSH_LINKAGE = 10;
    public static Float WATER_DENSITY = 0.01F;
    public static Integer WATER_LINKAGE = 10;
    public static Float LAVA_DENSITY = 0.01F;
    public static Integer LAVA_LINKAGE = 10;

    // Summoners config
    public static Integer MAX_SUMMONER_LIFE = 100;
    public static Integer MAX_SUMMONER_MANA = 100;
    public static Integer MANA_REGEN_AMOUNT = 1;
    public static Integer MANA_REGEN_TICK_DELTA = 1;
    public static Integer HEAL_AMOUNT_ON_KILL = 50;
    public static Integer SUMMONER_VISION_RADIUS = 10;
    public static Integer BUSH_VIEW_RADIUS = 2;
    public static Integer PUNCH_DAMAGE = 5;

    // Summons config
    public static Integer ARMADILLO_LIFE = 200;
    public static Integer ARMADILLO_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer ARMADILLO_MANA_COST = 20;

    public static Integer DRAGON_LIFE = 1; //Immediately killed only by arrows
    public static Integer DRAGON_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer DRAGON_MANA_COST = 20;
    public static Integer DRAGON_GIVEN_VISION_RADIUS = 5;

    public static Integer MOLE_LIFE = 40;
    public static Integer MOLE_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer MOLE_MANA_COST = 20;
    public static Integer MOLE_ABILITY_COUNT = 3;

    public static Integer CAT_LIFE = 20;
    public static Integer CAT_MANA_COST = 20;
    public static Integer CAT_VISION_RADIUS = SUMMONER_VISION_RADIUS;

    public static Integer SPECTRE_LIFE = 10;
    public static Integer SPECTRE_MANA_COST = 10;
    public static Integer SPECTRE_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer SPECTRE_GIVEN_VISION_RADIUS = 2;

    public static Integer ARROW_LIFE = 1; // Dies if hit
    public static Integer ARROW_MANA_COST = 10;
    public static Integer ARROW_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer ARROW_DAMAGE = 25; // High damage

    public static Integer WOLF_LIFE = 30;
    public static Integer WOLF_MANA_COST = 30;
    public static Integer WOLF_VISION_RADIUS = SUMMONER_VISION_RADIUS;
    public static Integer WOLF_DAMAGE = 25;
    public static Integer WOLF_ABILITY_COUNT = 3;
    public static Integer WOLF_ABILITY_RADIUS = 3;
    public static Integer WOLF_ABILITY_DAMAGE = 40;

    public static Integer MOUSE_LIFE = 10;
    public static Integer MOUSE_MANA_COST = 20;
    public static Integer MOUSE_MAX_TICK_ALIVE = 20;
    public static Integer MOUSE_VISION_RADIUS = SUMMONER_VISION_RADIUS;

    // VM config
    public static Integer VM_INITIAL_GAS = 20000;
    public static Integer VM_REGISTRIES_NUMBER = 32;

    public static void loadConfigFromFile(String filename) throws IOException {
        JSONObject config = new JSONObject(Files.readString(Paths.get(filename)));
        for (String key : config.keySet()) {
            if (key.equals("PLAYER_CONNECTION_URIS")) {
                JSONArray uris = config.getJSONArray(key);
                Config.PLAYER_CONNECTION_URIS = new String[uris.length()];
                for (int i = 0; i < uris.length(); i++)
                    Config.PLAYER_CONNECTION_URIS[i] = uris.getString(i);
            }else if(key.equals("PLAYER_NAMES")) {
                JSONArray names = config.getJSONArray(key);
                Config.PLAYER_NAMES = new String[names.length()];
                for (int i = 0; i < names.length(); i++)
                    Config.PLAYER_NAMES[i] = names.getString(i);
            }else if(key.equals("FINAL_SCOREBOARD_SCORES")) {
                JSONArray scores = config.getJSONArray(key);
                Config.FINAL_SCOREBOARD_SCORES = new int[scores.length()];
                for (int i = 0; i < scores.length(); i++)
                    Config.FINAL_SCOREBOARD_SCORES[i] = scores.getInt(i);
            } else {
                try {
                    Object value = config.get(key);
                    Field field = Config.class.getDeclaredField(key);
                    if (value instanceof BigDecimal) field.set(null, ((BigDecimal) value).floatValue());
                    else field.set(null, value);
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(String.format("Failed loading config %s!", key));
                }
            }
        }

        if (!Config.OUTPUT_INFO_DIRECTORY.endsWith(System.getProperty("file.separator"))) {
            Config.OUTPUT_INFO_DIRECTORY += System.getProperty("file.separator");
        }
    }
}
