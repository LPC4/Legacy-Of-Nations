package org.lpc.map;

public interface IMapGenerator<T extends BaseMap.BaseTile> {
    void generateTerrain(T[][] tiles, int width, int height);
    void generateResources(T[][] tiles);
}
