package net.iamaprogrammer.mixin;

import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Unique
    private final PlayerInventory THIS = (PlayerInventory)(Object)this;

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void t(BlockState block, CallbackInfoReturnable<Float> cir) {
        float highestMultiplier = cir.getReturnValue();

        if (((SpacialSwapPlayerAccess) THIS.player).isSwapActive()) {
            for (int i = 0; i < THIS.main.size(); ++i) {
                ItemStack stack = THIS.main.get(i);
                if (!stack.isEmpty()) {
                    float mult = stack.getMiningSpeedMultiplier(block);
                    if (mult > highestMultiplier) {
                        highestMultiplier = mult;
                    }
                }
            }
        }

        cir.setReturnValue(highestMultiplier);
    }
}
