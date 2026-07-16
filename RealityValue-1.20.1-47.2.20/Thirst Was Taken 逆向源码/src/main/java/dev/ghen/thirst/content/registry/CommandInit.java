package dev.ghen.thirst.content.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.network.ThirstModPacketHandler;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Thirst.f0ID)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/registry/CommandInit.class */
public class CommandInit {
    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.m_82127_(Thirst.f0ID).requires(cs -> {
            return cs.m_6761_(2);
        }).then(Commands.m_82127_("query").then(Commands.m_82129_("Player", EntityArgument.m_91466_()).executes(context -> {
            ServerPlayer player = EntityArgument.m_91474_(context, "Player");
            IThirst iThirst = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
            Object[] arg = {Integer.valueOf(iThirst.getThirst()), Integer.valueOf(iThirst.getQuenched())};
            ((CommandSourceStack) context.getSource()).m_288197_(() -> {
                return MutableComponent.m_237204_(new TranslatableContents("command.thirst.query", "command.thirst.query", arg));
            }, false);
            return 0;
        }))).then(Commands.m_82127_("set").then(Commands.m_82129_("Player", EntityArgument.m_91466_()).then(Commands.m_82129_(Thirst.f0ID, IntegerArgumentType.integer(0, 20)).then(Commands.m_82129_("quenched", IntegerArgumentType.integer(0, 20)).executes(context2 -> {
            ServerPlayer player = EntityArgument.m_91474_(context2, "Player");
            IThirst iThirst = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
            Object[] arg = {Integer.valueOf(IntegerArgumentType.getInteger(context2, Thirst.f0ID)), Integer.valueOf(IntegerArgumentType.getInteger(context2, "quenched"))};
            iThirst.setThirst(((Integer) arg[0]).intValue());
            iThirst.setQuenched(((Integer) arg[1]).intValue());
            ((CommandSourceStack) context2.getSource()).m_288197_(() -> {
                return MutableComponent.m_237204_(new TranslatableContents("command.thirst.set", "command.thirst.set", arg));
            }, false);
            return 0;
        }))))).then(Commands.m_82127_("enable").then(Commands.m_82129_("Player", EntityArgument.m_91470_()).then(Commands.m_82129_("bool", BoolArgumentType.bool()).executes(context3 -> {
            Collection<ServerPlayer> players = EntityArgument.m_91477_(context3, "Player");
            boolean shouldTick = BoolArgumentType.getBool(context3, "bool");
            Collection<Component> playersName = new ArrayList<>();
            for (ServerPlayer player : players) {
                IThirst thirstData = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
                thirstData.setShouldTickThirst(shouldTick);
                ThirstModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> {
                    return player;
                }), new PlayerThirstSyncMessage(shouldTick));
                playersName.add(player.m_7755_());
            }
            if (shouldTick) {
                ((CommandSourceStack) context3.getSource()).m_288197_(() -> {
                    return MutableComponent.m_237204_(new TranslatableContents("command.thirst.enable", "command.thirst.enable", playersName.toArray()));
                }, false);
                return 0;
            }
            ((CommandSourceStack) context3.getSource()).m_288197_(() -> {
                return MutableComponent.m_237204_(new TranslatableContents("command.thirst.disable", "command.thirst.disable", playersName.toArray()));
            }, false);
            return 0;
        })))));
    }
}
