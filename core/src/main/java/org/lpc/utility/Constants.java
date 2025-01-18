package org.lpc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Constants {
    // Camera
    public static final float MIN_ZOOM      = 0.1f;
    public static final float MAX_ZOOM      = 10f;
    public static final float ZOOM_SPEED    = 0.5f;

    // Terrain colors
    public static final Color WATER_COLOR       = new Color(0.0f, 0.2f, 0.4f, 1.0f); // Deep blue
    public static final Color BEACH_COLOR       = new Color(0.9f, 0.8f, 0.6f, 1.0f); // Soft sandy
    public static final Color DESERT_COLOR      = new Color(0.8f, 0.7f, 0.5f, 1.0f); // Muted tan
    public static final Color FOREST_COLOR      = new Color(0.1f, 0.4f, 0.1f, 1.0f); // Dark green
    public static final Color HILLS_COLOR       = new Color(0.5f, 0.4f, 0.3f, 1.0f); // Earthy brown
    public static final Color MOUNTAIN_COLOR    = new Color(0.5f, 0.5f, 0.5f, 1.0f); // Neutral gray
    public static final Color PLAINS_COLOR      = new Color(0.3f, 0.5f, 0.2f, 1.0f); // Olive green

    // Map generation
    public static final int SURFACE_MAP_WIDTH  = 200;
    public static final int SURFACE_MAP_HEIGHT = 200;

    // Window
    public static final int WINDOW_WIDTH            = 1000;
    public static final int WINDOW_HEIGHT           = 600;
    public static final int MENU_BUTTON_WIDTH       = 200;
    public static final int MENU_BUTTON_HEIGHT      = 40;
    public static final Skin DEFAULT_BUTTON_SKIN    = new Skin(Gdx.files.internal("menu/default/uiskin.json"));
}
