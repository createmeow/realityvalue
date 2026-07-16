package dev.ghen.thirst.foundation.common.event;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.content.purity.ContainerWithPurity;
import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/common/event/RegisterThirstValueEvent.class */
public class RegisterThirstValueEvent extends Event {
    public void addFood(Item item, int thirst, int quenched) {
        ThirstHelper.VALID_FOODS.putIfAbsent(item, new Number[]{Integer.valueOf(thirst), Integer.valueOf(quenched)});
    }

    public void addDrink(Item item, int thirst, int quenched) {
        ThirstHelper.VALID_DRINKS.putIfAbsent(item, new Number[]{Integer.valueOf(thirst), Integer.valueOf(quenched)});
    }

    public void addContainer(ContainerWithPurity container) {
        WaterPurity.addContainer(container);
    }

    public void addContainer(Item item) {
        WaterPurity.addContainer(new ContainerWithPurity(new ItemStack(item)));
    }

    public boolean isCancelable() {
        return false;
    }
}
