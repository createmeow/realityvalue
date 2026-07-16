package dev.ghen.thirst.foundation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/util/TickHelper.class */
public class TickHelper {
    private static final Map<Integer, List<Runnable>> tickTasks = new HashMap();
    private static int tickTimerFsr = 0;

    public static void addTask(int tick, Runnable task) {
        if (!tickTasks.containsKey(Integer.valueOf(tick))) {
            tickTasks.put(Integer.valueOf(tick), new ArrayList());
        }
        tickTasks.get(Integer.valueOf(tick)).add(task);
    }

    public static void nextTick(Level level, Runnable task) {
        addTask(((MinecraftServer) Objects.requireNonNull(level.m_7654_())).m_129921_() + 1, task);
    }

    public static void TickLater(Level level, int tickNumber, Runnable task) {
        addTask(((MinecraftServer) Objects.requireNonNull(level.m_7654_())).m_129921_() + tickNumber, task);
    }

    @SubscribeEvent
    static void runTasks(TickEvent.LevelTickEvent event) {
        if ((event.level instanceof ServerLevel) && tickTimerFsr == 0 && tickTasks.containsKey(Integer.valueOf(event.level.m_7654_().m_129921_()))) {
            tickTasks.get(Integer.valueOf(event.level.m_7654_().m_129921_())).forEach((v0) -> {
                v0.run();
            });
            tickTasks.remove(Integer.valueOf(event.level.m_7654_().m_129921_()));
            tickTimerFsr += 3;
        } else if (tickTimerFsr > 0) {
            tickTimerFsr--;
        }
    }
}
