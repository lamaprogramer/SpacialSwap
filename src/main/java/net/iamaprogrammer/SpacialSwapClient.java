package net.iamaprogrammer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.iamaprogrammer.networking.SpacialSwapPacket;
import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class SpacialSwapClient implements ClientModInitializer {
    private static boolean wasKeyPressed = false;

    @Override
    public void onInitializeClient() {
        SpacialSwapKeybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            boolean isCurrentlyPressed = SpacialSwapKeybinds.SPACIAL_SWAP.isPressed();

            if (isCurrentlyPressed && !wasKeyPressed) { // On Key Down
                SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) client.player;
                if (client.player != null && this.canSpacialSwap(client)) {
                    this.setSpacialSwap(access, true);
                }
            } else if (!isCurrentlyPressed && wasKeyPressed) { // On Key Up
                SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) client.player;
                this.setSpacialSwap(access, false);
            }

            if (isCurrentlyPressed && wasKeyPressed) {
                if (client.player != null) {
                    if (this.canSpacialSwap(client)) { // If key is down, player is holding block, and spacial swap is not active.
                        SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) client.player;
                        if (!access.isSwapActive()) {
                            this.setSpacialSwap(access, true);
                        }
                    } else { // If key is down, and player not holding block
                        SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) client.player;
                        if (access.isSwapActive()) {
                            this.setSpacialSwap(access, false);
                        }
                    }
                }
            }

            wasKeyPressed = isCurrentlyPressed;
        });
    }

    private void setSpacialSwap(SpacialSwapPlayerAccess access, boolean value) {
        if (access != null) {
            access.setSwapActive(value);
            ClientPlayNetworking.send(new SpacialSwapPacket(value));
        }
    }

    private boolean canSpacialSwap(MinecraftClient client) {
        ItemStack stack = client.player.getMainHandStack();
        BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();

//        client.player.sendMessage(Text.of(
//                stack.getName().getString() +
//                        " at position " +
//                        pos.toString() +
//                        ": " +
//                        this.canPlace(client.world, ((BlockItem)stack.getItem()).getBlock(), pos)
//                        //stack.canPlaceOn(new CachedBlockPosition(client.world, pos, false))
//                        ), true);
        return stack.getItem() instanceof BlockItem &&
                this.canPlace(client.world, ((BlockItem)stack.getItem()).getBlock(), pos);

    }

    private boolean canPlace(WorldAccess world, Block block, BlockPos pos) {
        boolean result = false;
        for (BlockState state : block.getStateManager().getStates()) {
            if (state.canPlaceAt(world, pos)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
