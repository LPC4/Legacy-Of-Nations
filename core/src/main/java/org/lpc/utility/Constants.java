package org.lpc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


/**
 * Constants class for globals
 */
public class Constants {
    private Constants() { throw new IllegalStateException("Utility class for global constants"); }

    // Camera
    public static final float MIN_ZOOM      = 0.5f;
    public static final float MAX_ZOOM      = 15.0f;
    public static final float ZOOM_SPEED    = 0.5f;

    // Map
    public static final int SURFACE_MAP_WIDTH  = 300;
    public static final int SURFACE_MAP_HEIGHT = 300;

    // Window
    public static final int WINDOW_WIDTH            = 1000;
    public static final int WINDOW_HEIGHT           = 600;
    public static final boolean START_FULLSCREEN    = false;

    // Building delays
    public static final int SAWMILL_HARVEST_DELAY_TICKS             = 20 * 20; // 20 seconds
    public static final int FARM_HARVEST_DELAY_TICKS                = 20 * 8; // 8 seconds

    // Building rates
    public static final float FARM_HARVEST_RATE = 0.5f;
    public static final float SAWMILL_HARVEST_RATE = 0.2f;

    // Population delays
    public static final int POPULATION_CHANGE_DELAY_TICKS           = 20 * 12; // 12 seconds
    public static final int POPULATION_FOOD_CONSUMPTION_DELAY_TICKS = 20 * 12; // 12 seconds
}
