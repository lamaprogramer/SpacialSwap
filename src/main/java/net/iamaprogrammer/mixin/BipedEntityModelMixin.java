package net.iamaprogrammer.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowPosing.class)
public abstract class BipedEntityModelMixin {
    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private static void t(ModelPart arm, float animationProgress, float sigma, CallbackInfo ci) {
        ci.cancel();
    }
}
