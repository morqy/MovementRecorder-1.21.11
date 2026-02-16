package xyz.yuro.movementrecorder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MovementRecorderMod implements ClientModInitializer {
    public static final String MOD_ID = "movementrecorder";
    public static KeyBinding toggleKeyBinding;

    @Override
    public void onInitializeClient() {
        MovementRecorderConfig.load();

        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.movementrecorder.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                KeyBinding.Category.MISC
        ));

        MovementRecorderCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MovementRecorder.onTickRecord();
            MovementRecorder.onTickPlay();

            while (toggleKeyBinding.wasPressed()) {
                if (MovementRecorder.isRecording() || MovementRecorder.isPlaying()) {
                    MovementRecorder.stopRecording();
                }
            }
        });

        WorldRenderEvents.END_MAIN.register(context -> {
            MovementRecorder.onRender();
        });
    }
}
