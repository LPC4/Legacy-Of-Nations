package org.lpc.map;

import com.badlogic.gdx.Game;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.MainGame;
import org.lpc.map.maps.SurfaceMap;

import static org.lpc.utility.Constants.SURFACE_MAP_HEIGHT;
import static org.lpc.utility.Constants.SURFACE_MAP_WIDTH;

@Getter
public class MapSystem {
    private static final Logger LOGGER = LogManager.getLogger(MapSystem.class);
    private final SurfaceMap surfaceMap;

    /// --- to be implemented ---
    /// private RegionalMap regionalMap;     // Groups of tiles
    /// private ContinentalMap continentMap; // Major regions
    /// private PlanetaryMap planetMap;      // Whole world
    /// private GalacticMap galaxyMap;       // Whole universe
    /// --- to be implemented ---

    private MapScale currentScale;

    public MapSystem(MainGame game) {
        LOGGER.info("SurfaceMap created with dimensions: {}x{}", SURFACE_MAP_WIDTH, SURFACE_MAP_HEIGHT);
        this.currentScale = MapScale.SURFACE;
        this.surfaceMap = new SurfaceMap(SURFACE_MAP_WIDTH, SURFACE_MAP_HEIGHT, game);
    }

    public void update() {
        surfaceMap.update();
    }

    public BaseMap getMap() {
        // Expand this to return the correct map based on the current scale
        switch (currentScale) {
            case SURFACE:
                return surfaceMap;
            default:
                return surfaceMap;
        }
    }
}
