package xyz.kamefrede.enderchestblocker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EnderChestBlocker.MODID)
public class EnderChestBlocker {
    public static final String MODID = "enderchestblocker";
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public EnderChestBlocker() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        EnderChestBlockerCommand.register(event.getDispatcher());
    }


}
