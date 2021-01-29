package xyz.kamefrede.enderchestblocker.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import xyz.kamefrede.enderchestblocker.Config;

@Mixin(EnderChestBlock.class)
public class MixinEnderChest {

	@Inject(method = "onBlockActivated(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;openContainer(Lnet/minecraft/inventory/container/INamedContainerProvider;)Ljava/util/OptionalInt;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void mixinEnderChestBlock(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit, CallbackInfoReturnable<ActionResultType> cir, EnderChestInventory inventory, TileEntity entity) {
		if (Config.isDimensionDisallowed(player.getEntityWorld().getDimensionKey())) {
			//Sometimes the chest is open, it kinda annoys me so here
			EnderChestTileEntity enderChestTileEntity = (EnderChestTileEntity) entity;
			enderChestTileEntity.closeChest();
		}
	}
}
