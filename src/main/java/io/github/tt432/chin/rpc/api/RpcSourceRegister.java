package io.github.tt432.chin.rpc.api;

import io.github.tt432.chin.Chin;
import io.github.tt432.chin.util.ChinNetworks;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * @author TT432
 */
@UtilityClass
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class RpcSourceRegister {
    public final ResourceKey<Registry<RpcSourceType<?>>> RPC_SOURCE_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Chin.MOD_ID, "rpc_source_type"));
    public final Registry<RpcSourceType<?>> REGISTRY = new RegistryBuilder<>(RPC_SOURCE_TYPE_KEY).sync(true).create();

    public final DeferredRegister<RpcSourceType<?>> CHIN_DEFERRED_REGISTER = DeferredRegister.create(RPC_SOURCE_TYPE_KEY, Chin.MOD_ID);

    public final DeferredHolder<RpcSourceType<?>, RpcSourceType<BlockEntity>> BLOCK_ENTITY =
            CHIN_DEFERRED_REGISTER.register("block_entity", () -> new RpcSourceType<>(
                    BlockEntity.class,
                    be -> be.getBlockPos().asLong(),
                    (ctx, id) -> {
                        BlockPos pos = BlockPos.of(id);
                        Level level = ChinNetworks.getLevel(ctx);

                        if (level.isLoaded(pos)) {
                            return level.getBlockEntity(pos);
                        }

                        return null;
                    }
            ));

    @SubscribeEvent
    public void onEvent(NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}
