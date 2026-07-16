package dev.anye.mc.reality_value.net;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.PlayerExNet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Messages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }
    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(RealityValue.MOD_ID,"messages")).networkProtocolVersion(()->"1.0").serverAcceptedVersions(s -> true).clientAcceptedVersions(s -> true).simpleChannel();
        INSTANCE = net;
        net.messageBuilder(PlayerExNet.SendToClient.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerExNet.SendToClient::new)
                .encoder(PlayerExNet.SendToClient::toBytes)
                .consumerMainThread(PlayerExNet.SendToClient::handle).add();
    }
    public static <MSG> void sendToServer(MSG msg){
        INSTANCE.sendToServer(msg);
    }
    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer serverPlayer){
        INSTANCE.send(PacketDistributor.PLAYER.with(()->serverPlayer), msg);
    }
}
