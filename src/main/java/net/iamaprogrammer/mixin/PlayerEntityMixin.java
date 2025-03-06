package net.iamaprogrammer.mixin;

import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements SpacialSwapPlayerAccess {
    @Unique
    private boolean spacialSwapActive;

    @Override
    public boolean isSwapActive() {
        return this.spacialSwapActive;
    }

    @Override
    public void setSwapActive(boolean active) {
        //System.out.println(active);
        this.spacialSwapActive = active;
    }
}
