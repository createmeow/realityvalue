package dev.ghen.thirst.foundation.mixin.brewinandchewin;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.mixin.accessors.brewinandchewin.KegBlockEntityAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.KegRecipeWrapper;

@Mixin(value = {KegBlockEntity.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/brewinandchewin/MixinKegBlockEntity.class */
public abstract class MixinKegBlockEntity {

    @Shadow
    private ResourceLocation lastRecipeID;

    @Shadow
    private boolean checkNewRecipe;

    @Shadow
    private int fermentTime;

    @Shadow
    @Final
    private KegRecipeWrapper recipeWrapper;

    @Inject(method = {"processFermenting"}, at = {@At(value = "INVOKE", target = "Lumpaz/brewinandchewin/common/crafting/KegFermentingRecipe;getResultFluid()Lnet/minecraft/world/level/material/Fluid;", ordinal = WaterPurity.MIN_PURITY)}, cancellable = true)
    private void processFermentingWithPurity(KegFermentingRecipe recipe, KegBlockEntity keg, CallbackInfoReturnable<Boolean> cir) {
        if (recipe.getResultFluid() != null) {
            int purity = WaterPurity.getPurity(keg.getFluidTank().getFluid());
            keg.getFluidTank().setFluid(WaterPurity.addPurity(new FluidStack(recipe.getResultFluid(), keg.getFluidTank().getFluidAmount()), purity < ((Number) CommonConfig.FERMENTATION_MOLDING_THRESHOLD.get()).intValue() ? Math.max(purity - ((Number) CommonConfig.FERMENTATION_MOLDING_HARSHNESS.get()).intValue(), 0) : purity));
            if (keg.m_58904_().m_5776_()) {
                keg.m_58904_().m_245747_(keg.m_58899_(), SoundEvents.f_11772_, SoundSource.BLOCKS, 1.0f, 0.8f, true);
            }
        }
        int purity_output = 0;
        if (recipe.getResultItem() != null) {
            if (recipe.getFluidIngredient() != null) {
                purity_output = WaterPurity.getPurity(keg.getFluidTank().getFluid());
                keg.getFluidTank().drain(recipe.getFluidIngredient().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            }
            ItemStack output = new ItemStack(recipe.getResultItem(), recipe.getAmount());
            if (WaterPurity.isWaterFilledContainer(output)) {
                WaterPurity.addPurity(output, purity_output);
            }
            keg.getInventory().insertItem(5, output, false);
        }
        for (int i = 0; i < 5; i++) {
            ItemStack slotStack = keg.getInventory().getStackInSlot(i);
            if (slotStack.hasCraftingRemainingItem()) {
                ((KegBlockEntityAccessor) keg).invokeEjectIngredientRemainder(slotStack.getCraftingRemainingItem());
            }
            if (!slotStack.m_41619_()) {
                slotStack.m_41774_(1);
            }
        }
        cir.setReturnValue(true);
    }

    @Overwrite
    private Optional<KegFermentingRecipe> getMatchingRecipe(KegRecipeWrapper inventoryWrapper) {
        if (((KegBlockEntity) this).m_58904_() == null) {
            return Optional.empty();
        }
        if (this.lastRecipeID != null) {
            KegFermentingRecipe kegFermentingRecipe = (Recipe) ((KegBlockEntity) this).m_58904_().m_7465_().getRecipeMap((RecipeType) BnCRecipeTypes.FERMENTING.get()).get(this.lastRecipeID);
            if ((kegFermentingRecipe instanceof KegFermentingRecipe) && kegFermentingRecipe.m_5818_(inventoryWrapper, ((KegBlockEntity) this).m_58904_())) {
                return Optional.of(kegFermentingRecipe);
            }
        }
        if (this.checkNewRecipe) {
            FluidStack stack = ((KegBlockEntity) this).getFluidTank().getFluid().copy();
            stack.removeChildTag("Purity");
            Optional<KegFermentingRecipe> recipe = ((KegBlockEntity) this).m_58904_().m_7465_().m_44013_((RecipeType) BnCRecipeTypes.FERMENTING.get()).stream().filter(a -> {
                return a.matches(inventoryWrapper, ((KegBlockEntity) this).m_58904_()) && (a.getFluidIngredient() == null || WaterPurity.matchRecipe(a.getFluidIngredient(), stack));
            }).findFirst();
            if (recipe.isPresent()) {
                ResourceLocation newRecipeID = recipe.get().m_6423_();
                if (this.lastRecipeID != null && !this.lastRecipeID.equals(newRecipeID)) {
                    this.fermentTime = 0;
                }
                this.lastRecipeID = newRecipeID;
                return recipe;
            }
        }
        this.checkNewRecipe = false;
        return Optional.empty();
    }

    @Overwrite
    private List<ItemStack> fluidExtract(ItemStack slotIn, int maxTakeAmount, boolean inGui, boolean isCreative) {
        KegBlockEntity keg = (KegBlockEntity) this;
        if (slotIn.m_41619_()) {
            return List.of();
        }
        Optional<KegPouringRecipe> recipe = keg.getPouringRecipe(slotIn);
        boolean changed = false;
        List<ItemStack> outputs = new ArrayList<>();
        if (recipe.isPresent() && (keg.getFluidTank().isEmpty() || keg.getFluidTank().getFluid().getFluid() == recipe.get().getRawFluid())) {
            ItemStack resultItem = recipe.get().assemble(this.recipeWrapper, keg.m_58904_().m_9598_());
            if (!ItemStack.m_41656_(slotIn, recipe.get().getContainer()) || recipe.get().getAmount() > keg.getFluidTank().getFluidAmount() || (inGui && !keg.getInventory().getStackInSlot(5).m_41619_() && !ItemStack.m_150942_(resultItem, keg.getInventory().getStackInSlot(5)))) {
                if (recipe.filter((v0) -> {
                    return v0.canFill();
                }).isPresent() && (((recipe.get().isStrict() && ItemStack.m_150942_(resultItem, slotIn)) || (!recipe.get().isStrict() && ItemStack.m_41656_(slotIn, resultItem))) && ((keg.getFluidTank().isEmpty() || keg.getFluidTank().getFluidAmount() < keg.getFluidTank().getCapacity()) && (!inGui || keg.getInventory().getStackInSlot(5).m_41619_() || ItemStack.m_150942_(recipe.get().getContainer(), keg.getInventory().getStackInSlot(5)))))) {
                    int containerAmount = Mth.m_14045_(Math.min(slotIn.m_41613_(), keg.getFluidTank().getCapacity() / recipe.get().getAmount()), 1, maxTakeAmount);
                    keg.getFluidTank().fill(WaterPurity.addPurity(new FluidStack(recipe.get().getFluid(slotIn), recipe.get().getAmount() * containerAmount), WaterPurity.getPurity(slotIn)), IFluidHandler.FluidAction.EXECUTE);
                    if (!isCreative) {
                        ItemStack recipeItem = recipe.get().getContainer(slotIn);
                        int amountToDrain = containerAmount;
                        while (amountToDrain > 0 && !slotIn.m_41619_()) {
                            ItemStack newResult = recipeItem.m_255036_(Math.min(recipeItem.m_41741_(), amountToDrain));
                            outputs.add(newResult);
                            amountToDrain -= newResult.m_41613_();
                            slotIn.m_41774_(newResult.m_41613_());
                        }
                        if (!slotIn.m_41619_()) {
                            outputs.add(slotIn);
                        }
                    } else {
                        outputs.add(slotIn);
                    }
                    changed = true;
                }
            } else {
                int purity = keg.getFluidTank().isEmpty() ? ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue() : WaterPurity.getPurity(keg.getFluidTank().getFluid());
                int containerAmount2 = Mth.m_14045_(Math.min(slotIn.m_41613_(), maxTakeAmount), 1, keg.getFluidTank().getCapacity() / recipe.get().getAmount());
                keg.getFluidTank().drain(new FluidStack(keg.getFluidTank().getFluid(), recipe.get().getAmount() * containerAmount2), IFluidHandler.FluidAction.EXECUTE);
                if (!isCreative) {
                    int overflow = containerAmount2;
                    if (WaterPurity.isWaterFilledContainer(resultItem)) {
                        WaterPurity.addPurity(resultItem, purity);
                    }
                    while (overflow > 0 && !slotIn.m_41619_()) {
                        ItemStack newResult2 = resultItem.m_255036_(Math.min(resultItem.m_41741_(), overflow));
                        outputs.add(newResult2);
                        overflow -= newResult2.m_41613_();
                        slotIn.m_41774_(newResult2.m_41613_());
                    }
                    if (!slotIn.m_41619_()) {
                        outputs.add(slotIn);
                    }
                } else {
                    outputs.add(slotIn);
                }
                changed = true;
            }
            if (changed) {
                keg.m_6596_();
            }
        }
        if (outputs.isEmpty() && recipe.isEmpty()) {
            LazyOptional<IFluidHandlerItem> fluidHandler = isCreative ? slotIn.m_41777_().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM) : slotIn.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            IFluidHandlerItem iFluidItemHandler = (IFluidHandlerItem) fluidHandler.orElse((Object) null);
            if (fluidHandler.isPresent() && !slotIn.m_41619_()) {
                if (keg.getFluidTank().getFluid().isFluidEqual(iFluidItemHandler.getFluidInTank(0)) || (keg.getFluidTank().getFluid().isEmpty() && ((!inGui || keg.getInventory().getStackInSlot(5).m_41619_() || keg.getInventory().getStackInSlot(5).m_150930_(iFluidItemHandler.getContainer().m_41720_())) && keg.m_58904_().m_7465_().m_44013_((RecipeType) BnCRecipeTypes.KEG_POURING.get()).stream().anyMatch(pouringRecipe -> {
                    return ((KegPouringRecipe) pouringRecipe).getFluid(slotIn).isFluidEqual(iFluidItemHandler.getFluidInTank(0));
                })))) {
                    int amountToDrain2 = keg.getFluidTank().getCapacity() - keg.getFluidTank().getFluidAmount();
                    int amount = keg.getFluidTank().fill(iFluidItemHandler.drain(amountToDrain2, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE);
                    if (amount <= amountToDrain2 && amount > 0) {
                        keg.getFluidTank().fill(iFluidItemHandler.drain(amountToDrain2, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        if (!isCreative) {
                            ItemStack recipeItem2 = slotIn.getCraftingRemainingItem().m_41619_() ? iFluidItemHandler.getContainer() : slotIn.getCraftingRemainingItem();
                            int overflow2 = amount / keg.getFluidTank().getCapacity();
                            while (overflow2 > 0 && !slotIn.m_41619_()) {
                                ItemStack newResult3 = recipeItem2.m_255036_(Math.min(recipeItem2.m_41741_(), overflow2));
                                outputs.add(newResult3);
                                overflow2 -= newResult3.m_41613_();
                                slotIn.m_41774_(newResult3.m_41613_());
                            }
                        } else {
                            outputs.add(slotIn);
                        }
                        keg.m_6596_();
                    }
                } else if (!keg.getFluidTank().getFluid().isEmpty() && iFluidItemHandler.isFluidValid(0, keg.getFluidTank().getFluid()) && (!inGui || keg.getInventory().getStackInSlot(5).m_41619_() || keg.getInventory().getStackInSlot(5).m_150930_(iFluidItemHandler.getContainer().m_41720_()))) {
                    int amountToDrain3 = iFluidItemHandler.getTankCapacity(0);
                    IFluidHandlerItem iFluidItemHandler2 = (IFluidHandlerItem) slotIn.m_255036_(amountToDrain3 / iFluidItemHandler.getTankCapacity(0)).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse((Object) null);
                    int amount2 = iFluidItemHandler2.fill(keg.getFluidTank().drain(amountToDrain3, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE);
                    if (amount2 > 0) {
                        iFluidItemHandler2.fill(keg.getFluidTank().drain(amountToDrain3, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        if (amount2 <= amountToDrain3) {
                            outputs.add(slotIn);
                            keg.m_6596_();
                        }
                    }
                }
            }
            return outputs;
        }
        return outputs;
    }
}
