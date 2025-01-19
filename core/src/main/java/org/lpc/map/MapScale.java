package org.lpc.map;

import lombok.Getter;

/**
 * Enum representing different map scales in the game.
 * Each scale specifies the number of tiles per unit and pixels per tile.
 */
@Getter
public enum MapScale {
    /**
     * Surface level scale: 128 pixels per tile.
     */
    SURFACE(1, 128),

    /**
     * Regional level scale: 64 pixels per grouped tile.
     */
    REGIONAL(4, 64),

    /**
     * Continental level scale: 64 pixels per region.
     */
    CONTINENTAL(16, 64),

    /**
     * Planetary level scale: 64 pixels per planet tile.
     */
    PLANETARY(64, 64);

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
