package xyz.yuro.movementrecorder;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MovementRecorder {
    private static final List<Movement> movements = new ArrayList<>();
    private static boolean isMovementRecording = false;
    private static boolean isMovementPlaying = false;
    private static boolean isMovementReading = false;
    private static int currentDelay = 0;
    private static int playingIndex = 0;
    private static float yawDifference = 0;
    private static String recordingName = "";
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final RotationUtils rotateBeforePlaying = new RotationUtils();
    private static final RotationUtils rotateDuringPlaying = new RotationUtils();

    public static class Movement {
        final boolean forward;
        final boolean left;
        final boolean backwards;
        final boolean right;
        final boolean sneak;
        final boolean sprint;
        final boolean fly;
        final boolean jump;
        final boolean attack;
        final boolean useItem;
        final float yaw;
        final float pitch;
        int delay;

        public Movement(boolean forward, boolean left, boolean backwards, boolean right,
                        boolean sneak, boolean sprint, boolean fly, boolean jump,
                        boolean attack, boolean useItem, float yaw, float pitch, int delay) {
            this.forward = forward;
            this.left = left;
            this.backwards = backwards;
            this.right = right;
            this.sneak = sneak;
            this.sprint = sprint;
            this.fly = fly;
            this.jump = jump;
            this.attack = attack;
            this.useItem = useItem;
            this.yaw = yaw;
            this.pitch = pitch;
            this.delay = delay;
        }

        public String toCsv() {
            return forward + ";" + left + ";" + backwards + ";" + right + ";" +
                    sneak + ";" + sprint + ";" + fly + ";" + jump + ";" +
                    attack + ";" + useItem + ";" + yaw + ";" + pitch + ";" + delay;
        }
    }

    public static void onTickRecord() {
        if (mc.player == null || mc.world == null) return;
        if (!isMovementRecording) return;

        Movement currentMovement = getCurrentMovement();

        if (!movements.isEmpty()) {
            Movement prev = movements.get(movements.size() - 1);
            if (currentMovement.forward == prev.forward &&
                    currentMovement.left == prev.left &&
                    currentMovement.backwards == prev.backwards &&
                    currentMovement.right == prev.right &&
                    currentMovement.sneak == prev.sneak &&
                    currentMovement.sprint == prev.sprint &&
                    currentMovement.fly == prev.fly &&
                    currentMovement.jump == prev.jump &&
                    currentMovement.attack == prev.attack &&
                    currentMovement.useItem == prev.useItem &&
                    currentMovement.yaw == prev.yaw &&
                    currentMovement.pitch == prev.pitch) {
                prev.delay++;
                return;
            }
        }
        movements.add(currentMovement);
    }

    public static void onTickPlay() {
        if (mc.player == null || mc.world == null) return;
        if (!isMovementPlaying || isMovementReading) return;

        if (movements.isEmpty()) {
            LogUtils.sendError("The file is empty!");
            stopRecording();
            return;
        }

        if (rotateBeforePlaying.rotating) {
            KeyBindUtils.stopMovement();
            return;
        }

        Movement movement = movements.get(playingIndex);
        setPlayerMovement(movement);

        float calculatedYaw = 0;
        if (MovementRecorderConfig.rotationType == 0) { // Closest 90°
            calculatedYaw = movement.yaw + yawDifference;
        } else if (MovementRecorderConfig.rotationType == 1) { // Recording's yaw
            calculatedYaw = movement.yaw;
        } else if (MovementRecorderConfig.rotationType == 2) { // Player's yaw (relative)
            calculatedYaw = movement.yaw - yawDifference;
        }
        rotateDuringPlaying.easeTo(calculatedYaw, movement.pitch, 49);

        if (currentDelay < movement.delay) {
            currentDelay++;
            return;
        }
        playingIndex++;
        currentDelay = 0;
        if (playingIndex >= movements.size()) {
            isMovementPlaying = false;
            resetTimers();
            KeyBindUtils.stopMovement();
            LogUtils.sendMessage("Playing has been finished.");
        }
    }

    public static void onRender() {
        if (rotateDuringPlaying.rotating) {
            rotateDuringPlaying.update();
            return;
        }
        if (rotateBeforePlaying.rotating) {
            rotateBeforePlaying.update();
        }
    }

    public static void startRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("Recording has already started.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing now.");
            return;
        }
        if (isMovementReading) {
            LogUtils.sendError("The recording is being read now.");
            return;
        }
        movements.clear();
        playingIndex = 0;
        currentDelay = 0;
        recordingName = name;
        isMovementPlaying = false;
        isMovementRecording = true;
        LogUtils.sendSuccess("Recording " + recordingName + " has been started.");
        LogUtils.sendMessage("Type /movrec stop to stop recording.");
    }

    public static void stopRecording() {
        playingIndex = 0;
        currentDelay = 0;
        resetTimers();
        KeyBindUtils.stopMovement();
        if (isMovementRecording) {
            isMovementRecording = false;
            saveRecording();
            LogUtils.sendSuccess("Recording has been stopped.");
            return;
        }
        if (isMovementPlaying || isMovementReading) {
            isMovementPlaying = false;
            isMovementReading = false;
            LogUtils.sendSuccess("Playing has been stopped.");
            return;
        }
        LogUtils.sendError("No recording has been started.");
    }

    public static void playRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("You are recording now!");
            LogUtils.sendError("Type /movrec stop to stop recording.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing already.");
            return;
        }
        movements.clear();
        playingIndex = 0;
        resetTimers();
        isMovementReading = true;
        try {
            Path recordingPath = getRecordingDir().resolve(name + ".movement");
            List<String> lines = Files.readAllLines(recordingPath);
            for (String line : lines) {
                if (!isMovementReading) return;
                String[] split = line.split(";");
                Movement movement = new Movement(
                        Boolean.parseBoolean(split[0]),
                        Boolean.parseBoolean(split[1]),
                        Boolean.parseBoolean(split[2]),
                        Boolean.parseBoolean(split[3]),
                        Boolean.parseBoolean(split[4]),
                        Boolean.parseBoolean(split[5]),
                        Boolean.parseBoolean(split[6]),
                        Boolean.parseBoolean(split[7]),
                        Boolean.parseBoolean(split[8]),
                        Boolean.parseBoolean(split[9]),
                        Float.parseFloat(split[10]),
                        Float.parseFloat(split[11]),
                        Integer.parseInt(split[12])
                );
                movements.add(movement);
            }
        } catch (Exception e) {
            LogUtils.sendError("An error occurred while playing the recording.");
            e.printStackTrace();
            isMovementReading = false;
            return;
        }
        isMovementReading = false;
        isMovementPlaying = true;
        Movement movement = movements.get(0);

        float calculatedYaw = 0;
        if (MovementRecorderConfig.rotationType == 0) { // Closest 90°
            yawDifference = AngleUtils.normalizeAngle(AngleUtils.getClosest90() - movement.yaw);
            calculatedYaw = movement.yaw + yawDifference;
        } else if (MovementRecorderConfig.rotationType == 1) { // Recording's yaw
            calculatedYaw = movement.yaw;
        } else if (MovementRecorderConfig.rotationType == 2) { // Player's yaw (relative)
            yawDifference = AngleUtils.normalizeAngle(movement.yaw - AngleUtils.get360RotationYaw());
            calculatedYaw = mc.player.getYaw();
        }
        rotateBeforePlaying.easeTo(calculatedYaw, movement.pitch, 500);
    }

    private static void saveRecording() {
        Path recordingDir = getRecordingDir();
        try {
            Files.createDirectories(recordingDir);
        } catch (IOException e) {
            LogUtils.sendError("Failed to create recording directory.");
            e.printStackTrace();
            return;
        }

        Path recordingFile = recordingDir.resolve(recordingName + ".movement");
        try {
            if (MovementRecorderConfig.removeStartDelay && !movements.isEmpty())
                movements.get(0).delay = 0;
            if (MovementRecorderConfig.removeEndDelay && !movements.isEmpty())
                movements.get(movements.size() - 1).delay = 0;
            try (PrintWriter pw = new PrintWriter(recordingFile.toFile())) {
                for (Movement movement : movements) {
                    pw.println(movement.toCsv());
                }
                LogUtils.sendSuccess("Recording " + recordingName + " has been saved.");
            }
        } catch (Exception e) {
            LogUtils.sendError("An error occurred while saving the recording.");
            e.printStackTrace();
        }
        movements.clear();
    }

    public static void deleteRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("You are recording now!");
            LogUtils.sendError("Type /movrec stop to stop recording.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing now!");
            return;
        }
        if (isMovementReading) {
            LogUtils.sendError("The recording is being read now!");
            return;
        }
        Path recordingFile = getRecordingDir().resolve(name + ".movement");
        if (Files.exists(recordingFile)) {
            try {
                Files.delete(recordingFile);
                LogUtils.sendSuccess("Recording " + name + " has been deleted.");
            } catch (IOException e) {
                LogUtils.sendError("Failed to delete recording " + name + ".");
                e.printStackTrace();
            }
        } else {
            LogUtils.sendError("Recording " + name + " does not exist.");
        }
    }

    public static void listRecordings() {
        Path recordingDir = getRecordingDir();
        if (!Files.exists(recordingDir) || !Files.isDirectory(recordingDir)) {
            LogUtils.sendError("Recording directory does not exist.");
            return;
        }
        File[] recordingFiles = recordingDir.toFile().listFiles();
        if (recordingFiles == null || recordingFiles.length == 0) {
            LogUtils.sendError("No recordings found.");
            return;
        }
        LogUtils.sendMessage("Recordings:");
        for (File file : recordingFiles) {
            if (file.isFile() && file.getName().endsWith(".movement")) {
                LogUtils.sendMessage("- " + file.getName().replace(".movement", ""));
            }
        }
    }

    public static List<String> getRecordingNames() {
        List<String> names = new ArrayList<>();
        Path recordingDir = getRecordingDir();
        if (!Files.exists(recordingDir)) return names;
        File[] files = recordingDir.toFile().listFiles();
        if (files == null) return names;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".movement")) {
                names.add(file.getName().replace(".movement", ""));
            }
        }
        return names;
    }

    private static Path getRecordingDir() {
        return FabricLoader.getInstance().getGameDir().resolve("movementrecorder");
    }

    private static void resetTimers() {
        rotateBeforePlaying.reset();
        rotateDuringPlaying.reset();
    }

    private static void setPlayerMovement(Movement movement) {
        mc.options.forwardKey.setPressed(movement.forward);
        mc.options.leftKey.setPressed(movement.left);
        mc.options.backKey.setPressed(movement.backwards);
        mc.options.rightKey.setPressed(movement.right);
        mc.options.sneakKey.setPressed(movement.sneak);
        mc.player.setSprinting(movement.sprint);
        if (mc.player.getAbilities().allowFlying && mc.player.getAbilities().flying != movement.fly)
            mc.player.getAbilities().flying = movement.fly;
        mc.options.jumpKey.setPressed(movement.jump);
        if (currentDelay == 0) {
            if (movement.attack) KeyBindUtils.leftClick();
            if (movement.useItem) KeyBindUtils.rightClick();
        }
        mc.options.attackKey.setPressed(movement.attack);
        mc.options.useKey.setPressed(movement.useItem);
    }

    private static Movement getCurrentMovement() {
        return new Movement(
                mc.options.forwardKey.isPressed(),
                mc.options.leftKey.isPressed(),
                mc.options.backKey.isPressed(),
                mc.options.rightKey.isPressed(),
                mc.options.sneakKey.isPressed(),
                mc.player.isSprinting(),
                mc.player.getAbilities().flying,
                mc.options.jumpKey.isPressed(),
                mc.options.attackKey.isPressed(),
                mc.options.useKey.isPressed(),
                mc.player.getYaw(),
                mc.player.getPitch(),
                0
        );
    }

    public static boolean isRecording() {
        return isMovementRecording;
    }

    public static boolean isPlaying() {
        return isMovementPlaying;
    }

    public static boolean isReading() {
        return isMovementReading;
    }
}
