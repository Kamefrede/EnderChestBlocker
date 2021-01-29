package xyz.kamefrede.enderchestblocker;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class EnderChestBlockerCommand {
	public static final SimpleCommandExceptionType NO_DISALLOWED_DIMENSIONS = new SimpleCommandExceptionType(new TranslationTextComponent("enderchestblocker.empty_disallowed_list"));
	public static final DynamicCommandExceptionType DIMENSION_NOT_PRESENT_IN_DISALLOWED_LIST = new DynamicCommandExceptionType((dimension) -> new TranslationTextComponent("enderchestblocker.dimension_not_present", dimension));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> commandBuilder = Commands.literal("enderchestblocker")
				.requires(source -> source.hasPermissionLevel(2))
				.then(Commands.literal("add")
						.then(Commands.argument("dimension", DimensionArgument.getDimension())
								.suggests((context, builder) -> {
									if (Config.disallowedDimensions.isEmpty()) {
										return DimensionArgument.getDimension().listSuggestions(context, builder);
									}
									return ISuggestionProvider.func_212476_a(context.getSource().func_230390_p_().stream().filter(e -> !Config.isDimensionDisallowed(e)).map(RegistryKey::getLocation), builder);
								}).executes(context -> {
									RegistryKey<World> dimension = DimensionArgument.getDimensionArgument(context, "dimension").getDimensionKey();
									if (!Config.isDimensionDisallowed(dimension)) {
										Config.addDimensionToDisallowed(dimension);
										Config.updateConfig();
									}
									context.getSource().sendFeedback(new TranslationTextComponent("enderchestblocker.added_dimension", dimension.getLocation().toString()), true);
									return Command.SINGLE_SUCCESS;
								})))
				.then(Commands.literal("remove")
						.then(Commands.argument("dimension", DimensionArgument.getDimension()).suggests((context, builder) -> {
							if (Config.disallowedDimensions.isEmpty()) {
								throw NO_DISALLOWED_DIMENSIONS.create();
							}
							return ISuggestionProvider.func_212476_a(Config.disallowedDimensions.stream().map(RegistryKey::getLocation), builder);
						})
								.executes(context -> {
									RegistryKey<World> dimension = DimensionArgument.getDimensionArgument(context, "dimension").getDimensionKey();
									if (!Config.isDimensionDisallowed(dimension)) {
										throw DIMENSION_NOT_PRESENT_IN_DISALLOWED_LIST.create(dimension.getLocation());
									}
									Config.removeDimensiomFromDisallowed(dimension);
									Config.updateConfig();
									context.getSource().sendFeedback(new TranslationTextComponent("enderchestblocker.removed_dimension", dimension.getLocation().toString()), true);
									return Command.SINGLE_SUCCESS;
								})))
				.then(Commands.literal("get")
						.executes(context -> {
							if (Config.disallowedDimensions.isEmpty()) {
								throw NO_DISALLOWED_DIMENSIONS.create();
							} else {
								StringBuilder sb = new StringBuilder();
								Config.disallowedDimensions.stream().map(dim -> dim.getLocation().toString()).forEach(consumer -> sb.append(consumer).append("; "));
								context.getSource().sendFeedback(new TranslationTextComponent("enderchestblocker.disallow_dimensions_print", sb), true);
							}
							return Command.SINGLE_SUCCESS;
						}));
		LiteralCommandNode<CommandSource> command = dispatcher.register(commandBuilder);
	}
}
