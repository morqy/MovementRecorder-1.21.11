package xyz.yuro.movementrecorder;

import net.minecraft.client.MinecraftClient;
import xyz.yuro.movementrecorder.mixins.MinecraftClientAccessor;

public class KeyBindUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void rightClick() {
        ((MinecraftClientAccessor) mc).invokeDoItemUse();
    }

    public static void leftClick() {
        ((MinecraftClientAccessor) mc).invokeDoAttack();
    }

    public static void stopMovement() {
        stopMovement(false);
    }

    public static void stopMovement(boolean ignoreAttack) {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        if (!ignoreAttack) {
            mc.options.attackKey.setPressed(false);
        }
        mc.options.sneakKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);
    }
}
