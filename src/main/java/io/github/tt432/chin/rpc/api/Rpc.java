package io.github.tt432.chin.rpc.api;

import io.github.tt432.chin.rpc.impl.RpcAspect;
import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * example:
 * <pre>{@code
 * class MyBlockEntity extends BlockEntity {
 *     @Rpc(CLIENT)
 *     public void clickClick() {
 *         // do something
 *     }
 * }
 * }</pre>
 *
 * the method will send RpcNetworkPacket to server. equals:
 *
 * <pre>{@code
 * class MyBlockEntity extends BlockEntity {
 *     public void clickClick() {
 *         if (isClientSide()) {
 *             // send packet
 *         } else {
 *             // do something
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author TT432
 * @see RpcAspect
 * @see RpcSourceRegister
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rpc {
    /**
     * @return sender side
     */
    Dist value();
}
