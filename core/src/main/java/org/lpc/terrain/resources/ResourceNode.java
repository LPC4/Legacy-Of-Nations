package org.lpc.terrain.resources;

import java.util.ArrayList;
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

    public ArrayList<ResourceType> getAvailableResources() {
        ArrayList<ResourceType> availableResources = new ArrayList<>();
        for (ResourceType type : ResourceType.values()) {
            if (resources.get(type) > 0) {
                availableResources.add(type);
            }
        }
        return availableResources;
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

    public int harvestResource(ResourceType type, int amount) {
        if (amount > 0 && resources.getOrDefault(type, 0) >= amount) {
            resources.put(type, resources.get(type) - amount);
            return amount;
        } else {
            throw new IllegalArgumentException("Invalid amount to harvest");
        }
    }

    public int harvestReplicableResource(ResourceType type, int amount) {
        if (amount > 0 && resources.getOrDefault(type, 0) >= amount) {
            return amount;
        } else {
            throw new IllegalArgumentException("Invalid amount to harvest");
        }
    }

    public int replenishResource(ResourceType type, int amount) {
        if (amount > 0) {
            resources.put(type, resources.get(type) + amount);
            return amount;
        } else {
            throw new IllegalArgumentException("Invalid amount to replenish");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resources: \n");
        for (ResourceType type : ResourceType.values()) {
            sb.append(type.toString()).append(": ").append(resources.get(type)).append("\n");
        }
        return sb.toString();
    }
}
