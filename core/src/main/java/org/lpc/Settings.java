package org.lpc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    // Rendering settings
    private boolean renderResources;
    private boolean renderGrid;

    // Game settings
    private int startingPopulation;
    private float ticksPerSecond;

    public Settings() {
        setDefaults();
    }

    private void setDefaults() {
        this.renderResources = false;
        this.renderGrid = true;
        this.startingPopulation = 50;
        this.ticksPerSecond = 20f;
    }

    public boolean changeRenderResources() {
        renderResources = !renderResources;
        return renderResources;
    }

    public boolean changeRenderGrid() {
        renderGrid = !renderGrid;
        return renderGrid;
    }

    public void increaseTPS() {
        if (ticksPerSecond * 2 <= 160f) {
            ticksPerSecond *= 2;
        }
    }

    public void decreaseTPS() {
        if (ticksPerSecond / 2 > 20f) {
            ticksPerSecond /= 2;
        }
    }
}
