package me.themfcraft.rpengine.job;

import com.mojang.brigadier.CommandDispatcher;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.EntityArgument;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class JobCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("duty")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    JobManager manager = RPEngine.getJobManager();

                    if (manager.getPlayerJobData(player.getUUID()) == null) {
                        context.getSource().sendFailure(Component.literal("Du hast keinen Job!"));
                        return 0;
                    }

                    boolean onDuty = manager.toggleDuty(player);
                    String status = onDuty ? "im Dienst" : "außer Dienst";
                    int color = onDuty ? 0x55FF55 : 0xFF5555;

                    player.sendSystemMessage(Component.literal("Du bist nun " + status + ".").withStyle(s -> s.withColor(color)));
                    return 1;
                }));

        dispatcher.register(Commands.literal("job")
                .then(Commands.literal("create")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .requires(s -> s.hasPermission(2))
                                        .executes(context -> {
                                            String id = StringArgumentType.getString(context, "id");
                                            String name = StringArgumentType.getString(context, "name");
                                            RPEngine.getJobManager().createJob(id, name);
                                            context.getSource().sendSuccess(() -> Component.literal("§aJob '" + name + "' erstellt."), true);
                                            return 1;
                                        }))))
                .then(Commands.literal("addrank")
                        .then(Commands.argument("jobId", StringArgumentType.string())
                                .then(Commands.argument("rankId", StringArgumentType.string())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .then(Commands.argument("salary", IntegerArgumentType.integer(0))
                                                        .requires(s -> s.hasPermission(2))
                                                        .executes(context -> {
                                                            String jobId = StringArgumentType.getString(context, "jobId");
                                                            String rankId = StringArgumentType.getString(context, "rankId");
                                                            String name = StringArgumentType.getString(context, "name");
                                                            int salary = IntegerArgumentType.getInteger(context, "salary");
                                                            RPEngine.getJobManager().addRank(jobId, rankId, name, salary);
                                                            context.getSource().sendSuccess(() -> Component.literal("§aRang '" + name + "' zu Job '" + jobId + "' hinzugefügt."), true);
                                                            return 1;
                                                        }))))))
                .then(Commands.literal("list")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.literal("§6§lVerfügbare Jobs:"), false);
                            RPEngine.getJobManager().getRegisteredJobs().values().forEach(job -> {
                                context.getSource().sendSuccess(() -> Component.literal("§7- " + job.name() + " (" + job.id() + ")"), false);
                            });
                            return 1;
                        }))
                .then(Commands.literal("select")
                        .then(Commands.argument("jobId", StringArgumentType.string())
                                .executes(context -> {
                                    String jobId = StringArgumentType.getString(context, "jobId");
                                    Job job = RPEngine.getJobManager().getJob(jobId);
                                    if (job == null) {
                                        context.getSource().sendFailure(Component.literal("Job existiert nicht!"));
                                        return 0;
                                    }
                                    if (job.ranks().isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("Job hat keine Ränge!"));
                                        return 0;
                                    }
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    RPEngine.getJobManager().setPlayerJob(player.getUUID(), jobId, job.ranks().get(0).id());
                                    RPEngine.getJobManager().savePlayerJob(player.getUUID());
                                    context.getSource().sendSuccess(() -> Component.literal("§aDu hast den Job '" + job.name() + "' angenommen."), true);
                                    return 1;
                                })))
                .then(Commands.literal("set")
                        .requires(s -> s.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("jobId", StringArgumentType.string())
                                        .then(Commands.argument("rankId", StringArgumentType.string())
                                                .executes(context -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                                    String jobId = StringArgumentType.getString(context, "jobId");
                                                    String rankId = StringArgumentType.getString(context, "rankId");
                                                    RPEngine.getJobManager().setPlayerJob(target.getUUID(), jobId, rankId);
                                                    RPEngine.getJobManager().savePlayerJob(target.getUUID());
                                                    context.getSource().sendSuccess(() -> Component.literal("§aJob für " + target.getName().getString() + " gesetzt."), true);
                                                    return 1;
                                                }))))));
    }
}
