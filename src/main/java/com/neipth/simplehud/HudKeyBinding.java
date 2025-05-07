package com.neipth.simplehud;

import com.neipth.simplehud.config.SimpleHudConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HudKeyBinding {
    private static final String CATEGORY = "key.simplehud.category.simple-hud";
    private static KeyBinding showHud;

    public static void register() {
        showHud = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.simplehud.hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (showHud.wasPressed()) {
                SimpleHudConfig config = SimpleHud.CONFIG;

                String chatMessage = "key.simplehud.chat_message.on";
                if (config.uiConfig.toggleSimpleHUD) {
                    chatMessage = "key.simplehud.chat_message.off";
                }

                client.player.sendMessage(Text.translatable(chatMessage), true);
                config.uiConfig.toggleSimpleHUD = !config.uiConfig.toggleSimpleHUD;
                // read file and save to file instead of using this method to save config file modifications
                AutoConfig.getConfigHolder(SimpleHudConfig.class).save();
            }
        });
    }
}
