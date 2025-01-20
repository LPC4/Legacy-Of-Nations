package org.lpc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Constants {
    // Camera
    public static final float MIN_ZOOM      = 0.5f;
    public static final float MAX_ZOOM      = 15.0f;
    public static final float ZOOM_SPEED    = 0.5f;

    // Muted Terrain colors
    public static final Color WATER_COLOR       = new Color(0.1f, 0.3f, 0.5f, 1.0f); // Softer deep blue
    public static final Color BEACH_COLOR       = new Color(0.8f, 0.7f, 0.5f, 1.0f); // More muted sandy
    public static final Color DESERT_COLOR      = new Color(0.7f, 0.6f, 0.4f, 1.0f); // Softer muted tan
    public static final Color FOREST_COLOR      = new Color(0.2f, 0.5f, 0.2f, 1.0f); // Softer dark green
    public static final Color HILLS_COLOR       = new Color(0.4f, 0.3f, 0.2f, 1.0f); // Muted earthy brown
    public static final Color MOUNTAIN_COLOR    = new Color(0.6f, 0.6f, 0.6f, 1.0f); // Softer neutral gray
    public static final Color PLAINS_COLOR      = new Color(0.4f, 0.6f, 0.3f, 1.0f); // Softer olive green

    // Terrain colors (original)
    public static final Color WATER_COLOR_1       = new Color(0.1f, 0.3f, 0.5f, 1.0f); // Softer deep blue
    public static final Color BEACH_COLOR_1       = new Color(0.8f, 0.7f, 0.5f, 1.0f); // More muted sandy
    public static final Color DESERT_COLOR_1      = new Color(0.7f, 0.6f, 0.4f, 1.0f); // Softer muted tan
    public static final Color FOREST_COLOR_1      = new Color(0.2f, 0.5f, 0.2f, 1.0f); // Softer dark green
    public static final Color HILLS_COLOR_1       = new Color(0.4f, 0.3f, 0.2f, 1.0f); // Muted earthy brown
    public static final Color MOUNTAIN_COLOR_1    = new Color(0.6f, 0.6f, 0.6f, 1.0f); // Softer neutral gray
    public static final Color PLAINS_COLOR_1      = new Color(0.4f, 0.6f, 0.3f, 1.0f); // Softer olive green

    // Resource colors
    public static final Color FOOD_COLOR       = new Color(0.0f, 0.8f, 0.0f, 1.0f); // Bright green
    public static final Color GOLD_COLOR       = new Color(1.0f, 0.8f, 0.0f, 1.0f); // Bright yellow
    public static final Color IRON_COLOR       = new Color(0.8f, 0.8f, 0.8f, 1.0f); // Light gray
    public static final Color STONE_COLOR      = new Color(0.5f, 0.5f, 0.5f, 1.0f); // Neutral gray
    public static final Color WOOD_COLOR       = new Color(0.5f, 0.3f, 0.0f, 1.0f); // Dark brown

    // Map generation
    public static final int SURFACE_MAP_WIDTH  = 200;
    public static final int SURFACE_MAP_HEIGHT = 200;

    // Window
    public static final int WINDOW_WIDTH            = 1000;
    public static final int WINDOW_HEIGHT           = 600;
    public static final int MENU_BUTTON_WIDTH       = 200;
    public static final int MENU_BUTTON_HEIGHT      = 40;
    public static final Skin DEFAULT_BUTTON_SKIN    = new Skin(Gdx.files.internal("menu/default/uiskin.json"));
    public static final boolean START_FULLSCREEN    = false;

    // Textures
    public static final Texture VEGETATION_TEXTURE  = new Texture("textures/vegetation.png");;

    // Delays
    public static final int SAWMILL_HARVEST_DELAY_TICKS = 20 * 20; // 20 seconds
    public static final int FARM_HARVEST_DELAY_TICKS = 20 * 10; // 15 seconds

    public static final int POPULATION_CHANGE_DELAY_TICKS = 20 * 2; // 60 seconds
    public static final int POPULATION_FOOD_CONSUMPTION_DELAY_TICKS = 20 * 5; // 1 second

    // Rates
    public static final float FARM_HARVEST_RATE = 0.5f;
    public static final float SAWMILL_HARVEST_RATE = 0.2f;
}
