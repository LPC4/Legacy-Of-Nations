package org.lpc.civilisation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jdk.tools.jmod.Main;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.GameStateManager;
import org.lpc.MainGame;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.resources.ResourceType;

@Getter
public class Civilisation {
    private final GameStateManager gameStateManager;
    private final String name;
    private final Color color = new Color(0.5f, 0.0f, 0.5f, 0.5f); // purple
    private final ResourceHandler resourceHandler;
    private final TerritoryHandler territoryHandler;
    private final PopulationHandler populationHandler;

    public Civilisation(GameStateManager gameStateManager, String name) {
        this(gameStateManager, name, 100);
    }

    public Civilisation(GameStateManager game, String name, int population) {
        this.gameStateManager = game;
        this.name = name;
        this.resourceHandler = new ResourceHandler(this);
        this.territoryHandler = new TerritoryHandler(this);
        this.populationHandler = new PopulationHandler(this, population);

        initCivilisation();
    }

    private void initCivilisation() {
        // Claim starting territory based on starting population
        SurfaceMap map = gameStateManager.getMapSystem().getSurfaceMap();

        territoryHandler.claimStartingTerritory(map.getWidth() / 2,  map.getHeight() / 2, populationHandler.getStartingRadius());
        territoryHandler.setStartingBuildings();
    }

    public void update() {
        // Update resource handler and territory handler
        territoryHandler.update();
        resourceHandler.update();

        // Update population
        populationHandler.update();
    }

    public void consumeResource(ResourceType type, int amount) {
        resourceHandler.removeResource(type, amount);
    }

    public int getResourceAmount(ResourceType type) {
        return resourceHandler.getResourceAmount(type);
    }

    public int getPopulation() {
        return populationHandler.getPopulation();
    }
}
