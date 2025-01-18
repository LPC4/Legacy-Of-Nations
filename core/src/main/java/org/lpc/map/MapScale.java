package org.lpc.map;

import lombok.Getter;

/**
 * Enum representing different map scales in the game.
 * Each scale specifies the number of tiles per unit and pixels per tile.
 */
@Getter
public enum MapScale {
    /**
     * Surface level scale: 32 pixels per tile.
     */
    SURFACE(1, 32),

    /**
     * Regional level scale: 32 pixels per grouped tile.
     */
    REGIONAL(4, 32),

    /**
     * Continental level scale: 16 pixels per region.
     */
    CONTINENTAL(16, 16),

    /**
     * Planetary level scale: 8 pixels per large region.
     */
    PLANETARY(64, 8);

    private final int tilesPerUnit;
    private final int pixelsPerTile;

    /**
     * Constructor for MapScale enum.
     *
     * @param tilesPerUnit  Number of tiles per unit.
     * @param pixelsPerTile Number of pixels per tile.
     */
    MapScale(int tilesPerUnit, int pixelsPerTile) {
        this.tilesPerUnit = tilesPerUnit;
        this.pixelsPerTile = pixelsPerTile;
    }
}
