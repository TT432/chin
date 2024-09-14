package io.github.tt432.chin.rpc.api;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;

/**
 * @author TT432
 */
public record RpcSourceType<T>(
        Class<T> clazz,
        IdGetter<T> idGetter,
        ObjectGetter<T> objectGetter
) {
    @FunctionalInterface
    public interface IdGetter<T> {
        long getId(T object);
    }

    @FunctionalInterface
    public interface ObjectGetter<T> {
        @Nullable
        T getObject(IPayloadContext context, long id);
    }
}
