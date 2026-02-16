package xyz.yuro.movementrecorder;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class MovementRecorderCommand {

    private static final SuggestionProvider<FabricClientCommandSource> RECORDING_SUGGESTIONS = (context, builder) -> {
        for (String name : MovementRecorder.getRecordingNames()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    };

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("movrec")
                    .then(ClientCommandManager.literal("start")
                            .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");
                                        MovementRecorder.startRecording(name);
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("stop")
                            .executes(context -> {
                                MovementRecorder.stopRecording();
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("play")
                            .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                    .suggests(RECORDING_SUGGESTIONS)
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");
                                        MovementRecorder.playRecording(name);
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("delete")
                            .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                    .suggests(RECORDING_SUGGESTIONS)
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");
                                        MovementRecorder.deleteRecording(name);
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("list")
                            .executes(context -> {
                                MovementRecorder.listRecordings();
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.literal("rotationType")
                                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                builder.suggest("closest90");
                                                builder.suggest("recording");
                                                builder.suggest("relative");
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String value = StringArgumentType.getString(context, "value");
                                                if (value.equalsIgnoreCase("closest90")) {
                                                    MovementRecorderConfig.rotationType = 0;
                                                } else if (value.equalsIgnoreCase("recording")) {
                                                    MovementRecorderConfig.rotationType = 1;
                                                } else if (value.equalsIgnoreCase("relative")) {
                                                    MovementRecorderConfig.rotationType = 2;
                                                } else {
                                                    LogUtils.sendError("Invalid value. Use: closest90, recording, relative");
                                                    return 0;
                                                }
                                                MovementRecorderConfig.save();
                                                LogUtils.sendSuccess("Rotation type set to: " + value);
                                                return 1;
                                            })
                                    )
                            )
                            .then(ClientCommandManager.literal("removeStartDelay")
                                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String value = StringArgumentType.getString(context, "value");
                                                MovementRecorderConfig.removeStartDelay = Boolean.parseBoolean(value);
                                                MovementRecorderConfig.save();
                                                LogUtils.sendSuccess("Remove start delay: " + MovementRecorderConfig.removeStartDelay);
                                                return 1;
                                            })
                                    )
                            )
                            .then(ClientCommandManager.literal("removeEndDelay")
                                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String value = StringArgumentType.getString(context, "value");
                                                MovementRecorderConfig.removeEndDelay = Boolean.parseBoolean(value);
                                                MovementRecorderConfig.save();
                                                LogUtils.sendSuccess("Remove end delay: " + MovementRecorderConfig.removeEndDelay);
                                                return 1;
                                            })
                                    )
                            )
                    )
            );
        });
    }
}
