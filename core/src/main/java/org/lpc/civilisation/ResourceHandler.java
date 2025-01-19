package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.lpc.map.BaseMap;
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

    public void harvestResource(ResourceType type, int amount) {
        List<BaseMap.BaseTile> territory = civilisation.getTerritoryHandler().getTerritory();
        int totalAmount = 0;

        for (BaseMap.BaseTile tile : territory) {
            totalAmount += tile.harvest(type, amount);
        }

        addResource(type, totalAmount);
    }

    public void addResource(ResourceType type, int amount) {
        resources.put(type, resources.get(type) + amount);
    }

    public void removeResource(ResourceType type, int amount) {
        if (resources.get(type) >= amount) {
            resources.put(type, resources.get(type) - amount);
        } else {
            throw new IllegalArgumentException("Invalid amount to remove");
        }
    }
}
