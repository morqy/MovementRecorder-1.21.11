
package xyz.yuro.movementrecorder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class RotationUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public boolean rotating;
    public boolean completed;

    private long startTime;
    private long endTime;

    private float startYaw, startPitch;
    private float targetYaw, targetPitch;

    public void easeTo(float yaw, float pitch, long time) {
        completed = false;
        rotating = true;
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
        startYaw = mc.player.getYaw();
        startPitch = mc.player.getPitch();

        float yawDiff = MathHelper.wrapDegrees(yaw) - MathHelper.wrapDegrees(startYaw);
        yawDiff = AngleUtils.normalizeAngle(yawDiff);

        targetYaw = startYaw + yawDiff;
        targetPitch = pitch;
    }

    public void update() {
        if (mc.player == null) return;
        if (System.currentTimeMillis() <= endTime) {
            mc.player.setYaw(interpolate(startYaw, targetYaw));
            mc.player.setPitch(interpolate(startPitch, targetPitch));
        } else if (!completed) {
            mc.player.setYaw(targetYaw);
            mc.player.setPitch(targetPitch);
            completed = true;
            rotating = false;
        }
    }

    public void reset() {
        completed = false;
        rotating = false;
    }

    private float interpolate(float start, float end) {
        float progress = (float) (System.currentTimeMillis() - startTime) / (endTime - startTime);
        return (end - start) * easeOutCubic(progress) + start;
    }

    private float easeOutCubic(float number) {
        return (float) Math.max(0, Math.min(1, 1 - Math.pow(1 - number, 3)));
    }
}
