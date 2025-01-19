package org.lpc.map;

public interface IMapGenerator {
    void generateTerrain(BaseMap.BaseTile[][] tiles, int width, int height);
    void generateResources(BaseMap.BaseTile[][] tiles);
}
