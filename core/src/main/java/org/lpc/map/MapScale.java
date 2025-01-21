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
     * Regional level scale
     */
    REGIONAL(4, 128),

    /**
     * Planetary level scale
     */
    PLANETARY(64, 128);

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
