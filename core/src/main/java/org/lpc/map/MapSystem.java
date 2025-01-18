package org.lpc.map;

import com.badlogic.gdx.Game;
import lombok.Getter;
import org.lpc.MainGame;
import org.lpc.map.maps.SurfaceMap;

import static org.lpc.utility.Constants.SURFACE_MAP_HEIGHT;
import static org.lpc.utility.Constants.SURFACE_MAP_WIDTH;

@Getter
public class MapSystem {
    private final SurfaceMap surfaceMap;       // Most detailed

    //private RegionalMap regionalMap;     // Groups of tiles
    //private ContinentalMap continentMap; // Major regions
    //private PlanetaryMap planetMap;      // Whole world

    private MapScale currentScale;

    public MapSystem(MainGame game) {
        this.currentScale = MapScale.SURFACE;
        this.surfaceMap = new SurfaceMap(SURFACE_MAP_WIDTH, SURFACE_MAP_HEIGHT, game);
    }

    public void update() {
        surfaceMap.update();
    }

    public BaseMap getMap() {
        switch (currentScale) {
            case SURFACE:
                return surfaceMap;
            default:
                return surfaceMap;
        }
    }
}
