package org.lpc.terrain.buildings.buildings;

import lombok.Getter;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

@Getter
public class Farm extends BaseBuilding {
    public Farm() {
        super(BuildingFunctionality.RESOURCE_GATHERING, 700);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Farm
    }
}
