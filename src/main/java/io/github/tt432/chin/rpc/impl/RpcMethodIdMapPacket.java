package io.github.tt432.chin.rpc.impl;

import io.github.tt432.chin.Chin;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TT432
 */
public record RpcMethodIdMapPacket(
        Map<String, Long> map
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RpcMethodIdMapPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Chin.MOD_ID, "rpc_method_id_map"));

    public static final StreamCodec<ByteBuf, RpcMethodIdMapPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.VAR_LONG
            ),
            RpcMethodIdMapPacket::map,
            RpcMethodIdMapPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}