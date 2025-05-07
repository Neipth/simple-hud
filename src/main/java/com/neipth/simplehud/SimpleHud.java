package com.neipth.simplehud;

import com.neipth.simplehud.config.SimpleHudConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHud implements ClientModInitializer {
	public static final String MOD_ID = "simple-hud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static SimpleHudConfig CONFIG;

	@Override
	public void onInitializeClient() {
		AutoConfig.register(SimpleHudConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(SimpleHudConfig.class).getConfig();

		HudKeyBinding.register();
	}
}