package com.neipth.simplehud.mixin;

import com.neipth.simplehud.SimpleHud;
import com.neipth.simplehud.config.SimpleHudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(InGameHud.class)
public class HudMixin {
    @Unique
    private float currentPosX = 0; // Posición actual (animada)
    @Unique
    private float currentPosY = 0;

	@Inject(at = @At("TAIL"), method = "render")
	public void render(DrawContext context, float tickDelta, CallbackInfo info) throws Exception {
		MinecraftClient client = MinecraftClient.getInstance();
		SimpleHudConfig config = SimpleHud.CONFIG;

		// Check if HUD is enabled
		if (!config.uiConfig.toggleSimpleHUD) return;

        // Lista de texto
        // @params config
        // @params client
        List<String> textLines = getStrings(config, client);

        if (textLines.isEmpty()) return;

        // Ajusta el tamaño de la GUI
        // Si el usuario tiene ajustada una escala de pantalla (ej: 125%, 150% en Windows),
        // devolverá 1.25, 1.5, etc.
        double guiScale = client.getWindow().getScaleFactor();

        // Content Fill y Obtengo el ancho y alto total del texto
        int padding = 2;
        int backgroundColor = 0x80000000;
        String longestLine = getLongestString(textLines);
        int textWidth = client.textRenderer.getWidth(longestLine);
        int textHeight = textLines.size() * client.textRenderer.fontHeight;

        // Calcular posición basada en la configuración
        int textPosX, textPosY;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        switch (config.uiConfig.hudPosition) {
            case TOP_RIGHT:
                textPosX = screenWidth - textWidth - padding - 4; // 2 es el margen mínimo
                textPosY = 4;
                break;
            case BOTTOM_LEFT:
                textPosX = 4;
                textPosY = screenHeight - textHeight - padding - 4;
                break;
            case BOTTOM_RIGHT:
                textPosX = screenWidth - textWidth - padding - 4;
                textPosY = screenHeight - textHeight - padding - 4;
                break;
            case TOP_LEFT:
            default:
                textPosX = 4;
                textPosY = 4;
        }

        // Interpolación lineal para animación suave (ajusta 0.1f para más/menos velocidad)
        currentPosX += (textPosX - currentPosX) * 0.05f;
        currentPosY += (textPosY - currentPosY) * 0.05f;

        // Dibujar en la posición animada
        int finalPosX = (int) currentPosX;
        int finalPosY = (int) currentPosY;

        // Dibujar el fondo
        if (config.uiConfig.textBackground) {
            // Posición del fondo (ajustada con padding)
            int backgroundX = finalPosX - padding;
            int backgroundY = finalPosY - padding;
            int backgroundWidth = textWidth + 2 * padding;
            int backgroundHeight = textHeight + 2 * padding;

            context.fill(backgroundX, backgroundY, backgroundX + backgroundWidth, backgroundY + backgroundHeight, backgroundColor);
        }

        // Draw
        int textColor = config.uiConfig.textColor;
        for (int i = 0; i < textLines.size(); i++) {
            String line = textLines.get(i);
            int linePosY = finalPosY + i *(client.textRenderer.fontHeight);
            context.drawText(client.textRenderer, line, finalPosX, linePosY, textColor, config.uiConfig.textShadow);
        }
	}

    @Unique
    @NotNull
    private static List<String> getStrings(SimpleHudConfig config, MinecraftClient client) {
        List<String> lines = new ArrayList<>();

        // Coords
        if (config.hudElements.toggleCoord) {
            lines.add(getCords(client));
        }

        // FPS
        if (config.hudElements.toggleFps) {
            lines.add(getFPS(client));
        }

        // Biome
        if (config.hudElements.toggleBiome) {
            lines.add(getBiomes(client));
        }

        // World Time
        if (config.hudElements.gameTime.toggleGameTime) {
            lines.add(getTime(client, config));
        }

        // World Days
        if (config.hudElements.gameTime.toggleGameDayCounter) {
            lines.add(getDay(client));
        }

        // Local Time
        if (config.hudElements.systemTime.toggleSystemTime) {
            lines.add(getTimeLocal(config));
        }

        return lines;
    }

    @Unique
    private String getLongestString(List<String> textLines) {
        return textLines.stream()
                .reduce("",(longestText, text) -> longestText.length() < text.length() ? text : longestText);
    }

    @Unique
    private static String getCords(MinecraftClient client) {
        // Los assert se usan para detectar errores.
        assert client.player != null;
        return "X: " + Math.round(client.player.getX()) + " Y: " + Math.round(client.player.getY()) + " Z: " + Math.round(client.player.getZ());
    }

    @Unique
    private static String getBiomes(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return "";
        }

        // Obtener la posición del jugador
        BlockPos pos = client.player.getBlockPos();
        // Acceder al registro de biomas y obtener su ID
        Optional<RegistryKey<Biome>> biomeKey = client.world.getBiome(pos).getKey();

        String biomeId = biomeKey.get().getValue().getPath(); // "plains"
        String formattedName = Arrays.stream(biomeId.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" ")); // "Plains" o "Snowy Plains"

        return "Bioma: " + Text.translatable(formattedName).getString();
    }

    @Unique
    // Get Player FPS
    private static String getFPS(MinecraftClient client) {
        return "FPS: " + client.getCurrentFps();
    }

    @Unique
    // Get World Time
    private static String getTime(MinecraftClient client, SimpleHudConfig config) {
        assert client.player != null;
        long time = client.player.getWorld().getTimeOfDay();

        if (config.hudElements.gameTime.toggleGameTime24Hour) {
            //24-hour format
            int hour = (int) (time / 1000 + 6) % 24;
            int minute = (int) ((time % 1000) / 1000.0 * 60);
            return String.format("Hora: %02d:%02d", hour, minute);
        }

        // 12-hour format
        int hour = (int) (time / 1000 + 6) % 24;
        int minute = (int) ((time % 1000) / 1000.0 * 60);

        // Formatear a 12h y asegurar 2 dígitos (05 en lugar de 5)
        String ampm = hour < 12 ? "AM" : "PM";
        if (hour > 12) hour -= 12;
        if (hour == 0) hour = 12;

        return String.format("Hora: %02d:%02d %s", hour, minute, ampm);
    }

    @Unique
    // Get World Day
    private static String getDay(MinecraftClient client) {
        assert client.player != null;
        long time = client.player.getWorld().getTimeOfDay();
        long day = (time / 24000) + 1;
        long daytime = time % 24000; // Ticks dentro del día actual

        String phase = (daytime < 13000) ? "☀ Día" : "🌙 Noche";
        return "Día: " + day + " | " + phase;
    }

    @Unique
    // Get Time Local
    private static String getTimeLocal(SimpleHudConfig config) {
        // Hora actual
        LocalTime now = LocalTime.now();

        // Formato 24hs
        if (config.hudElements.systemTime.toggleSystemTime24Hour) {
            DateTimeFormatter formatter24h = DateTimeFormatter.ofPattern("HH:mm:ss");

            return "Hora PC: " + now.format(formatter24h) + " ";
        }

        // Formato 12hs
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return "Hora PC: " + now.format(formatter);
    }
}