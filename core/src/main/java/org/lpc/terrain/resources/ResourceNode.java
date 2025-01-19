package org.lpc.terrain.resources;

import java.util.HashMap;
import java.util.Map;

public class ResourceNode {
    private final Map<ResourceType, Integer> resources;

    public ResourceNode() {
        this.resources = new HashMap<>();

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public void addResource(ResourceType type, int quantity) {
        resources.put(type, resources.getOrDefault(type, 0) + quantity);
    }

    public int getDifferentResourceCount() {
        // get all resources that have a quantity greater than 0
        return (int) resources.entrySet().stream().filter(entry -> entry.getValue() > 0).count();
    }

    public int getResourceQuantity(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    public boolean isDepleted(ResourceType type) {
        return resources.getOrDefault(type, 0) <= 0;
    }

    public void harvestResource(ResourceType type, int amount) {
        if (amount > 0 && resources.getOrDefault(type, 0) >= amount) {
            resources.put(type, resources.get(type) - amount);
        } else {
            throw new IllegalArgumentException("Invalid amount to harvest");
        }
    }
}
