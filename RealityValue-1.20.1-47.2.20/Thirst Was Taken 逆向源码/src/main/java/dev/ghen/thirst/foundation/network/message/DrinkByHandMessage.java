package dev.ghen.thirst.foundation.network.message;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.config.CommonConfig;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/network/message/DrinkByHandMessage.class */
public class DrinkByHandMessage {
    public BlockPos pos;

    public DrinkByHandMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(DrinkByHandMessage message, FriendlyByteBuf buffer) {
        buffer.m_130064_(message.pos);
    }

    public static DrinkByHandMessage decode(FriendlyByteBuf buffer) {
        return new DrinkByHandMessage(buffer.m_130135_());
    }

    public static void handle(DrinkByHandMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                Level level = sender.m_9236_();
                sender.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(cap -> {
                    if (cap.getThirst() == 20) {
                        return;
                    }
                    int purity = WaterPurity.getBlockPurity(level, message.pos);
                    level.m_6263_(sender, sender.m_20185_(), sender.m_20186_(), sender.m_20189_(), SoundEvents.f_11911_, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    if (WaterPurity.givePurityEffects(sender, purity)) {
                        cap.drink(sender, ((Number) CommonConfig.HAND_DRINKING_HYDRATION.get()).intValue(), ((Number) CommonConfig.HAND_DRINKING_QUENCHED.get()).intValue());
                    }
                });
            });
        }
        context.setPacketHandled(true);
    }
}
