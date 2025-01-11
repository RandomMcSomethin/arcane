package io.github.randommcsomethin.arcane.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.randommcsomethin.arcane.ArcaneMain;
import io.github.randommcsomethin.arcane.data.EnchantmentPowerProviderType;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
	@Final @Shadow private Property seed;
	@Final @Shadow private Random random;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;setSeed(J)V"), method = "method_17411(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
	private void addMorePower(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) LocalIntRef i) {
		// add enchanting power like in vanilla but with data driven stuff
		Stream<RegistryEntry<EnchantmentPowerProviderType>> s = world.getRegistryManager().getOrThrow(ArcaneMain.CONFIGURED_ENCHANTMENT_POWER_PROVIDER_TYPES).streamEntries().map(reference -> reference);
		// using a float here for more granularity (it'll all make sense later I promise)
		float extraPower = 0;
		// check each power provider type
		for (RegistryEntry<EnchantmentPowerProviderType> powerProviderRef : s.toList()) {
			EnchantmentPowerProviderType powerProvider = powerProviderRef.value();
			// check each block pos
			for(BlockPos blockPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
				// check if block can provide power according to the power provider
				if (powerProvider.canProvidePower(world, pos.add(blockPos)) && ArcaneMain.canReachBlock(world, pos, blockPos)) {
					extraPower += powerProvider.providePower(world, pos.add(blockPos));
				}
			}
		}
		// add power with random chance for extra according to decimal value
		int base = (int) extraPower;
		while (extraPower > 1.0F) extraPower -= 1.0F;
		// based on enchanting seed to prevent any tomfoolery
		if (this.random.nextFloat() < extraPower) base++;
		i.set(i.get() + base);
		// set enchanting seed again in case any calculations got screwed up
		this.random.setSeed(this.seed.get());
	}
}