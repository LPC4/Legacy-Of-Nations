package org.lpc;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.MapSystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Getter
public class GameStateManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Logger LOGGER = LogManager.getLogger(GameStateManager.class);

    private MapSystem mapSystem;

    public GameStateManager(MainGame game) {
        LOGGER.info("Initializing GameStateManager");
        this.mapSystem = new MapSystem(game);
    }

    public void update() {
        mapSystem.update();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        LOGGER.info("Serializing GameStateManager");

        // Custom serialization logic for important map data
        // Add your custom serialization logic here
        out.writeObject(mapSystem);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Reinitialize transient fields
        LOGGER = LogManager.getLogger(GameStateManager.class);
        LOGGER.info("Deserializing GameStateManager");

        // Custom deserialization logic for important map data
        // Add custom deserialization logic here
        mapSystem = (MapSystem) in.readObject();
    }
}
