package io.github.tt432.chin.rpc.impl;

import io.github.tt432.chin.rpc.api.Rpc;
import io.github.tt432.chin.util.ChinNetworks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author TT432
 */
@Aspect
public class RpcAspect {
    @Pointcut("@annotation(rpc) && args() && target(target)")
    public void rpcPointcut(Rpc rpc, Object target) {
    }

    @Around("rpcPointcut(rpc, target)")
    public Object processRpc(Rpc rpc, Object target, ProceedingJoinPoint joinPoint) throws Throwable {
        if (rpc.value().isClient() == ChinNetworks.isClientSide()) {
            RpcNetworkPacket<Object> packet = RpcNetworkPacket.of(
                    target,
                    target.getClass().getName() + "#" + joinPoint.getSignature().getName()
            );

            if (packet != null) {
                if (rpc.value().isClient()) {
                    PacketDistributor.sendToServer(packet);
                } else {
                    PacketDistributor.sendToAllPlayers(packet);
                }
            }

            return null;
        } else {
            return joinPoint.proceed();
        }
    }
}
