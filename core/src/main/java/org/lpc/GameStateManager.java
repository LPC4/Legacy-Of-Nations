package org.lpc;

import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.civilisation.Civilisation;
import org.lpc.map.BaseMap;
import org.lpc.map.MapSystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The GameStateManager class is responsible for managing the game state, including the map system and the civilisation.
 * It is responsible for updating the map system and the civilisation.
 * It is also responsible for serializing and deserializing the game state.
 */
@Getter
public class GameStateManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient Logger LOGGER = LogManager.getLogger(GameStateManager.class);

    private final @NonNull MapSystem mapSystem;
    private final @NonNull Civilisation civilisation;

    public GameStateManager(@NonNull MainGame game, @NonNull Settings settings) {
        LOGGER.info("Initializing GameStateManager");
        this.mapSystem = new MapSystem(game);
        this.civilisation = new Civilisation(game, this, "Test", settings.getStartingPopulation());
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
