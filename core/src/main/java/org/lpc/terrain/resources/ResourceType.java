package org.lpc.terrain.resources;

/**
 * Enum representing various types of resources available in the game.
 */
public enum ResourceType {
    /**
     * Wood resource type.
     * This resource is used for building construction and other purposes.
     * It can be gathered from trees and forests.
     * It is a renewable resource.
     */
    WOOD,

    /**
     * Stone resource type.
     * This resource is used for building construction and other purposes.
     * It can be gathered from stone deposits and mountains.
     * It is a non-renewable resource.
     */
    STONE,

    /**
     * Food resource type.
     * This resource is used to feed the population.
     * It can be gathered from farms and hunting grounds.
     * It is a renewable resource.
     */
    FOOD,

    /**
     * Gold resource type.
     * This resource is used as currency.
     * It can be gathered from gold mines.
     * It is a non-renewable resource.
     */
    GOLD,

    /**
     * Iron resource type.
     * This resource is used for building construction and other purposes.
     * It can be gathered from iron deposits and mountains.
     * It is a non-renewable resource.
     */
    IRON;

    public boolean isRenewable() {
        return this == WOOD || this == FOOD;
    }

    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
