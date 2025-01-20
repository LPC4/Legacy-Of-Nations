package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Pair<ResourceType, Integer> harvested = tile.harvestResources();

            if (harvested == null) continue;

            addResource(harvested.getValue0(), harvested.getValue1());
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
}
