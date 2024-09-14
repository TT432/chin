package io.github.tt432.chin;

import io.github.tt432.chin.rpc.api.RpcSourceRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * @author TT432
 */
@Mod(Chin.MOD_ID)
public class Chin {
    public static final String MOD_ID = "chin";

    public Chin(IEventBus bus) {
        RpcSourceRegister.CHIN_DEFERRED_REGISTER.register(bus);
    }
}
