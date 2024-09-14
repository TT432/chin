package io.github.tt432.chin.rpc.impl;

import io.github.tt432.chin.Chin;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;

/**
 * @author TT432
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class RpcNetworkHandler {
    @SubscribeEvent
    public static void onEvent(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0").optional();

        registrar.playBidirectional(RpcNetworkPacket.TYPE, RpcNetworkPacket.STREAM_CODEC, (pkt, ctx) -> {
            Object object = pkt.sourceType().objectGetter().getObject(ctx, pkt.sourceId());
            if (object != null) RpcManager.invoke(object, pkt.methodId());
        });

        registrar.configurationToClient(RpcMethodIdMapPacket.TYPE, RpcMethodIdMapPacket.STREAM_CODEC,
                (pkt, ctx) -> RpcManager.handleConfigTask(pkt));
    }

    @SubscribeEvent
    public static void onEvent(final RegisterConfigurationTasksEvent event) {
        event.register(new ICustomConfigurationTask() {
            public static final ConfigurationTask.Type TYPE =
                    new ConfigurationTask.Type(ResourceLocation.fromNamespaceAndPath(Chin.MOD_ID, "update_rpc_id_map"));

            @Override
            public void run(Consumer<CustomPacketPayload> sender) {
                RpcManager.acceptConfigTask(sender);
                event.getListener().finishCurrentTask(this.type());
            }

            @Override
            public Type type() {
                return TYPE;
            }
        });
    }
}
