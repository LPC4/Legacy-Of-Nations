package org.lpc;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.BaseMap;
import org.lpc.map.MapSystem;
import org.lpc.map.maps.SurfaceMap;

import static org.lpc.utility.Constants.SURFACE_MAP_HEIGHT;
import static org.lpc.utility.Constants.SURFACE_MAP_WIDTH;

@Getter
public class GameStateManager {
    private static final Logger LOGGER = LogManager.getLogger(GameStateManager.class);

    private final MapSystem mapSystem;

    public GameStateManager(MainGame game) {
        LOGGER.info("Initializing GameStateManager");
        this.mapSystem = new MapSystem(game);
        LOGGER.info("SurfaceMap created with dimensions: {}x{}", SURFACE_MAP_WIDTH, SURFACE_MAP_HEIGHT);
    }

    public void update() {
        LOGGER.debug("Updating GameStateManager");
        mapSystem.update();
    }
}
