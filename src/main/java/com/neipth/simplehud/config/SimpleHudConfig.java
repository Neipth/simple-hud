package com.neipth.simplehud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "simplehud")
public class SimpleHudConfig implements ConfigData {
    // Agrega una nueva categoría
    @ConfigEntry.Category("UI Config")
    @ConfigEntry.Gui.TransitiveObject
    public UIConfig uiConfig = new UIConfig();
    // Expanded
    @ConfigEntry.Category("UI Config")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public HudElements hudElements = new HudElements();

    public static class UIConfig {
        // Toggle Hud
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSimpleHUD = true;
        // ColorPicker
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int textColor = 0xFFFFFF;
        // Shadows
        @ConfigEntry.Gui.Tooltip
        public boolean textShadow = false;
        // Background Hud
        @ConfigEntry.Gui.Tooltip
        public boolean textBackground = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip
        public TextAlignment hudPosition = TextAlignment.TOP_LEFT;
    }

    public static class HudElements {
        // Coords
        @ConfigEntry.Gui.Tooltip
        public boolean toggleCoord = true;
        // FPS
        @ConfigEntry.Gui.Tooltip
        public boolean toggleFps = true;
        // Biome
        @ConfigEntry.Gui.Tooltip
        public boolean toggleBiome = true;
        // Game Time
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public GameTime gameTime = new GameTime();
        // Local Time
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public SystemTime systemTime = new SystemTime();
    }

    public static class GameTime {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameTime = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameTime24Hour = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameDayCounter = false;
    }

    public static class SystemTime {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSystemTime = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSystemTime24Hour = false;
    }
}
