package io.github.tt432.chin.rpc.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.tt432.chin.rpc.api.Rpc;
import io.github.tt432.chin.rpc.api.RpcSourceRegister;
import io.github.tt432.chin.rpc.api.RpcSourceType;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author TT432
 */
@UtilityClass
public class RpcManager {
    private final Object2LongMap<String> idToMethodMap = new Object2LongOpenHashMap<>();
    private final Long2ObjectMap<MethodHandle> methodToIdMap = new Long2ObjectOpenHashMap<>();

    public void handleConfigTask(RpcMethodIdMapPacket pkt) {
        var newIdToMethodMap = new Object2LongOpenHashMap<String>();
        var newMethodToIdMap = new Long2ObjectOpenHashMap<MethodHandle>();
        newIdToMethodMap.putAll(pkt.map());

        for (var objectEntry : newIdToMethodMap.object2LongEntrySet()) {
            if (idToMethodMap.containsKey(objectEntry.getKey())) {
                newMethodToIdMap.put(objectEntry.getLongValue(), methodToIdMap.get(idToMethodMap.getLong(objectEntry.getKey())));
            }
        }

        idToMethodMap.clear();
        methodToIdMap.clear();

        idToMethodMap.putAll(newIdToMethodMap);
        methodToIdMap.putAll(newMethodToIdMap);
    }

    public void acceptConfigTask(Consumer<CustomPacketPayload> sender) {
        sender.accept(new RpcMethodIdMapPacket(idToMethodMap));
    }

    public long getMethodId(String methodName) {
        return idToMethodMap.getOrDefault(methodName, -1L);
    }

    public void invoke(Object o, long method) {
        try {
            methodToIdMap.get(method).invoke(o);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final Cache<Class<?>, RpcSourceType<?>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> RpcSourceType<T> getType(Object object) {
        if (object == null) return null;

        Class<?> oClass = object.getClass();
        RpcSourceType<?> cacheValue = cache.getIfPresent(oClass);

        if (cacheValue != null) {
            return (RpcSourceType<T>) cacheValue;
        } else {
            RpcSourceType<?> newValue = null;

            for (var entry : RpcSourceRegister.REGISTRY.entrySet()) {
                if (entry.getValue().clazz().isInstance(object)) {
                    newValue = entry.getValue();
                }
            }

            if (newValue != null) {
                cache.put(oClass, newValue);
                return (RpcSourceType<T>) newValue;
            }

            return null;
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static final class EventListener {
        @SubscribeEvent
        public static void onEvent(FMLCommonSetupEvent event) {
            final long[] id = {0};
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType voidType = MethodType.methodType(void.class);

            for (ModFileScanData allScanDatum : ModList.get().getAllScanData()) {
                allScanDatum.getAnnotatedBy(Rpc.class, ElementType.METHOD).forEach(data -> {
                    long idIn = id[0]++;
                    String className = data.clazz().getClassName();
                    String methodName = data.memberName().split("\\(\\)")[0];
                    idToMethodMap.put(className + "#" + methodName, idIn);
                    try {
                        methodToIdMap.put(
                                idIn,
                                lookup.findVirtual(
                                        Class.forName(className, false, RpcManager.class.getClassLoader()),
                                        methodName,
                                        voidType
                                )
                        );
                    } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
