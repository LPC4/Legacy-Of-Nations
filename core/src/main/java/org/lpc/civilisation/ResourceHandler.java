package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.resources.ResourceType;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class ResourceHandler {
    private final Civilisation civilisation;
    private final Map<ResourceType, Integer> resources;

    public ResourceHandler(Civilisation civilisation) {
        this.civilisation = civilisation;
        this.resources = new HashMap<>();

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public void update() {
        harvestSurfaceTiles();
    }

    private void harvestSurfaceTiles() {
        List<SurfaceMap.SurfaceTile> territory = civilisation.getTerritoryHandler().getSurfaceTerritory();

        for (SurfaceMap.SurfaceTile tile : territory) {
            Optional<Pair<ResourceType, Integer>> harvested = tile.harvestResource();

            harvested.ifPresent(pair ->
                addResource(pair.getValue0(), pair.getValue1())
            );
        }
    }

    public void addResource(ResourceType type, int amount) {
        resources.put(type, resources.get(type) + amount);
    }

    public void removeResource(ResourceType type, int amount) {
        if (resources.get(type) >= amount) {
            resources.put(type, resources.get(type) - amount);
        } else {
            resources.put(type, 0);
        }
    }

    public int getResourceAmount(ResourceType type) {
        return resources.get(type);
    }

    @Override
    public String toString() {
        // format nicely
        StringBuilder builder = new StringBuilder();
        builder.append("Resources: \n");
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            builder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }
}
