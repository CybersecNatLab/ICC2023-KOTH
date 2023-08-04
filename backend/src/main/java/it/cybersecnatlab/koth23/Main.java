package it.cybersecnatlab.koth23;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException, IOException {
        Thread.sleep(5000); // FIXME: Wait for docker networks to be connected

        // Load config file
        if(args.length>=1) {
            Config.loadConfigFromFile(args[0]);
        }

        // Generate random map
        MapGenerator gen = new MapGenerator(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        while (!gen.generateMap()) {
            LOGGER.info("Generating map...");
        }

        LOGGER.info("Map generated with %d summoners".formatted(gen.getSummoners().size()));

        // Initialize game manager
        GameManager gm = new GameManager(gen.getMap(), gen.getSummoners());

        // Connect to players via RPC
        gm.connectToPlayers();

        // Run the actual game
        LOGGER.info("Game started");
        gm.startGame();
        LOGGER.info("Game ended");

        // Export game results
        gm.exportResults();
    }
}
