package net.iamaprogrammer;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.iamaprogrammer.networking.SpacialSwapPacket;
import net.iamaprogrammer.util.SpacialSwapPlayerAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpacialSwap implements ModInitializer {
	public static final String MOD_ID = "spacial-swap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(SpacialSwapPacket.ID, SpacialSwapPacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SpacialSwapPacket.ID, (SpacialSwapPacket packet, ServerPlayNetworking.Context context) -> {
			SpacialSwapPlayerAccess access = (SpacialSwapPlayerAccess) context.player();
			access.setSwapActive(packet.active());
        });
		LOGGER.info("Hello Fabric world!");
	}
}