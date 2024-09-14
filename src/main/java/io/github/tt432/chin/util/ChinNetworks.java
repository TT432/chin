package io.github.tt432.chin.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.Environment;

/**
 * @author TT432
 */
@UtilityClass
public class ChinNetworks {
    public boolean isClientSide() {
        if (ServerLifecycleHooks.getCurrentServer() != null
                && ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
            return Thread.currentThread().getThreadGroup().getName().equals("main");
        } else {
            return Environment.get().getDist().isClient();
        }
    }

    public Level getLevel(IPayloadContext context) {
        if (isClientSide()) {
            return (Level) (Object) Minecraft.getInstance().level;
        } else {
            return context.player().level();
        }
    }
}
