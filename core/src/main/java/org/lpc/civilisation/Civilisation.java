package org.lpc.civilisation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import jdk.tools.jmod.Main;
import lombok.Getter;
import org.lpc.GameStateManager;
import org.lpc.MainGame;
import org.lpc.map.maps.SurfaceMap;

@Getter
public class Civilisation {
    private final GameStateManager gameStateManager;
    private final String name;
    private final int population;

    // purple
    private final Color color = new Color(0.5f, 0.0f, 0.5f, 0.5f);

    private final ResourceHandler resourceHandler;
    private final TerritoryHandler territoryHandler;

    public Civilisation(GameStateManager gameStateManager, String name) {
        this(gameStateManager, name, 100);
    }

    public Civilisation(GameStateManager game, String name, int population) {
        this.gameStateManager = game;
        this.name = name;
        this.population = population;
        this.resourceHandler = new ResourceHandler(this);
        this.territoryHandler = new TerritoryHandler(this);

        initCivilisation();
    }

    private void initCivilisation() {
        // Claim starting territory based on starting population
        SurfaceMap map = gameStateManager.getMapSystem().getSurfaceMap();

        territoryHandler.claimStartingTerritory(map.getWidth() / 2,  map.getHeight() / 2, population / 50);
    }

    public void update() {
        // Update resource handler and territory handler
    }
}
