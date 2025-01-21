package org.lpc;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.civilisation.Civilisation;
import org.lpc.map.BaseMap;
import org.lpc.map.MapSystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Getter
public class GameStateManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient Logger LOGGER = LogManager.getLogger(GameStateManager.class);

    private final MapSystem mapSystem;
    private final Civilisation civilisation;

    public GameStateManager(MainGame game) {
        LOGGER.info("Initializing GameStateManager");
        this.mapSystem = new MapSystem(game);
        this.civilisation = new Civilisation(game, this, "Test", game.getSettings().getStartingPopulation());
    }

    public void update() {
        mapSystem.update();
        civilisation.update();
    }

    public BaseMap<?> getMap() {
        return mapSystem.getMap();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        LOGGER.info("Serializing GameStateManager");

        // Custom serialization logic for important map data
        // Add custom serialization logic here
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        LOGGER.info("Deserializing GameStateManager");

        // Custom deserialization logic for important map data
        // Add custom deserialization logic here
        in.defaultReadObject();
    }
}
