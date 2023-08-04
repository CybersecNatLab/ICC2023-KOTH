package it.cybersecnatlab.koth23;

import it.cybersecnatlab.koth23.models.*;
import it.cybersecnatlab.koth23.models.entities.ArmadilloEntity;
import it.cybersecnatlab.koth23.vm.VMException;
import it.cybersecnatlab.koth23.vm.VMExecutionEngine;
import it.cybersecnatlab.koth23.vm.VMInstructionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VMTest {

    private static GameManager newGame() {
        MapGenerator gen = new MapGenerator(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        while (!gen.generateMap()) ;
        return new GameManager(gen.getMap(), gen.getSummoners());
    }

    public static void test_A_star(String[] args) throws VMException, FileNotFoundException {
        var gm = newGame();
        var gameMap = gm.getGameMap();
        Integer startX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0), startY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        while (gameMap.tiles[startX][startY].bgTile != BackgroundTile.EMPTY || gameMap.tiles[startX][startY].entity != null) {
            startX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
            startY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        }
        Integer endX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0), endY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        while (gameMap.tiles[endX][endY].bgTile != BackgroundTile.EMPTY || gameMap.tiles[endX][endY].entity != null) {
            endX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
            endY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        }
        startX = 0;
        startY = 0;
        endX = Config.MAP_WIDTH - 1;
        endY = 0;
        System.out.println("From " + startX + "," + startY + " to " + endX + "," + endY);
        List<int[]> path = gm.performAStar(startX, startY, endX, endY, new BackgroundTile[]{BackgroundTile.LAVA, BackgroundTile.WATER, BackgroundTile.MOUNTAIN});
        int j = 0;
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i)[0] + "," + path.get(i)[1]);
            gameMap.tiles[path.get(i)[0]][path.get(i)[1]].entity = new SummonerEntity("aaaaa", "", path.get(i)[0], path.get(i)[1]);
            j += 1;
        }

        MapWithUpdates mapWithUpdates = new MapWithUpdates(gameMap);
        try (FileOutputStream fos = new FileOutputStream("map.json")) {
            mapWithUpdates.serialize(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws VMException {
        System.out.println("VMTest running");
        MapGenerator m = new MapGenerator(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        Boolean success = false;
        while (!success) {
            System.out.println("Generating map!");
            success = m.generateMap();
        }
        GameMap gameMap = m.getMap();
        List<SummonerEntity> summoners = m.getSummoners();
        java.util.Map<SummonerEntity, List<VMEntity>> summonerSummons = new HashMap<>();
        for (SummonerEntity summoner : summoners) {
            summonerSummons.put(summoner, new ArrayList<>());
        }
        GameMap foggedMap = gameMap.getFogged();

        Integer spawnX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0), spawnY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        while (gameMap.tiles[spawnX][spawnY].bgTile != BackgroundTile.EMPTY || gameMap.tiles[spawnX][spawnY].entity != null) {
            spawnX = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
            spawnY = (int) ((Math.random() * (Config.MAP_WIDTH - 1)) + 0);
        }

        System.out.println("Spawning armadillo at " + spawnX + " " + spawnY);

        JSONArray armadillo_code = getMoveToMyselfPlus3Code();

        armadillo_code = new JSONArray("[{\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"Test log immediate\"}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 12, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 2, \"value\": 1.5}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 2}, {\"type\": 1, \"value\": 100}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 4, \"value\": \"testStringa\"}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected false is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected 1.5 is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected 100 is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected testStringa is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 12, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 0, \"value\": 1}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 0, \"value\": 2}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 0, \"value\": 3}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected false is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected 1.5 is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 6}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected 100 is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 7}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected testStringa is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 8}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 11, \"params\": [{\"type\": 0, \"value\": 5}]}, {\"instruction\": 13, \"params\": [{\"type\": 0, \"value\": 6}]}, {\"instruction\": 15, \"params\": [{\"type\": 0, \"value\": 7}]}, {\"instruction\": 17, \"params\": [{\"type\": 0, \"value\": 8}]}, {\"instruction\": 0, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 0, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 6, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 1, \"value\": 10}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 5, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 1, \"value\": 1}]}, {\"instruction\": 1, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 2, \"value\": 3.14}]}, {\"instruction\": 1, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 2, \"value\": 3.14}]}, {\"instruction\": 7, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 1, \"value\": 10}, {\"type\": 2, \"value\": 3.14}]}, {\"instruction\": 5, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 1, \"value\": 1}]}, {\"instruction\": 2, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 1, \"value\": 777}]}, {\"instruction\": 2, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 1, \"value\": 777}]}, {\"instruction\": 8, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 1, \"value\": 10}, {\"type\": 1, \"value\": 777}]}, {\"instruction\": 5, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 1, \"value\": 1}]}, {\"instruction\": 3, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 4, \"value\": \"test\"}]}, {\"instruction\": 3, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 4, \"value\": \"test\"}]}, {\"instruction\": 9, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 1, \"value\": 10}, {\"type\": 4, \"value\": \"test\"}]}, {\"instruction\": 5, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 1, \"value\": 1}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected booleanList is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected floatList is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 6}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected Integerlist is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 7}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected Stringlist is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 8}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 4, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 1, \"value\": 10}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 4, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 1, \"value\": 10}, {\"type\": 0, \"value\": 11}]}, {\"instruction\": 4, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 1, \"value\": 10}, {\"type\": 0, \"value\": 12}]}, {\"instruction\": 4, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 1, \"value\": 10}, {\"type\": 0, \"value\": 13}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected boolean is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected float is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 11}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected Integer is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 12}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected String is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 13}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 4, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 1, \"value\": 11}, {\"type\": 0, \"value\": 14}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 4, \"value\": \"Expected null is: \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 14}, {\"type\": 0, \"value\": 4}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 4}]}, {\"instruction\": 38, \"params\": [{\"type\": 0, \"value\": 15}]}, {\"instruction\": 42, \"params\": [{\"type\": 1, \"value\": 88}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should not be logged\"}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should be logged\"}]}, {\"instruction\": 38, \"params\": [{\"type\": 0, \"value\": 13}]}, {\"instruction\": 43, \"params\": [{\"type\": 1, \"value\": 92}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should not be logged\"}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should be logged\"}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 1}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 2}, {\"type\": 1, \"value\": 2}]}, {\"instruction\": 12, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 2, \"value\": 1.5}]}, {\"instruction\": 12, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 2, \"value\": 2.5}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 5}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 6}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 4, \"value\": \"aaa\"}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 8}, {\"type\": 4, \"value\": \"bbb\"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 3 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 4.0 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 7}, {\"type\": 0, \"value\": 8}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting aaabbb got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 or 3 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2.5 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2.5 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 19, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting -1 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 19, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting -1.0 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 20, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 20, \"params\": [{\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 3.75 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 20, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 20, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 5.0 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 21, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 0.5 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 21, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 1.66666666 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 21, \"params\": [{\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 1.25 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 31, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 5}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 32 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 30, \"params\": [{\"type\": 0, \"value\": 9}, {\"type\": 1, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 8 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 29, \"params\": [{\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting -8 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 30, \"params\": [{\"type\": 0, \"value\": 9}, {\"type\": 1, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 1073741822 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 9}, {\"type\": 1, \"value\": -8}]}, {\"instruction\": 32, \"params\": [{\"type\": 0, \"value\": 9}, {\"type\": 1, \"value\": 2}, {\"type\": 0, \"value\": 9}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting -4 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 9}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 7}]}, {\"instruction\": 25, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 7}]}, {\"instruction\": 26, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 15 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 7}]}, {\"instruction\": 28, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 13 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 27, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting-10 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 27, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 25, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting false got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 26, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting true got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 3, \"value\": false}]}, {\"instruction\": 28, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting true got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 10, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 3, \"value\": true}]}, {\"instruction\": 27, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting false got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 12, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 2, \"value\": 1.7}]}, {\"instruction\": 33, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}]}, {\"instruction\": 34, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 2}]}, {\"instruction\": 35, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 3}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 1 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 2 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 3}]}, {\"instruction\": 22, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting 1 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 3}]}, {\"instruction\": 37, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"Expecting between 3 and 10 got \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 1, \"value\": 10}]}, {\"instruction\": 14, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 1, \"value\": 3}]}, {\"instruction\": 46, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}]}, {\"instruction\": 45, \"params\": [{\"type\": 1, \"value\": 264}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should not be logged\"}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should be logged\"}]}, {\"instruction\": 46, \"params\": [{\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 0}]}, {\"instruction\": 44, \"params\": [{\"type\": 1, \"value\": 268}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should not be logged\"}]}, {\"instruction\": 50, \"params\": [{\"type\": 4, \"value\": \"This should be logged\"}]}, {\"instruction\": 40, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 3}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"GetOwnInformation return value \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 39, \"params\": [{\"type\": 0, \"value\": 0}, {\"type\": 0, \"value\": 1}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 6}, {\"type\": 0, \"value\": 7}]}, {\"instruction\": 16, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \"GetTileInformation return value \"}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 2}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 3}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 4}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 5}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 6}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 4, \"value\": \" \"}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 18, \"params\": [{\"type\": 0, \"value\": 10}, {\"type\": 0, \"value\": 7}, {\"type\": 0, \"value\": 10}]}, {\"instruction\": 50, \"params\": [{\"type\": 0, \"value\": 10}]}, {\"instruction\": 49, \"params\": []}]");
        System.out.println(armadillo_code);
        VMEntity armadilloEntity = new ArmadilloEntity(summoners.get(0), spawnX, spawnY, armadillo_code);
        gameMap.tiles[spawnX][spawnY].entity = armadilloEntity;

        GameMap deliveryMap = new GameMap(foggedMap); //Construct map to send to player, start from empty and fill-in what it sees
        for (int x = armadilloEntity.currentX - Config.SUMMONER_VISION_RADIUS; x <= armadilloEntity.currentX + Config.SUMMONER_VISION_RADIUS; x++) {
            for (int y = armadilloEntity.currentY - Config.SUMMONER_VISION_RADIUS; y <= armadilloEntity.currentY + Config.SUMMONER_VISION_RADIUS; y++) {
                if (x >= 0 && x < gameMap.width) {
                    if (y >= 0 && y < gameMap.height) {
                        deliveryMap.tiles[x][y] = new Tile(gameMap.tiles[x][y]); //Copy tiles from main map
                        if (deliveryMap.tiles[x][y].bgTile == BackgroundTile.BUSH) {
                            if (Math.abs(x - armadilloEntity.currentX) > Config.BUSH_VIEW_RADIUS || Math.abs(y - armadilloEntity.currentY) > Config.BUSH_VIEW_RADIUS) {
                                deliveryMap.tiles[x][y].entity = null;
                            }
                        }
                    }
                }
            }
        }

        armadilloEntity.setMapTiles(deliveryMap.tiles);
        try {
            int[] answer = armadilloEntity.executeCode();
            System.out.println("Got result from code");
            for (int i = 0; i < answer.length; i++) {
                System.out.println(answer[i]);
            }

        } catch (Exception e) {
            System.out.println("Code crashed!");
            System.out.println(e);
            throw e;
        }

        System.out.println(String.join("\n", armadilloEntity.logs));


    }

    private static JSONArray getMoveTo00Code() {
        JSONArray code = new JSONArray();
        JSONArray params = new JSONArray();
        params.put(getIntegerParam(0));
        params.put(getIntegerParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.MoveTo, params));


        return code;

    }

    private static JSONArray getMoveToMyselfCode() {
        JSONArray code = new JSONArray();
        JSONArray params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(1));
        params.put(getRegistryParam(2));
        params.put(getRegistryParam(3));
        code.put(generateInstruction(VMExecutionEngine.Instructions.GetOwnInformation, params));

        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.MoveTo, params));

        return code;

    }

    private static JSONArray getMoveToMyselfPlus3Code() {
        JSONArray code = new JSONArray();
        JSONArray params = new JSONArray();
        params.put(getRegistryParam(0)); //Get own informations in registries 0,1,2,3
        params.put(getRegistryParam(1));
        params.put(getRegistryParam(2));
        params.put(getRegistryParam(3));
        code.put(generateInstruction(VMExecutionEngine.Instructions.GetOwnInformation, params));

        //Add 3 to registry 0
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getIntegerParam(3));
        params.put(getRegistryParam(0));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Add, params));

        //Add 5 to registry 1
        params = new JSONArray();
        params.put(getRegistryParam(1));
        params.put(getIntegerParam(5));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Add, params));

        //Move to 0 and 1
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.MoveTo, params));
        return code;

    }


    private static JSONArray testConditionalJump() {
        JSONArray code = new JSONArray();


        JSONArray params = new JSONArray();
        params.put(getRegistryParam(1)); //Init registry 1 to value 100
        params.put(getIntegerParam(100));
        code.put(generateInstruction(VMExecutionEngine.Instructions.InitInteger, params));


        params = new JSONArray();
        params.put(getRegistryParam(0)); //Init registry 0 to value 1
        params.put(getIntegerParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.InitInteger, params));

        params = new JSONArray();
        params.put(getRegistryParam(0)); //Add 1 to registry 0
        params.put(getIntegerParam(1));
        params.put(getRegistryParam(0));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Add, params));

        //Cmp registry 0 to 10
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getIntegerParam(10));
        code.put(generateInstruction(VMExecutionEngine.Instructions.CMP, params));

        params = new JSONArray(); //Jump to 2 if registry 0-10<0
        params.put(getIntegerParam(2));
        code.put(generateInstruction(VMExecutionEngine.Instructions.JMPL0, params));

        //Move to 0 and 1
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.MoveTo, params));
        return code;
    }

    private static JSONArray moveToFlooreSQRTRoot() {
        JSONArray code = new JSONArray();
        JSONArray params = new JSONArray();
        params.put(getRegistryParam(0)); //Get own informations in registries 0,1,2,3
        params.put(getRegistryParam(1));
        params.put(getRegistryParam(2));
        params.put(getRegistryParam(3));
        code.put(generateInstruction(VMExecutionEngine.Instructions.GetOwnInformation, params));

        //Sqrt registry 0
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(0));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Sqrt, params));

        //Sqrt registry 1
        params = new JSONArray();
        params.put(getRegistryParam(1));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Sqrt, params));

        //Floor registry 0
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(0));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Floor, params));

        //Ceil registry 1
        params = new JSONArray();
        params.put(getRegistryParam(1));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.Ceil, params));

        //Move to 0 and 1
        params = new JSONArray();
        params.put(getRegistryParam(0));
        params.put(getRegistryParam(1));
        code.put(generateInstruction(VMExecutionEngine.Instructions.MoveTo, params));
        return code;
    }


    private static JSONObject generateInstruction(VMExecutionEngine.Instructions instruction, JSONArray params) {
        JSONObject objInstruction = new JSONObject();
        objInstruction.put("instruction", instruction.ordinal());
        objInstruction.put("params", params);
        return objInstruction;
    }


    private static JSONObject getRegistryParam(Integer registry_number) {
        JSONObject param = new JSONObject();
        param.put("type", VMInstructionUtils.ParameterType.REGISTRY.ordinal());
        param.put("value", registry_number);
        return param;
    }

    private static JSONObject getIntegerParam(Integer value) {
        JSONObject param = new JSONObject();
        param.put("type", VMInstructionUtils.ParameterType.INTEGER.ordinal());
        param.put("value", value);
        return param;
    }

    private static JSONObject getFloatParam(Float value) {
        JSONObject param = new JSONObject();
        param.put("type", VMInstructionUtils.ParameterType.FLOAT.ordinal());
        param.put("value", value);
        return param;
    }

    private static JSONObject getBooleanParam(Boolean value) {
        JSONObject param = new JSONObject();
        param.put("type", VMInstructionUtils.ParameterType.BOOLEAN.ordinal());
        param.put("value", value);
        return param;
    }

    private static JSONObject getStringParam(String value) {
        JSONObject param = new JSONObject();
        param.put("type", VMInstructionUtils.ParameterType.STRING.ordinal());
        param.put("value", value);
        return param;
    }


}
