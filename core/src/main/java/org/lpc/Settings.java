package org.lpc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    private boolean renderResources;
    private boolean renderGrid;

    public Settings() {
        setDefaults();
    }

    private void setDefaults() {
        this.renderResources = false;
        this.renderGrid = true;
    }

    public boolean changeRenderResources() {
        renderResources = !renderResources;
        return renderResources;
    }

    public boolean changeRenderGrid() {
        renderGrid = !renderGrid;
        return renderGrid;
    }
}
