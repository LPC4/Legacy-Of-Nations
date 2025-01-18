package org.lpc.map;

import lombok.Getter;

@Getter
public enum MapScale {
    SURFACE(1, 32),      // 32px per tile
    REGIONAL(4, 32),     // 32px per grouped tile
    CONTINENTAL(16, 16), // 16px per region
    PLANETARY(64, 8);    // 8px per large region

    final int tilesPerUnit;
    final int pixelsPerTile;

    MapScale(int tilesPerUnit, int pixelsPerTile) {
        this.tilesPerUnit = tilesPerUnit;
        this.pixelsPerTile = pixelsPerTile;
    }
}
