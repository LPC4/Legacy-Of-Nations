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
    private final MainGame game;
    private final GameStateManager gameStateManager;
    private final String name;
    private final Color color = new Color(0.5f, 0.0f, 0.5f, 0.5f); // purple
    private final ResourceHandler resourceHandler;
    private final TerritoryHandler territoryHandler;
    private final PopulationHandler populationHandler;

    public Civilisation(MainGame game, GameStateManager gameStateManager, String name, int population) {
        this.game = game;
        this.gameStateManager = gameStateManager;
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

        // Add starting resources
        resourceHandler.addResource(ResourceType.FOOD, 100);
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Civilisation: ").append(name).append("\n");
        builder.append("Population: ").append(getPopulation()).append("\n");
        builder.append(resourceHandler.toString());
        return builder.toString();
    }
}
