package net.iamaprogrammer.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SpacialSwapPacket(boolean active) implements CustomPayload {
    public static final CustomPayload.Id<SpacialSwapPacket> ID = new CustomPayload.Id<>(SpacialSwapNetworkingConstants.SPACIAL_SWAP_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SpacialSwapPacket> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, SpacialSwapPacket::active, SpacialSwapPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
