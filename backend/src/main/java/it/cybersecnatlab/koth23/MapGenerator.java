package it.cybersecnatlab.koth23;

import it.cybersecnatlab.koth23.models.BackgroundTile;
import it.cybersecnatlab.koth23.models.GameMap;
import it.cybersecnatlab.koth23.models.SummonerEntity;
import it.cybersecnatlab.koth23.models.Tile;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MapGenerator {
    private static final Logger LOGGER = Logger.getLogger(MapGenerator.class.getSimpleName());
    private final int width;
    private final int height;
    private GameMap map;
    private List<SummonerEntity> summoners;

    public MapGenerator(int width, int height) {
        if (width < 5 || height < 5) {
            throw new IllegalArgumentException("Map needs to be big enough");
        }

        this.width = width;
        this.height = height;
    }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private int[] getRandomNeighbour(int x, int y) {
        List<int[]> choices = Arrays.asList(new int[][]{{x - 1, y - 1}, {x - 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}, {x + 1, y}, {x + 1, y - 1}, {x, y - 1}});
        Collections.shuffle(choices);
        for (int[] choice : choices)
            if (choice[0] >= 0 && choice[0] < this.width && choice[1] >= 0 && choice[1] < this.height)
                return choice;

        return new int[]{-1, -1};
    }

    private int[] getRandomCloseNeighbour(int x, int y) {
        List<int[]> choices = Arrays.asList(new int[][]{{x - 1, y}, {x, y + 1}, {x + 1, y}, {x, y - 1}});
        Collections.shuffle(choices);
        for (int[] choice : choices)
            if (choice[0] >= 0 && choice[0] < this.width && choice[1] >= 0 && choice[1] < this.height)
                return choice;

        return new int[]{-1, -1};
    }

    private List<Tile> getNeighbours(Tile[][] map, int x, int y) {
        List<Tile> neighbourTiles = new ArrayList<>();
        if (x > 0)
            neighbourTiles.add(map[x - 1][y]);
        if (x + 1 < this.width)
            neighbourTiles.add(map[x + 1][y]);
        if (y > 0)
            neighbourTiles.add(map[x][y - 1]);
        if (y + 1 < this.height)
            neighbourTiles.add(map[x][y + 1]);
        return neighbourTiles;
    }

    private Tile[][] DFSColouring(Tile[][] map, int startX, int startY) {
        Tile[][] newTileMap = new Tile[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                newTileMap[x][y] = new Tile(map[x][y]);

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});
        while (!stack.empty()) {
            int[] item = stack.pop();
            newTileMap[item[0]][item[1]].bgTile = BackgroundTile.UNUSED;
            List<Tile> neighbours = getNeighbours(newTileMap, item[0], item[1]);
            for (Tile neighbour : neighbours) {
                if (neighbour.bgTile != BackgroundTile.UNUSED
                        && neighbour.bgTile != BackgroundTile.LAVA
                        && neighbour.bgTile != BackgroundTile.WATER
                        && neighbour.bgTile != BackgroundTile.MOUNTAIN) {
                    stack.push(new int[]{neighbour.x, neighbour.y});
                }
            }
        }

        return newTileMap;
    }

    public boolean generateMap() {
        if (map != null) {
            throw new IllegalStateException("Map already generated!");
        }

        Tile[][] tiles = new Tile[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                tiles[x][y] = new Tile(x, y);

        int minimalDimension = Math.min(width, height);

        // Generating spawn points equidistant on a circle of random radius
        int spawnCircleRadius = getRandomNumber((minimalDimension / 4), minimalDimension / 2);
        int circleCenterX = width / 2, circleCenterY = height / 2;

        List<int[]> spawnPoints = new ArrayList<>();
        List<Integer> spawnOrder = IntStream.range(0, Config.PLAYER_NAMES.length).boxed().collect(Collectors.toList());
        Collections.shuffle(spawnOrder);

        this.summoners = new ArrayList<>();
        for (int i = 0; i < Config.PLAYER_NAMES.length; i++) {
            int x = (int) Math.round(circleCenterX + spawnCircleRadius * Math.cos(2 * Math.PI * i / Config.PLAYER_NAMES.length));
            int y = (int) Math.round(circleCenterY + spawnCircleRadius * Math.sin(2 * Math.PI * i / Config.PLAYER_NAMES.length));
            LOGGER.info("Spawning %d at %d,%d".formatted(spawnOrder.get(i), x, y));

            SummonerEntity summoner = new SummonerEntity(Config.PLAYER_NAMES[spawnOrder.get(i)], Config.PLAYER_CONNECTION_URIS[spawnOrder.get(i)],  x, y);
            this.summoners.add(summoner);
            tiles[x][y].entity = summoner;

            spawnPoints.add(new int[]{x, y});
        }

        // Generating mountains
        int totalMountains = 0, targetTotalMountains = Math.round(width * height * Config.MOUNTAIN_DENSITY);
        while (totalMountains < targetTotalMountains) {
            int x = getRandomNumber(0, width), y = getRandomNumber(0, height);
            int linkageSize = getRandomNumber(Config.MOUNTAIN_LINKAGE / 2, Config.MOUNTAIN_LINKAGE);
            for (int i = 0; i < linkageSize; i++) {
                if (tiles[x][y].entity == null && tiles[x][y].bgTile == BackgroundTile.EMPTY) {
                    tiles[x][y].bgTile = BackgroundTile.MOUNTAIN;
                    totalMountains += 1;
                }

                int[] tmp = this.getRandomNeighbour(x, y);
                if (tmp[0] == -1 && tmp[1] == -1) {
                    break;
                }

                x = tmp[0];
                y = tmp[1];
            }
        }

        // Generating bushes
        int totalBushes = 0, targetTotalBushes = Math.round(width * height * Config.BUSH_DENSITY);
        while (totalBushes < targetTotalBushes) {
            int x = getRandomNumber(0, width), y = getRandomNumber(0, height);
            int linkageSize = getRandomNumber(Config.BUSH_LINKAGE / 2, Config.BUSH_LINKAGE);
            for (int i = 0; i < linkageSize; i++) {
                if (tiles[x][y].entity == null && tiles[x][y].bgTile == BackgroundTile.EMPTY) {
                    tiles[x][y].bgTile = BackgroundTile.BUSH;
                    totalBushes += 1;
                }

                int[] tmp = this.getRandomCloseNeighbour(x, y);
                if (tmp[0] == -1 && tmp[1] == -1) {
                    break;
                }

                x = tmp[0];
                y = tmp[1];
            }
        }


        // Generating lava lakes
        int totalLavaLakes = 0, targetTotalLavaLakes = Math.round(width * height * Config.LAVA_DENSITY);
        while (totalLavaLakes < targetTotalLavaLakes) {
            int x = getRandomNumber(0, width), y = getRandomNumber(0, height);
            int linkageSize = getRandomNumber(Config.LAVA_LINKAGE / 2, Config.LAVA_LINKAGE);
            for (int i = 0; i < linkageSize; i++) {

                if (tiles[x][y].entity == null && tiles[x][y].bgTile == BackgroundTile.EMPTY) {
                    tiles[x][y].bgTile = BackgroundTile.LAVA;
                    totalLavaLakes += 1;
                }

                int[] tmp = this.getRandomCloseNeighbour(x, y);
                if (tmp[0] == -1 && tmp[1] == -1) {
                    break;
                }

                x = tmp[0];
                y = tmp[1];
            }
        }


        // Generating water lakes
        int totalWaterLakes = 0, targetTotalWaterLakes = Math.round(width * height * Config.WATER_DENSITY);
        while (totalWaterLakes < targetTotalWaterLakes) {
            int x = getRandomNumber(0, width), y = getRandomNumber(0, height);
            int linkageSize = getRandomNumber(Config.WATER_LINKAGE / 2, Config.WATER_LINKAGE);
            for (int i = 0; i < linkageSize; i++) {
                List<Tile> neighbourTiles = getNeighbours(tiles, x, y);

                if (tiles[x][y].entity == null && tiles[x][y].bgTile == BackgroundTile.EMPTY
                        // Ensure no lava near water
                        && neighbourTiles.stream().noneMatch(t -> t != null && t.bgTile == BackgroundTile.LAVA)) {
                    tiles[x][y].bgTile = BackgroundTile.WATER;
                    totalWaterLakes += 1;
                }

                int[] tmp = this.getRandomCloseNeighbour(x, y);
                if (tmp[0] == -1 && tmp[1] == -1) {
                    break;
                }

                x = tmp[0];
                y = tmp[1];
            }
        }

        // We need to ensure every player can reach every other player.
        int[] firstSpawnPoint = spawnPoints.get(0);
        Tile[][] colouredMap = DFSColouring(tiles, firstSpawnPoint[0], firstSpawnPoint[1]);
        for (int[] spawnPoint : spawnPoints) {
            if (colouredMap[spawnPoint[0]][spawnPoint[1]].bgTile != BackgroundTile.UNUSED) {
                LOGGER.warning("Invalid map generated, cannot reach %d,%d from %d,%d".formatted(spawnPoint[0], spawnPoint[1], firstSpawnPoint[0], firstSpawnPoint[1]));
                return false;
            }
        }

        this.map = new GameMap(this.width, this.height, tiles);
        return true;
    }

    public GameMap getMap() {
        return this.map;
    }

    public List<SummonerEntity> getSummoners() {
        return this.summoners;
    }
}
