package xyz.yuro.movementrecorder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LogUtils {

    public static void sendMessage(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(
                    Text.literal("")
                            .append(Text.literal("Movement Recorder ").formatted(Formatting.DARK_AQUA))
                            .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(message).formatted(Formatting.GRAY)),
                    false
            );
        }
    }

    public static void sendError(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(
                    Text.literal("")
                            .append(Text.literal("Movement Recorder ").formatted(Formatting.DARK_RED))
                            .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(message).formatted(Formatting.RED)),
                    false
            );
        }
    }

    public static void sendSuccess(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(
                    Text.literal("")
                            .append(Text.literal("Movement Recorder ").formatted(Formatting.DARK_GREEN))
                            .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(message).formatted(Formatting.GREEN)),
                    false
            );
        }
    }
}
