package dev.anye.mc.reality_value.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

@EventBusSubscriber(modid = RealityValue.MOD_ID)
public class RealityValueCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("setplayervalue")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("health")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeHealthAdd)))
                                .then(Commands.literal("rm")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeHealthRemove)))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeHealthSet)))))
                .then(Commands.literal("sanity")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeSanityAdd)))
                                .then(Commands.literal("rm")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeSanityRemove)))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(RealityValueCommand::executeSanitySet)))));

        dispatcher.register(command);
    }

    private static int executeHealthAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executeHealth(context, IntegerArgumentType.getInteger(context, "amount"));
    }

    private static int executeHealthRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executeHealth(context, -IntegerArgumentType.getInteger(context, "amount"));
    }

    private static int executeHealthSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        for (ServerPlayer player : players) {
            PlayerExCap cap = PlayerExCap.get(player);
            cap.setHealth(amount, player);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Set health for " + players.size() + " players"), true);
        return players.size();
    }

    private static int executeHealth(CommandContext<CommandSourceStack> context, int amount)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

        for (ServerPlayer player : players) {
            PlayerExCap cap = PlayerExCap.get(player);
            cap.addHealth(amount, player);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Modified health for " + players.size() + " players"), true);
        return players.size();
    }

    private static int executeSanityAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executeSanity(context, IntegerArgumentType.getInteger(context, "amount"));
    }

    private static int executeSanityRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executeSanity(context, -IntegerArgumentType.getInteger(context, "amount"));
    }

    private static int executeSanitySet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        for (ServerPlayer player : players) {
            PlayerExCap cap = PlayerExCap.get(player);
            cap.setSanity(amount, player);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Set sanity for " + players.size() + " players"), true);
        return players.size();
    }

    private static int executeSanity(CommandContext<CommandSourceStack> context, int amount)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

        for (ServerPlayer player : players) {
            PlayerExCap cap = PlayerExCap.get(player);
            cap.addSanity(amount, player);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Modified sanity for " + players.size() + " players"), true);
        return players.size();
    }
}