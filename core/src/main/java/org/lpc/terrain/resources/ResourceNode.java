package org.lpc.terrain.resources;

import java.util.*;

public class ResourceNode {
    private final EnumMap<ResourceType, Integer> resources;

    public ResourceNode() {
        this.resources = new EnumMap<>(ResourceType.class);
        // Initialize all resource types with zero quantity
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    /**
     * Adds specified quantity to a resource type
     * @param type Resource type to modify
     * @param quantity Positive number to add
     */
    public void addResource(ResourceType type, int quantity) {
        validatePositiveQuantity(quantity);
        resources.put(type, resources.get(type) + quantity);
    }

    /**
     * Gets list of resource types with available quantities
     * @return Unmodifiable list of available resources
     */
    public List<ResourceType> getAvailableResources() {
        List<ResourceType> available = new ArrayList<>();
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            if (entry.getValue() > 0) {
                available.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(available);
    }

    /**
     * Gets count of different resource types with positive quantities
     */
    public int getDifferentResourceCount() {
        int count = 0;
        for (int quantity : resources.values()) {
            if (quantity > 0) count++;
        }
        return count;
    }

    public int getResourceQuantity(ResourceType type) {
        return resources.get(type);
    }

    public boolean isDepleted(ResourceType type) {
        return resources.get(type) <= 0;
    }

    /**
     * Harvests resources and reduces the available quantity
     * @return Actual amount harvested
     * @throws IllegalArgumentException if amount is invalid
     */
    public int harvestResource(ResourceType type, int amount) {
        validatePositiveQuantity(amount);
        int available = resources.get(type);

        if (available < amount) {
            throw new IllegalArgumentException(String.format(
                "Cannot harvest %d %s (only %d available)",
                amount, type, available
            ));
        }

        if (!type.isRenewable()) {
            resources.put(type, available - amount);
        }
        return amount;
    }

    /**
     * Checks maximum harvestable amount without modifying storage
     * @return Maximum amount that can be harvested
     */
    public int calculateHarvestableAmount(ResourceType type, int requestedAmount) {
        validatePositiveQuantity(requestedAmount);
        int available = resources.get(type);
        return Math.min(available, requestedAmount);
    }

    /**
     * Replenishes resources by specified amount
     * @return New total quantity
     */
    public int replenishResource(ResourceType type, int amount) {
        validatePositiveQuantity(amount);
        int newTotal = resources.get(type) + amount;
        resources.put(type, newTotal);
        return newTotal;
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Resource Node Contents:\n");
        // Find the longest resource name for proper alignment
        int maxNameLength = 0;
        for (ResourceType type : ResourceType.values()) {
            maxNameLength = Math.max(maxNameLength, type.name().length());
        }

        // Create format string based on longest name
        String format = "- %-" + (maxNameLength + 2) + "s: %6d%n";

        for (ResourceType type : ResourceType.values()) {
            sb.append(String.format(format, type, resources.get(type)));
        }

        return sb.toString();
    }
}
