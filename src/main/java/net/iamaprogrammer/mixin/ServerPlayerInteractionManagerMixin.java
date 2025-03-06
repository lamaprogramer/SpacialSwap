package net.iamaprogrammer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @ModifyVariable(
            method = "tryBreakBlock",
            at = @At("STORE"),
            ordinal = 0
    )
    private ItemStack t(ItemStack value, @Local BlockState blockState) {
        if (!((SpacialSwapPlayerAccess) this.player).isSwapActive()) {
            return value;
        }

        float highestMultiplier = 1.0F;
        ItemStack targetStack = value;
        PlayerInventory inventory = this.player.getInventory();

        if (((SpacialSwapPlayerAccess) this.player).isSwapActive()) {
            for (int i = 0; i < inventory.main.size(); ++i) {
                ItemStack stack = inventory.main.get(i);
                if (!stack.isEmpty()) {
                    float mult = stack.getMiningSpeedMultiplier(blockState);
                    if (mult > highestMultiplier) {
                        highestMultiplier = mult;
                        targetStack = stack;
                    }
                }
            }
        }

        return targetStack;
    }
}
