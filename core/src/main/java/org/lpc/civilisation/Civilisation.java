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
    private final Color color;
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
        this.color = new Color(0.5f, 0.0f, 0.5f, 0.5f); // purple

        initCivilisation();
    }

    private void initCivilisation() {
        claimStartingTerritory();
        addStartingResources();
    }

    private void claimStartingTerritory() {
        SurfaceMap map = gameStateManager.getMapSystem().getSurfaceMap();
        territoryHandler.claimStartingTerritory(map.getWidth() / 2,  map.getHeight() / 2, populationHandler.getStartingRadius());
        territoryHandler.setStartingBuildings();
    }

    private void addStartingResources() {
        resourceHandler.addResource(ResourceType.FOOD, 100);
    }

    public void update() {
        territoryHandler.update();
        resourceHandler.update();
        populationHandler.update();
    }

    public void consumeResource(ResourceType type, int amount) {
        resourceHandler.removeResource(type, amount);
    }

    public int getResourceAmount(ResourceType type) {
        return resourceHandler.getResourceAmount(type);
    }

    @Override
    public String toString() {
        return
            "Civilisation: " + name + "\n" +
            populationHandler.toString() + "\n" +
            resourceHandler.toString();
    }
}
