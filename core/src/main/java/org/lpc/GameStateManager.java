package org.lpc;

import lombok.Getter;
import org.lpc.map.BaseMap;
import org.lpc.map.maps.SurfaceMap;

import static org.lpc.utility.Constants.SURFACE_MAP_HEIGHT;
import static org.lpc.utility.Constants.SURFACE_MAP_WIDTH;

@Getter
public class GameStateManager {
    private final BaseMap map;

    public GameStateManager(MainGame game) {
        this.map = new SurfaceMap(SURFACE_MAP_WIDTH, SURFACE_MAP_HEIGHT, game);
    }

    public void update() {
        map.update();
    }
}
