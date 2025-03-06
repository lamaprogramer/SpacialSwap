package net.iamaprogrammer.mixin;

import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract ActionResult interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);


    @Inject(
        method = "attackBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
        )
    )
    private void handleCreativeSwap(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        this.handleSwap(pos);
    }


    @Inject(
        method = "updateBlockBreakingProgress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
            shift = At.Shift.AFTER
            //ordinal = 1
        )
    )
    private void handleSurvivalSwap(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        this.handleSwap(pos);
    }

    @Unique
    private void handleSwap(BlockPos pos) {
        SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) this.client.player;
        if (access != null && access.isSwapActive()) {
            Vec3d lookDirection = this.client.player.getRotationVec(1.0F);
            BlockPos blockBehindPos = pos.add(
                    (int) -lookDirection.x,
                        0,
                    (int) -lookDirection.z
            );

            Direction blockSide = ((BlockHitResult)findCrosshairTarget(
                    this.client.getCameraEntity(),
                    this.client.player.getBlockInteractionRange(),
                    this.client.getRenderTickCounter().getTickDelta(false)
            )).getSide();

            BlockHitResult hitResult = new BlockHitResult(
                    Vec3d.ofCenter(blockBehindPos),
                    blockSide,
                    blockBehindPos,
                    false
            );

            this.interactBlock(this.client.player, Hand.MAIN_HAND, hitResult);
        }
    }

    @Unique
    private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, float tickDelta) {
        Vec3d vec3d = camera.getCameraPosVec(tickDelta);
        HitResult hitResult = camera.raycast(blockInteractionRange, tickDelta, false);

        return ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
    }

    @Unique
    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }
}
