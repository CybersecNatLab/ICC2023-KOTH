package it.cybersecnatlab.koth23;

import it.cybersecnatlab.koth23.models.*;
import it.cybersecnatlab.koth23.models.entities.*;
import it.cybersecnatlab.koth23.models.updates.*;
import it.cybersecnatlab.koth23.vm.VMException;
import jakarta.websocket.DeploymentException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GameManager {
    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getSimpleName());
    private final GameMap gameMap;
    private final GameMap foggedGameMap;
    private final List<SummonerEntity> summoners;
    private final Map<SummonerEntity, WebsocketClient> clients;
    private final Map<SummonerEntity, List<VMEntity>> summonerSummons;
    private final List<SummonerEntity> scoreboard;
    private final MapWithUpdates mapWithUpdates;

    public GameManager(GameMap map, List<SummonerEntity> summoners) {
        this.gameMap = map;
        this.mapWithUpdates = new MapWithUpdates(gameMap);

        this.foggedGameMap = map.getFogged();
        this.summoners = summoners;

        this.summonerSummons = new HashMap<>();
        for (SummonerEntity summoner : summoners)
            summonerSummons.put(summoner, new ArrayList<>());

        this.scoreboard = new ArrayList<>();
        this.clients = new HashMap<>();
    }

    public void connectToPlayers() {
        for (SummonerEntity summoner : summoners) {
            LOGGER.info("Attempting connection to %s at ws://%s".formatted(summoner.getSummonerName(), summoner.getServerHost()));

            try {
                var client = WebsocketClient.connect(new URI("ws://" + summoner.getServerHost()));
                clients.put(summoner, client);

                LOGGER.info("Connection to %s was successful".formatted(summoner.getSummonerName()));
            } catch (URISyntaxException | DeploymentException | IOException | TimeoutException ex) {
                LOGGER.log(Level.SEVERE, "Connection to %s failed".formatted(summoner.getSummonerName()), ex);
                killEntity(summoner);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    public static int[] getTileAtDirection(int startX, int startY, AbilityUpdate.Direction direction) {
        return switch (direction) {
            case Up -> new int[]{startX, startY - 1};
            case Down -> new int[]{startX, startY + 1};
            case Left -> new int[]{startX - 1, startY};
            case Right -> new int[]{startX + 1, startY};
        };
    }

    public void startGame() {
        // Send game start packets
        for (SummonerEntity summoner : summoners) {
            if (!summoner.isAlive())
                continue;

            var summonerClient = clients.get(summoner);
            if (summonerClient == null || !summonerClient.isConnected()) {
                LOGGER.info("Summoner %s disconnected early!".formatted(summoner.getSummonerName()));
                killEntity(summoner);
                continue;
            }

            try {
                JSONObject data = new JSONObject();
                data.put("id", summoner.entityId);
                data.put("name", summoner.getSummonerName());
                data.put("gameStatus", "start");
                summonerClient.sendMessage(data.toString());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Summoner %s failed to receive message!".formatted(summoner.getSummonerName()), ex);
                killEntity(summoner);
            }
        }

        // This is the main game loop, have fun
        boolean isGameRunning = true;
        int currentTick = 0;
        while (isGameRunning) {
            LOGGER.info("Current tick is %d".formatted(currentTick));

            // Process each summoner
            for (SummonerEntity summonerEntity : summoners) {
                // Check if the game is running as it may stop inside this cycle and we don't want to process other players in that case
                if (!isGameRunning)
                    break;

                var summonerClient = clients.get(summonerEntity);
                if (summonerClient == null || !summonerClient.isConnected()) {
                    if (summonerEntity.isAlive()) {
                        // The summoner is alive, but disconnected, kill it
                        LOGGER.info("Summoner %s disconnected!".formatted(summonerEntity.getSummonerName()));
                        killEntity(summonerEntity);
                    }
                } else if (summonerEntity.isAlive()) {
                    // The summoner is alive

                    if(gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].entity == null || !(gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].entity.equals(summonerEntity))){ // Push vulnerability
                        LOGGER.info("Entity %s is not where it should be! Expected tile [%d,%d] to contain entity %s but found %s instead!".formatted(summonerEntity.getSummonerName(), summonerEntity.currentX, summonerEntity.currentY, summonerEntity.entityId, gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].entity==null?"null":gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].entity.entityId));
                        killEntity(summonerEntity);
                    }

                    // If the current tile deals damage, apply it
                    if (gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].isDamaging) {
                        LOGGER.info("Summoner %s is in the fog!".formatted(summonerEntity.getSummonerName()));
                        damageEntity(summonerEntity, null, Config.MAP_GAS_DAMAGE);
                    }

                    // If the current tile is lava, kill it
                    if (gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].bgTile == BackgroundTile.LAVA) {
                        LOGGER.info("Summoner %s fell in the lava!".formatted(summonerEntity.getSummonerName()));
                        killEntity(summonerEntity);
                    }

                    // If not dead yet
                    if (!summonerEntity.isAlive())
                        continue;

                    // Handle entities for this summoner
                    for (VMEntity entity : summonerSummons.get(summonerEntity)) {
                        // If entity is dead ignore
                        if (!entity.isAlive())
                            continue;

                        if(gameMap.tiles[entity.currentX][entity.currentY].entity == null || !(gameMap.tiles[entity.currentX][entity.currentY].entity.equals(entity))){ // Push vulnerability
                            LOGGER.info("Entity %s (%s) is not where it should be! Expected tile [%d,%d] to contain entity %s but found %s instead!".formatted(entity.entityId, entity.owner.getSummonerName(), entity.currentX, entity.currentY, entity.entityId, gameMap.tiles[entity.currentX][entity.currentY].entity==null?"null":gameMap.tiles[entity.currentX][entity.currentY].entity.entityId));
                            killEntity(entity);
                        }

                        // If the current tile deals damage, apply it
                        if (gameMap.tiles[entity.currentX][entity.currentY].isDamaging) {
                            LOGGER.info("Entity %s (%s) is in the fog!".formatted(entity.entityId, entity.owner.getSummonerName()));
                            damageEntity(entity, null, Config.MAP_GAS_DAMAGE);
                        }
                        if(!entity.isAlive()){
                            continue;
                        }
                        if (!entity.canFlyOverLiquids() && gameMap.tiles[entity.currentX][entity.currentY].bgTile == BackgroundTile.LAVA) {
                            // If the current tile is lava and the entity is not flying, kill it
                            LOGGER.info("Entity %s (%s) fell in the lava!".formatted(entity.entityId, entity.owner.getSummonerName()));
                            killEntity(summonerEntity);
                        } else if (!entity.canFlyOverLiquids() && gameMap.tiles[entity.currentX][entity.currentY].bgTile == BackgroundTile.WATER) {
                            // If the current tile is water and the entity is not flying, lose a turn and move it
                            LOGGER.info("Entity %s (%s) fell in the water!".formatted(entity.entityId, entity.owner.getSummonerName()));
                            int[] snapTo = getNearestEmptyLand(entity);
                            if (snapTo != null && snapTo.length == 2) moveEntity(entity, snapTo);
                        } else if (entity instanceof MouseEntity && entity.getTickAlive() >= Config.MOUSE_MAX_TICK_ALIVE) {
                            // If the entity is a mouse that has lived too long
                            LOGGER.info("Mouse entity %s (%s) is too old!".formatted(entity.entityId, entity.owner.getSummonerName()));
                            killEntity(entity);
                        } else if (entity.isAlive()){
                            GameMap deliveryMap = new GameMap(this.foggedGameMap);
                            for (int x = entity.currentX - entity.getVisionRadius(); x <= entity.currentX + entity.getVisionRadius(); x++) {
                                for (int y = entity.currentY - entity.getVisionRadius(); y <= entity.currentY + entity.getVisionRadius(); y++) {
                                    if (x < 0 || x >= gameMap.width)
                                        continue;
                                    else if (y < 0 || y >= gameMap.height)
                                        continue;

                                    // Copy tiles from original map
                                    deliveryMap.tiles[x][y] = new Tile(gameMap.tiles[x][y]);

                                    // When inside a bush, the view radius is reduced
                                    if (deliveryMap.tiles[x][y].bgTile == BackgroundTile.BUSH && deliveryMap.tiles[x][y].entity!=null) {
                                        if(!(deliveryMap.tiles[x][y].entity.entityId.equals(entity.entityId) || deliveryMap.tiles[x][y].entity.entityId.equals(entity.owner.entityId) || (deliveryMap.tiles[x][y].entity.owner!=null && deliveryMap.tiles[x][y].entity.owner.entityId.equals(entity.owner.entityId)))){ //Can see himself, owner, and other minions owned by owner inside bushes
                                            if (Math.abs(x - summonerEntity.currentX) > Config.BUSH_VIEW_RADIUS || Math.abs(y - summonerEntity.currentY) > Config.BUSH_VIEW_RADIUS) {
                                                deliveryMap.tiles[x][y].entity = null;
                                            }
                                        }
                                    }
                                }
                            }

                            if (entity instanceof CatEntity) {
                                // Allow cats to see the closest entity or summoner
                                int[] additionalEntity = findClosestPlayerOrMouse(entity.currentX, entity.currentY, summonerEntity);
                                if (additionalEntity != null) {
                                    // Avoid masking an entity in the true vision radius
                                    if (Math.abs(additionalEntity[0] - entity.currentX) > entity.getVisionRadius() || Math.abs(additionalEntity[1] - entity.currentY) > entity.getVisionRadius()) {
                                        // Dummy entity, to avoid recognizing summoner/mouse
                                        deliveryMap.tiles[additionalEntity[0]][additionalEntity[1]].entity = new SummonerEntity("???", "", additionalEntity[0], additionalEntity[1]);
                                    }
                                }
                            }

                            // Set the tiles on the entity
                            entity.setMapTiles(deliveryMap.tiles);

                            int[] action = null;
                            try {
                                LOGGER.info("Executing VM of %s (%s)".formatted(entity.entityId, entity.owner.getSummonerName()));
                                action = entity.executeCode();
                            } catch (VMException ex) {
                                LOGGER.log(Level.SEVERE, "A VM exception was thrown when executing %s (%s)".formatted(entity.entityId, entity.owner.getSummonerName()), ex);
                            } catch (Exception ex) {
                                LOGGER.log(Level.SEVERE, "The VM crashed with an unknown exception while executing %s (%s)".formatted(entity.entityId, entity.owner.getSummonerName()), ex);
                            }

                            if (action == null) {
                                LOGGER.info("VM returned failed to return an action");
                                killEntity(entity);
                            } else if (action.length == 0) {
                                LOGGER.info("VM returned no action");
                                // Do nothing
                            } else if (action.length == 1) {
                                // Execute ability
                                Map<Entity, Integer> m = entity.executeAbility(action, gameMap, mapWithUpdates, scoreboard, summonerSummons);
                                if (m != null) { // TODO: It is always null except for wolf, which returns the map of entities to damage
                                    Set<Entity> entitySet = m.keySet();
                                    for (Entity e : entitySet) {
                                        damageEntity(e, entity, m.get(e));
                                    }
                                }
                            } else if (action.length == 2) {
                                // Move to tile
                                int entitySpeed = entity.getSpeed();
                                if (entity instanceof SpectreEntity) {
                                    for (int i = 0; i < entitySpeed; i++) {
                                        AbilityUpdate.Direction direction = ((SpectreEntity) entity).direction;
                                        int[] destination = getTileAtDirection(entity.currentX, entity.currentY, direction);
                                        int destinationX = destination[0], destinationY = destination[1];
                                        if (destinationX >= 0 && destinationX < gameMap.width && destinationY >= 0 && destinationY < gameMap.height) {
                                            if (gameMap.tiles[destinationX][destinationY].entity == null) {
                                                moveEntity(entity, destination);
                                            }
                                        } else {
                                            killEntity(entity);
                                            break;
                                        }
                                    }
                                } else {
                                    int toX = action[0], toY = action[1];
                                    if (toX < 0 || toX >= gameMap.width || toY < 0 || toY >= gameMap.height)
                                        continue;

                                    BackgroundTile[] blockingTiles;
                                    if (entity instanceof DragonEntity || entity instanceof ArrowEntity) {
                                        blockingTiles = new BackgroundTile[]{BackgroundTile.MOUNTAIN};
                                    } else if (entity instanceof MoleEntity) {
                                        blockingTiles = new BackgroundTile[]{BackgroundTile.WATER, BackgroundTile.LAVA};
                                    } else {
                                        blockingTiles = new BackgroundTile[]{BackgroundTile.MOUNTAIN, BackgroundTile.WATER, BackgroundTile.LAVA};
                                    }

                                    List<int[]> path = performAStar(entity.currentX, entity.currentY, toX, toY, blockingTiles);
                                    LOGGER.info("Entity %s path is %s".formatted(entity.entityId, path));

                                    for (int i = 0; i < entitySpeed; i++) {
                                        if (path.size() <= i)
                                            continue;

                                        // We fully trust that A* will return valid moves

                                        int[] nextCoords = path.get(i);
                                        var tile = gameMap.tiles[nextCoords[0]][nextCoords[1]];
                                        if (tile.entity == null) {
                                            if (entity.type == Entity.Type.Mole && tile.bgTile == BackgroundTile.MOUNTAIN) {
                                                // If a mole wants to hit a mountain, destroy it
                                                gameMap.tiles[nextCoords[0]][nextCoords[1]].bgTile = BackgroundTile.EMPTY;
                                                mapWithUpdates.addUpdate(new BackgroundTileChangeUpdate(nextCoords[0], nextCoords[1], BackgroundTile.Type.Empty));
                                            }

                                            // Move to empty tile
                                            moveEntity(entity, nextCoords);
                                        } else if (entity instanceof ArrowEntity) {
                                            if ((tile.entity instanceof SummonerEntity && !tile.entity.equals(entity.owner)) || (tile.entity instanceof VMEntity && !tile.entity.owner.equals(entity.owner))) {
                                                // Do not hit ourselves or one of our entities
                                                damageEntity(tile.entity, entity, entity.getDamage());
                                            }

                                            // When an arrow hits any entity it dies
                                            killEntity(entity);
                                            break;
                                        } else if (entity instanceof WolfEntity) {
                                            if (!tile.entity.isPunchable()) {
                                                // Wolves cannot hit flying entities
                                                continue;
                                            }

                                            // Deal damage
                                            if ((tile.entity instanceof SummonerEntity && !tile.entity.equals(entity.owner)) || (tile.entity instanceof VMEntity && !tile.entity.owner.equals(entity.owner))) { //If target entity is a summoner and not owner or target entity is a vmentity and not same owner
                                                // Do not hit ourselves or one of our entities
                                                damageEntity(tile.entity, entity, entity.getDamage());
                                            }
                                        } else  {
                                            // If we cannot hurt it, do nothing
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Summoner handling code
                    if (gameMap.tiles[summonerEntity.currentX][summonerEntity.currentY].bgTile == BackgroundTile.WATER) {
                        // If the current tile is water, lose a turn and move the summoner
                        LOGGER.info("Summoner %s fell in the water!".formatted(summonerEntity.getSummonerName()));
                        int[] snapTo = getNearestEmptyLand(summonerEntity);
                        if (snapTo != null && snapTo.length == 2) moveEntity(summonerEntity, snapTo);
                    } else {
                        // Construct map to send to player, start from empty and fill-in what it sees
                        GameMap deliveryMap = new GameMap(this.foggedGameMap);

                        // What the summoner sees
                        for (int x = summonerEntity.currentX - Config.SUMMONER_VISION_RADIUS; x <= summonerEntity.currentX + Config.SUMMONER_VISION_RADIUS; x++) {
                            for (int y = summonerEntity.currentY - Config.SUMMONER_VISION_RADIUS; y <= summonerEntity.currentY + Config.SUMMONER_VISION_RADIUS; y++) {
                                if (x < 0 || x >= gameMap.width)
                                    continue;
                                else if (y < 0 || y >= gameMap.height)
                                    continue;

                                // Copy tiles from original map
                                deliveryMap.tiles[x][y] = new Tile(gameMap.tiles[x][y]);

                                // When inside a bush, the view radius is reduced
                                if (deliveryMap.tiles[x][y].bgTile == BackgroundTile.BUSH && deliveryMap.tiles[x][y].entity!=null) {
                                    if(!(deliveryMap.tiles[x][y].entity.entityId.equals(summonerEntity.entityId) || (deliveryMap.tiles[x][y].entity.owner!=null && deliveryMap.tiles[x][y].entity.owner.entityId.equals(summonerEntity.entityId)))){ //Can see itself and his minions inside bushes
                                        if (Math.abs(x - summonerEntity.currentX) > Config.BUSH_VIEW_RADIUS || Math.abs(y - summonerEntity.currentY) > Config.BUSH_VIEW_RADIUS) {
                                            deliveryMap.tiles[x][y].entity = null;
                                        }
                                    }

                                }
                            }
                        }

                        // Whet the entities see
                        for (VMEntity entity : summonerSummons.get(summonerEntity)) {
                            if (!entity.isAlive() || entity.givenVisionRadius() == 0) {
                                continue;
                            }

                            for (int x = entity.currentX - entity.givenVisionRadius(); x <= entity.currentX + entity.givenVisionRadius(); x++) {
                                for (int y = entity.currentY - entity.givenVisionRadius(); y <= entity.currentY + entity.givenVisionRadius(); y++) {
                                    if (x < 0 || x >= gameMap.width)
                                        continue;
                                    else if (y < 0 || y >= gameMap.height)
                                        continue;

                                    // Copy tiles from original map
                                    deliveryMap.tiles[x][y] = new Tile(gameMap.tiles[x][y]);

                                    // When inside a bush, the view radius is reduced
                                    if (deliveryMap.tiles[x][y].bgTile == BackgroundTile.BUSH && deliveryMap.tiles[x][y].entity!=null) {
                                        if(!(deliveryMap.tiles[x][y].entity.entityId.equals(entity.entityId) || deliveryMap.tiles[x][y].entity.entityId.equals(entity.owner.entityId)  || (deliveryMap.tiles[x][y].entity.owner!=null && deliveryMap.tiles[x][y].entity.owner.entityId.equals(entity.owner.entityId)))){ //Can see himself, owner, and other minions owned by owner inside bushes
                                            if (Math.abs(x - summonerEntity.currentX) > Config.BUSH_VIEW_RADIUS || Math.abs(y - summonerEntity.currentY) > Config.BUSH_VIEW_RADIUS) {
                                                deliveryMap.tiles[x][y].entity = null;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        try {
                            // Send map to player
                            summonerClient.sendMessage(deliveryMap.toJson().toString());
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, "Failed sending map to %s, killing it!".formatted(summonerEntity.getSummonerName()), ex);
                            killEntity(summonerEntity);
                        }

                        // If not dead yet
                        if (!summonerEntity.isAlive())
                            continue;

                        // Get a move from the queue
                        Instant start = Instant.now();
                        String move = summonerClient.nextMessage();
                        Instant end = Instant.now();
                        LOGGER.info("nextMessage returned after %d ms".formatted(Duration.between(start, end).toMillis()));
                        if (move != null) {
                            LOGGER.info("Got move from summoner %s: %s".formatted(summonerEntity.getSummonerName(), move));

                            try {
                                // Handle the move
                                handleMove(summonerEntity, new JSONObject(move));
                            } catch (Exception ex) {
                                // TODO: invalid move, whatever
                                LOGGER.log(Level.SEVERE, "Something bad happened while processing move", ex);
                            }
                        }else{
                            LOGGER.info("Did not get move from %s".formatted(summonerEntity.getSummonerName()));
                        }

                        // Regenerate mana
                        if (currentTick % Config.MANA_REGEN_TICK_DELTA == 0 && summonerEntity.getMana() + Config.MANA_REGEN_AMOUNT <= Config.MAX_SUMMONER_MANA) {
                            summonerEntity.setMana(summonerEntity.getMana() + Config.MANA_REGEN_AMOUNT);
                            mapWithUpdates.addUpdate(new ManaRecoveryUpdate(summonerEntity.entityId, Config.MANA_REGEN_AMOUNT, summonerEntity.getMana()));
                        }
                    }
                }

                // Check if game is completed
                var aliveCount = summoners.stream().filter(Entity::isAlive).count();
                LOGGER.fine("%d summoners remaining!".formatted(aliveCount));

                if (aliveCount == 1) {
                    var lastSurvivor = summoners.stream().filter(Entity::isAlive).findFirst();
                    if (lastSurvivor.isEmpty())
                        throw new IllegalStateException("Invalid game state");

                    LOGGER.info("Game ended, summoner %s survived!".formatted(lastSurvivor.get().getSummonerName()));
                    isGameRunning = false;

                    scoreboard.add(lastSurvivor.get());
                    lastSurvivor.get().addScore(Config.FINAL_SCOREBOARD_SCORES[this.summoners.size()-this.scoreboard.size()]);
                    mapWithUpdates.addUpdate(new ScoreboardUpdate(lastSurvivor.get().entityId, lastSurvivor.get().getScore()));
                } else if (aliveCount == 0) {
                    LOGGER.info("Game ended, no more summoners alive!");
                    isGameRunning = false;
                }
            }

            // Fog expand code
            if (Config.SHOULD_SHRINK_MAP && currentTick >= Config.MAP_SHRINK_START_TICK && (currentTick - Config.MAP_SHRINK_START_TICK) % Config.MAP_SHRINK_DELTA_TICK == 0) {
                // TODO: this works well only with square maps, if we want to to use a non-square map it needs to be updated
                int mapShrinkRadius = ((currentTick - Config.MAP_SHRINK_START_TICK) / Config.MAP_SHRINK_DELTA_TICK) + 1;
                if (mapShrinkRadius < Math.ceil((float) Math.max(Config.MAP_WIDTH, Config.MAP_HEIGHT) / 2)) {
                    LOGGER.info("Fog is closing, thickness: %d".formatted(mapShrinkRadius));

                    for (int x = 0; x < mapShrinkRadius; x++)
                        for (int y = 0; y < gameMap.height; y++)
                            gameMap.tiles[x][y].isDamaging = true;

                    for (int x = gameMap.width - mapShrinkRadius; x < gameMap.width; x++)
                        for (int y = 0; y < gameMap.height; y++)
                            gameMap.tiles[x][y].isDamaging = true;

                    for (int y = 0; y < mapShrinkRadius; y++)
                        for (int x = 0; x < gameMap.width; x++)
                            gameMap.tiles[x][y].isDamaging = true;

                    for (int y = gameMap.height - mapShrinkRadius; y < gameMap.height; y++)
                        for (int x = 0; x < gameMap.width; x++)
                            gameMap.tiles[x][y].isDamaging = true;

                    mapWithUpdates.addUpdate(new ZoneReduceUpdate(mapShrinkRadius, mapShrinkRadius, mapShrinkRadius, mapShrinkRadius));
                }
            }

            currentTick += 1;
            mapWithUpdates.addUpdate(new TickUpdate(currentTick));
        }
    }

    private int[] getNearestEmptyLand(Entity entity) {
        Queue<int[]> queue = new ArrayBlockingQueue<>(gameMap.width * gameMap.height);
        HashMap<int[], Integer> visited = new HashMap<>();
        visited.put(new int[]{entity.currentX, entity.currentY}, 1);
        queue.add(new int[]{entity.currentX, entity.currentY});
        while (!queue.isEmpty()) {
            int[] item = queue.poll();
            if (item != null && item.length == 2) {
                Tile t = gameMap.tiles[item[0]][item[1]];
                if (t.bgTile == BackgroundTile.EMPTY || t.bgTile == BackgroundTile.BUSH) {
                    if (t.entity == null) {
                        return item;
                    }
                }
                List<int[]> neighbours = getNeighbours(item[0], item[1]);
                for (int[] neighbour : neighbours) {
                    if (!visited.containsKey(neighbour)) {
                        visited.put(neighbour, 1);
                        queue.add(neighbour);
                    }
                }
            }
        }
        return null;
    }

    public List<int[]> performAStar(int startX, int startY, int endX, int endY, BackgroundTile[] blockingTiles) {
        GameMap copyMap = new GameMap(gameMap);
        if(Arrays.stream(blockingTiles).anyMatch(t -> copyMap.tiles[startX][startY].bgTile == t)){
            return new ArrayList<>();
        }
        Queue<Tile> openSet = new PriorityQueue<>();
        Map<Tile, Tile> cameFrom = new HashMap<>();
        Map<Tile, Integer> gScore = new HashMap<>();
        gScore.put(copyMap.tiles[startX][startY], 0);
        copyMap.tiles[startX][startY].fscore = distanceScore(startX, startY, endX, endY);
        openSet.add(copyMap.tiles[startX][startY]);
        while (!openSet.isEmpty()) {
            Tile current = openSet.poll();
            if (current.x == endX && current.y == endY) {
                return reconstructPath(cameFrom, current);
            }
            List<int[]> neighbours = getNeighboursNoDiagonals(current.x, current.y);
            for (int[] neighbour : neighbours) {
                if (Arrays.stream(blockingTiles).noneMatch(t -> copyMap.tiles[neighbour[0]][neighbour[1]].bgTile == t)) {
                    int tentativeGScore = gScore.getOrDefault(current, 999999999) + 1;
                    if (tentativeGScore < gScore.getOrDefault(copyMap.tiles[neighbour[0]][neighbour[1]], 999999999)) {
                        copyMap.tiles[neighbour[0]][neighbour[1]].fscore = tentativeGScore + distanceScore(neighbour[0], neighbour[1], endX, endY);
                        cameFrom.put(copyMap.tiles[neighbour[0]][neighbour[1]], current);
                        gScore.put(copyMap.tiles[neighbour[0]][neighbour[1]], tentativeGScore);
                        if (!openSet.contains(copyMap.tiles[neighbour[0]][neighbour[1]])) {
                            openSet.add(copyMap.tiles[neighbour[0]][neighbour[1]]);
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private int distanceScore(int x, int y, int otherX, int otherY) {
        return Math.abs(x - otherX) + Math.abs(y - otherY);
    }

    private List<int[]> reconstructPath(Map<Tile, Tile> cameFrom, Tile current) {
        List<int[]> totalPath = new ArrayList<>();
        totalPath.add(new int[]{current.x, current.y});
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(new int[]{current.x, current.y});
        }
        Collections.reverse(totalPath);
        return totalPath.subList(1, totalPath.size());
    }

    private int[] findClosestPlayerOrMouse(Integer x, Integer y, SummonerEntity ignoredSummoner) {
        List<SummonerEntity> copy = new ArrayList<>(summoners);
        copy.sort((SummonerEntity s1, SummonerEntity s2) -> (int) Math.floor((Math.pow(s1.currentX - x, 2) + Math.pow(s1.currentY - y, 2)) - (Math.pow(s2.currentX - x, 2) + Math.pow(s2.currentY - y, 2))));
        SummonerEntity closerSummoner = null;
        for (SummonerEntity summoner : copy) {
            if (!summoner.equals(ignoredSummoner)) {
                closerSummoner = summoner;
                break;
            }
        }
        if (closerSummoner == null) {
            return null;
        }
        for (VMEntity entity : summonerSummons.get(closerSummoner)) {
            if (entity.isAlive() && entity instanceof MouseEntity) {
                return new int[]{entity.currentX, entity.currentY};
            }
        }
        return new int[]{closerSummoner.currentX, closerSummoner.currentY};
    }

    private void handleMove(SummonerEntity summoner, JSONObject move) {
        if (!move.has("type"))
            return;

        String type = move.getString("type");
        if (type.equals("nothing"))
            return;

        switch (type) {
            case "move" -> {
                int toX = -1, toY = -1;
                if (move.has("to")){
                    JSONArray to = move.getJSONArray("to");
                    if (to.length() != 2)
                        return;
                    toX = to.getInt(0);
                    toY = to.getInt(1);
                }else if(move.has("direction")){
                    int directionOrdinal = move.getInt("direction");
                    if (directionOrdinal < 0 || directionOrdinal >= AbilityUpdate.Direction.values().length)
                        return;
                    AbilityUpdate.Direction dir = AbilityUpdate.Direction.values()[directionOrdinal];
                    int[] moveData = getTileAtDirection(summoner.currentX, summoner.currentY, dir);
                    toX = moveData[0];
                    toY = moveData[1];
                }else{
                    return;
                }


                if (toX >= 0 && toX < gameMap.width && toY >= 0 && toY < gameMap.height) {
                    if (isNearbyTile(summoner.currentX, summoner.currentY, toX, toY)) {
                        Tile destinationTile = gameMap.tiles[toX][toY];
                        if (destinationTile.entity == null && (destinationTile.bgTile == BackgroundTile.EMPTY || destinationTile.bgTile == BackgroundTile.BUSH)) {
                            moveEntity(summoner, new int[]{toX, toY});
                        }
                    }
                }
            }
            case "summon" -> {
                int toX = -1, toY = -1;

                if (move.has("to")){
                    JSONArray to = move.getJSONArray("to");
                    if (to.length() != 2)
                        return;
                    toX = to.getInt(0);
                    toY = to.getInt(1);
                }else if(move.has("direction")){
                    int directionOrdinal = move.getInt("direction");
                    if (directionOrdinal < 0 || directionOrdinal >= AbilityUpdate.Direction.values().length)
                        return;
                    AbilityUpdate.Direction d = AbilityUpdate.Direction.values()[directionOrdinal];
                    int[] moveData = getTileAtDirection(summoner.currentX, summoner.currentY, d);
                    toX = moveData[0];
                    toY = moveData[1];
                }else{
                    return;
                }



                if (!isNearbyTile(summoner.currentX, summoner.currentY, toX, toY)) {
                    return;
                }

                AbilityUpdate.Direction dir;
                if(summoner.currentX-toX>0){
                    dir = AbilityUpdate.Direction.Left;
                }else if(summoner.currentX-toX<0){
                    dir = AbilityUpdate.Direction.Right;
                }else if(summoner.currentY-toY>0){
                    dir = AbilityUpdate.Direction.Up;
                }else{
                    dir = AbilityUpdate.Direction.Down;
                }

                if (!move.has("entityType") || !move.has("code"))
                    return;

                int entityTypeOrdinal = move.getInt("entityType");
                JSONArray code = move.getJSONArray("code");

                // Check that we know the type, the direction to spawn it in, and the code
                if (entityTypeOrdinal < 0 || entityTypeOrdinal >= VMEntity.Type.values().length)
                    return;

                Integer summonX = Math.floorMod(toX, gameMap.width), summonY = Math.floorMod(toY, gameMap.height);
                LOGGER.info("Attempting summon at [%d, %d]".formatted(summonX, summonY));
                if (summonX < 0 || summonX >= gameMap.width || summonY < 0 || summonY >= gameMap.height)
                    return;

                Tile spawnTile = gameMap.tiles[summonX][summonY];
                if (spawnTile.entity != null || spawnTile.bgTile == BackgroundTile.WATER || spawnTile.bgTile == BackgroundTile.LAVA || spawnTile.bgTile == BackgroundTile.MOUNTAIN)
                    return;

                // Entity instantiation checks the code, so if it is invalid it gets caught at the end
                VMEntity.Type entityType = VMEntity.Type.values()[entityTypeOrdinal];
                VMEntity entity = switch (entityType) {
                    case Cat -> new CatEntity(summoner, summonX, summonY, code);
                    case Mole -> new MoleEntity(summoner, summonX, summonY, code);
                    case Wolf -> new WolfEntity(summoner, summonX, summonY, code);
                    case Mouse -> new MouseEntity(summoner, summonX, summonY, code);
                    case Dragon -> new DragonEntity(summoner, summonX, summonY, code);
                    case Spectre -> new SpectreEntity(summoner, summonX, summonY, code, dir);
                    case Armadillo -> new ArmadilloEntity(summoner, summonX, summonY, code);
                    case MagicArrow -> new ArrowEntity(summoner, summonX, summonY, code);
                    default -> null;
                };

                if (entity != null && entity.getManaCost() <= summoner.getMana()) {
                    summoner.setMana(summoner.getMana() - entity.getManaCost());

                    gameMap.tiles[summonX][summonY].entity = entity;
                    mapWithUpdates.addUpdate(new ManaUsageUpdate(summoner.entityId, entity.getManaCost(), summoner.getMana()));
                    mapWithUpdates.addUpdate(new VMEntitySummonedUpdate(summoner.entityId, entity.entityId, entity.life, summonX, summonY, entityType));
                    summonerSummons.get(summoner).add(entity);
                }
            }
            case "push" -> {

                int toX = -1, toY = -1;

                if (move.has("to")){
                    JSONArray to = move.getJSONArray("to");
                    if (to.length() != 2)
                        return;
                    toX = to.getInt(0);
                    toY = to.getInt(1);
                }else if(move.has("direction")){
                    int directionOrdinal = move.getInt("direction");
                    if (directionOrdinal < 0 || directionOrdinal >= AbilityUpdate.Direction.values().length)
                        return;
                    AbilityUpdate.Direction d = AbilityUpdate.Direction.values()[directionOrdinal];
                    int[] moveData = getTileAtDirection(summoner.currentX, summoner.currentY, d);
                    toX = moveData[0];
                    toY = moveData[1];
                }

                if (!isNearbyTile(summoner.currentX, summoner.currentY, toX, toY)) {
                    return;
                }


                AbilityUpdate.Direction dir;

                if(summoner.currentX-toX>0){
                    dir = AbilityUpdate.Direction.Left;
                }else if(summoner.currentX-toX<0){
                    dir = AbilityUpdate.Direction.Right;
                }else if(summoner.currentY-toY>0){
                    dir = AbilityUpdate.Direction.Up;
                }else{
                    dir = AbilityUpdate.Direction.Down;
                }


                int[] targetData = getTileAtDirection(summoner.currentX, summoner.currentY, dir);
                int[] targetDestData = getTileAtDirection(targetData[0], targetData[1], dir);
                int targetX = targetData[0], targetY = targetData[1];
                int targetXDest = targetDestData[0], targetYDest = targetDestData[1];
                if (targetXDest >= 0 && targetXDest < gameMap.width && targetYDest >= 0 && targetYDest < gameMap.height) {
                    Tile targetTile = gameMap.tiles[targetX][targetY];

                    Entity targetEntity = targetTile.entity;
                    if (targetEntity == null || !targetEntity.isMovable())
                        return;

                    Tile destinationTile = gameMap.tiles[targetXDest][targetYDest];
                    if (destinationTile.bgTile != BackgroundTile.MOUNTAIN) { // destinationTile.entity == null removed to allow push vulnerability
                        mapWithUpdates.addUpdate(new AbilityUpdate(summoner.entityId, 999, "push", dir));
                        moveEntity(targetEntity, new int[]{targetXDest, targetYDest});

                        // Consider pushing as hit even if we don't deal damage
                        if (targetEntity instanceof SummonerEntity) {
                            ((SummonerEntity) targetEntity).setLastHitBy(summoner);
                        }
                    }
                }
            }
            case "punch" -> {
                int toX = -1, toY = -1;

                if (move.has("to")){
                    JSONArray to = move.getJSONArray("to");
                    if (to.length() != 2)
                        return;
                    toX = to.getInt(0);
                    toY = to.getInt(1);
                }else if(move.has("direction")){
                    int directionOrdinal = move.getInt("direction");
                    if (directionOrdinal < 0 || directionOrdinal >= AbilityUpdate.Direction.values().length)
                        return;
                    AbilityUpdate.Direction d = AbilityUpdate.Direction.values()[directionOrdinal];
                    int[] moveData = getTileAtDirection(summoner.currentX, summoner.currentY, d);
                    toX = moveData[0];
                    toY = moveData[1];
                }




                AbilityUpdate.Direction dir;

                if(summoner.currentX-toX>0){
                    dir = AbilityUpdate.Direction.Left;
                }else if(summoner.currentX-toX<0){
                    dir = AbilityUpdate.Direction.Right;
                }else if(summoner.currentY-toY>0){
                    dir = AbilityUpdate.Direction.Up;
                }else{
                    dir = AbilityUpdate.Direction.Down;
                }

                int targetX = toX, targetY = toY;
                if (targetX >= 0 && targetX < gameMap.width && targetY >= 0 && targetY < gameMap.height) {
                    Tile targetTile = gameMap.tiles[targetX][targetY];

                    Entity targetEntity = targetTile.entity;
                    if (targetEntity == null || !targetEntity.isPunchable())
                        return;

                    if(targetEntity instanceof SummonerEntity){
                        ((SummonerEntity)targetEntity).setLastHitBy(summoner);
                    }

                    if(isNearbyTile(toX, toY, summoner.currentX, summoner.currentY)){
                        mapWithUpdates.addUpdate(new AbilityUpdate(summoner.entityId, 999, "punch", dir));
                        damageEntity(targetEntity, summoner, Config.PUNCH_DAMAGE);
                    }
                }
            }
        }
    }

    private void killEntity(Entity entity) {
        entity.life = 0;
        gameMap.tiles[entity.currentX][entity.currentY].entity = null;


        if (entity instanceof SummonerEntity targetSummoner) {
            LOGGER.info("Summoner %s died!".formatted(targetSummoner.getSummonerName()));
            mapWithUpdates.addUpdate(new SummonerEntityDeadUpdate(targetSummoner.entityId));

            this.scoreboard.add(targetSummoner);

            targetSummoner.addScore(Config.FINAL_SCOREBOARD_SCORES[this.summoners.size()-this.scoreboard.size()]);
            mapWithUpdates.addUpdate(new ScoreboardUpdate(targetSummoner.entityId, targetSummoner.getScore()));

            if (targetSummoner.getLastHitBy() != null && !targetSummoner.getLastHitBy().equals(targetSummoner) && targetSummoner.getLastHitBy().isAlive()) {
                // Heal entity that killed us
                SummonerEntity entityToHeal = targetSummoner.getLastHitBy();

                int healTo = entityToHeal.life + Config.HEAL_AMOUNT_ON_KILL;
                if (healTo > Config.MAX_SUMMONER_LIFE) healTo = Config.MAX_SUMMONER_LIFE;

                entityToHeal.life = healTo;
                mapWithUpdates.addUpdate(new LifeRecoveryUpdate(entityToHeal.entityId, Config.HEAL_AMOUNT_ON_KILL, entityToHeal.life));
                entityToHeal.addScore(Config.POINTS_ON_KILL);
            }

            // Kill all his entities
            for (VMEntity vm : this.summonerSummons.get(entity)) {
                vm.life = 0;
                gameMap.tiles[vm.currentX][vm.currentY].entity = null;
                mapWithUpdates.addUpdate(new VMEntityDeathUpdate(vm.entityId));
                LOGGER.info("Removed entity %s of %s".formatted(vm.entityId, targetSummoner.getSummonerName()));
            }
        } else if (entity instanceof VMEntity targetEntity) {
            mapWithUpdates.addUpdate(new VMEntityDeathUpdate(targetEntity.entityId));
            LOGGER.info("Entity %s (%s) died!".formatted(entity.entityId, entity.owner.getSummonerName()));
        }
    }

    private void moveEntity(Entity e, int[] destinationTuple) {
        if (destinationTuple == null || destinationTuple.length != 2)
            return;
        LOGGER.log(Level.INFO, "Got move action, entity %s, to [%d, %d]".formatted(e.entityId, destinationTuple[0], destinationTuple[1]));
        int destX = destinationTuple[0], destY = destinationTuple[1];
        gameMap.tiles[e.currentX][e.currentY].entity = null;

        e.currentX = destX;
        e.currentY = destY;
        gameMap.tiles[destX][destY].entity = e;

        mapWithUpdates.addUpdate(new MoveUpdate(e.entityId, destX, destY));
    }

    private void damageEntity(Entity e, Entity damagingEntity, Integer amount) {
        if (e == null || !e.isAlive())
            return;

        // Remove life
        e.life -= amount;
        mapWithUpdates.addUpdate(new DamageUpdate(e.entityId, amount, e.life));
        if (damagingEntity != null && e instanceof SummonerEntity) {
            // If damage comes from summoner or vm and is inflicted to summoner

            SummonerEntity damagingEntityOwner = null;
            if (damagingEntity instanceof VMEntity) {
                // If from VM, get owner
                damagingEntityOwner = damagingEntity.owner;
            } else if (damagingEntity instanceof SummonerEntity) {
                // Else itself
                damagingEntityOwner = (SummonerEntity) damagingEntity;
            }

            if (damagingEntityOwner != null) {
                // If not null, update last hit by
                ((SummonerEntity) e).setLastHitBy(damagingEntityOwner);
            }
        }

        // If not remaining life, kill it
        if (e.life <= 0) {
            killEntity(e);
        }
    }

    private List<int[]> getNeighbours(int x, int y) {
        int[][] possibleNeighbours = new int[][]{{x - 1, y - 1}, {x - 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}, {x + 1, y}, {x + 1, y - 1}, {x, y - 1}};
        List<int[]> trueNeighbours = new ArrayList<>();
        for (int[] pb : possibleNeighbours) {
            if (pb[0] >= 0 && pb[0] < this.gameMap.width && pb[1] >= 0 && pb[1] < this.gameMap.height) {
                trueNeighbours.add(pb);
            }
        }
        return trueNeighbours;
    }

    private List<int[]> getNeighboursNoDiagonals(int x, int y) {
        int[][] possibleNeighbours = new int[][]{{x - 1, y}, {x, y + 1}, {x + 1, y}, {x, y - 1}};
        List<int[]> trueNeighbours = new ArrayList<>();
        for (int[] pb : possibleNeighbours) {
            if (pb[0] >= 0 && pb[0] < this.gameMap.width && pb[1] >= 0 && pb[1] < this.gameMap.height) {
                trueNeighbours.add(pb);
            }
        }
        return trueNeighbours;
    }

    private Boolean isNearbyTile(int x, int y, int otherX, int otherY) {
        return (x == otherX && Math.abs(y - otherY) == 1) || (y == otherY && Math.abs(x - otherX) == 1);
    }

    public void exportResults() throws IOException {
        Files.createDirectories(Paths.get(Config.OUTPUT_INFO_DIRECTORY));
        for (SummonerEntity summoner : summoners) {
            Files.createDirectories(Paths.get(Config.OUTPUT_INFO_DIRECTORY + summoner.getSummonerName()));
            for (VMEntity entity : summonerSummons.get(summoner)) {
                List<String> logs = entity.logs;
                FileWriter fw = new FileWriter(Config.OUTPUT_INFO_DIRECTORY + summoner.getSummonerName() + "/" + entity.entityId + ".log");
                fw.write(entity.type.name() + "---" + entity.entityId);
                for (String log : logs) {
                    fw.write(log + "\n");
                }
                fw.close();
            }
        }
        try (FileOutputStream fos = new FileOutputStream(Config.OUTPUT_INFO_DIRECTORY + "map.json")) {
            this.mapWithUpdates.serialize(fos);
        }
        Collections.reverse(scoreboard); // Reverse since we are adding in order of death
        try (FileOutputStream fos = new FileOutputStream(Config.OUTPUT_INFO_DIRECTORY + "scoreboard.json")) {
            JSONArray scoreboardArray = new JSONArray();
            for (SummonerEntity summoner : scoreboard) {
                JSONObject scoreboardObject = new JSONObject();
                scoreboardObject.put("name",summoner.getSummonerName());
                scoreboardObject.put("score",summoner.getScore());
                scoreboardArray.put(scoreboardObject);
            }
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes(scoreboardArray.toString());

        }
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}
