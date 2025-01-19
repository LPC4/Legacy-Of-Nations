package org.lpc.terrain;

/**
 * Enum representing different types of terrain in the game.
 */
public enum TerrainType {
    /**
     * Water terrain, typically representing oceans, lakes, or rivers.
     */
    WATER,

    /**
     * Beach terrain, found near water bodies.
     */
    BEACH,

    /**
     * Forest terrain, covered with dense trees.
     */
    FOREST,

    /**
     * Plains terrain, flat and open land.
     */
    PLAINS,

    /**
     * Desert terrain, characterized by sand and arid conditions.
     */
    DESERT,

    /**
     * Hills terrain, featuring rolling elevated areas.
     */
    HILLS,

    /**
     * Mountain terrain, high and rugged elevation.
     */
    MOUNTAIN;

    public static float getMovementModifier(TerrainType terrain) {
        switch (terrain) {
            case WATER:
                return 0.0f;
            case BEACH:
                return 0.7f;
            case FOREST:
            case DESERT:
                return 0.8f;
            case PLAINS:
                return 1.0f;
            case HILLS:
                return 0.6f;
            case MOUNTAIN:
                return 0.4f;
            default:
                throw new IllegalArgumentException("Invalid terrain type: " + terrain);
        }
    }
}
