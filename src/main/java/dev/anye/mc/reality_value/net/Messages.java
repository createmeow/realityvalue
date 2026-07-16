package dev.anye.mc.reality_value.net;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.ClientPlayerExData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Messages {

    public record ExDataPayload(int health, int sanity, int thirst) implements CustomPacketPayload {
        public static final Type<ExDataPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "ex_data"));

        public static final StreamCodec<ByteBuf, ExDataPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ExDataPayload::health,
                ByteBufCodecs.VAR_INT, ExDataPayload::sanity,
                ByteBufCodecs.VAR_INT, ExDataPayload::thirst,
                ExDataPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG msg, ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, msg);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG msg) {
        PacketDistributor.sendToServer(msg);
    }

    public static void register(FMLCommonSetupEvent event) {
    }
}