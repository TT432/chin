package io.github.tt432.chin.rpc.impl;

import io.github.tt432.chin.Chin;
import io.github.tt432.chin.rpc.api.RpcSourceRegister;
import io.github.tt432.chin.rpc.api.RpcSourceType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author TT432
 */
public record RpcNetworkPacket<T>(
        RpcSourceType<T> sourceType,
        long sourceId,
        long methodId
) implements CustomPacketPayload {
    @SuppressWarnings("unchecked")
    public static <T> RpcNetworkPacket<T> of(Object o, String method) {
        RpcSourceType<T> type = RpcManager.getType(o);

        if (type == null) return null;

        return new RpcNetworkPacket<>(
                type,
                type.idGetter().getId((T) o),
                RpcManager.getMethodId(method)
        );
    }

    public static final Type<RpcNetworkPacket<?>> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Chin.MOD_ID, "rpc_network"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RpcNetworkPacket<?>> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RpcSourceRegister.RPC_SOURCE_TYPE_KEY),
            RpcNetworkPacket::sourceType,
            ByteBufCodecs.VAR_LONG,
            RpcNetworkPacket::sourceId,
            ByteBufCodecs.VAR_LONG,
            RpcNetworkPacket::methodId,
            RpcNetworkPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
