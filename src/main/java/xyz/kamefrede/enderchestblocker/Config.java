package xyz.kamefrede.enderchestblocker;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = EnderChestBlocker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final Server SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;
	public static List<RegistryKey<World>> disallowedDimensions;

	static {
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	public static void cacheConfig() {
		List<String> registryKeyStrings = Config.SERVER.disallowedDimensions.get();
		Config.disallowedDimensions = registryKeyStrings.stream().map(string -> {
			ResourceLocation resourceLocation = ResourceLocation.tryCreate(string);
			if (resourceLocation == null) {
				EnderChestBlocker.LOGGER.error("Error loading dimension resource location " + string + " from config");
				return null;
			}
			return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, resourceLocation);
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static void updateConfig() {
		List<String> registryKeyStrings = disallowedDimensions.stream().map(registryKey -> registryKey.getLocation().toString()).collect(Collectors.toList());
		Config.SERVER.disallowedDimensions.set(registryKeyStrings);
	}

	public static boolean isDimensionDisallowed(RegistryKey<World> dimension) {
		return disallowedDimensions.contains(dimension);
	}

	public static void addDimensionToDisallowed(RegistryKey<World> dimension) {
		Config.disallowedDimensions.add(dimension);
		updateConfig();
	}

	public static void removeDimensiomFromDisallowed(RegistryKey<World> dimension) {
		Config.disallowedDimensions.remove(dimension);
		updateConfig();
	}

	@SubscribeEvent
	public static void loadConfig(ModConfig.Loading configEvent) {
		if (configEvent.getConfig().getSpec() == Config.SERVER_SPEC) {
			cacheConfig();
		}
	}

	@SubscribeEvent
	public static void reloadConfig(ModConfig.Reloading configEvent) {
		if (configEvent.getConfig().getSpec() == Config.SERVER_SPEC) {
			cacheConfig();
		}
	}

	public static class Server {
		public final ForgeConfigSpec.ConfigValue<List<String>> disallowedDimensions;

		public Server(ForgeConfigSpec.Builder builder) {
			disallowedDimensions = builder.comment("Controls which dimensions the player can't open their ender chest in")
					.comment("Preferably set this via the ingame command.")
					.define("common.disallowedDimensions", new ArrayList<>());
		}
	}
}
