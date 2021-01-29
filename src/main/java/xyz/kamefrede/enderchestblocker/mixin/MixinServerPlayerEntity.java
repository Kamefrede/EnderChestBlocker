package xyz.kamefrede.enderchestblocker.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

import java.util.OptionalInt;
import xyz.kamefrede.enderchestblocker.Config;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends MixinEntity {

	@Inject(method = "openContainer(Lnet/minecraft/inventory/container/INamedContainerProvider;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/inventory/container/INamedContainerProvider;createMenu(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/inventory/container/Container;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void mixinOpenContainer(@Nullable INamedContainerProvider containerProvider, CallbackInfoReturnable<OptionalInt> ci, Container container) {
		Object self = (Object) this;
		if (container != null && Config.isDimensionDisallowed(this.getEntityWorld().getDimensionKey())) {
			if (container instanceof ChestContainer) {
				ChestContainer chestContainer = (ChestContainer) container;
				if (chestContainer.getLowerChestInventory() instanceof EnderChestInventory) {
					//lol
					container.onContainerClosed((ServerPlayerEntity) self);
					ci.setReturnValue(OptionalInt.empty());
				}
			}
		}
	}

}
