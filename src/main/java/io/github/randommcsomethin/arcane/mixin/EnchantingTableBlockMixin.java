package io.github.randommcsomethin.arcane.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.randommcsomethin.arcane.ArcaneMain;
import io.github.randommcsomethin.arcane.data.EnchantmentPowerProviderType;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.stream.Stream;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    // display stuff
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/EnchantingTableBlock;canAccessPowerProvider(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Z"), method = "randomDisplayTick")
    private boolean extraPowerProviderCheck(World world, BlockPos tablePos, BlockPos providerOffset, Operation<Boolean> original) {
        // setup
        boolean providesPower = false;
        // check if block can be reached
        if (ArcaneMain.canReachBlock(world, tablePos, providerOffset)) {
            // maybe a little expensive, but should only run when a particle spawns AND if the block in question is reachable
            Stream<RegistryEntry<EnchantmentPowerProviderType>> s = world.getRegistryManager().getWrapperOrThrow(ArcaneMain.CONFIGURED_ENCHANTMENT_POWER_PROVIDER_TYPES).streamEntries().map(reference -> reference);
            // check each power provider type
            for (RegistryEntry<EnchantmentPowerProviderType> powerProviderRef : s.toList()) {
                EnchantmentPowerProviderType powerProvider = powerProviderRef.value();
                // check if block can provide power according to the power provider
                if (powerProvider.canProvidePower(world, tablePos.add(providerOffset))) {
                    providesPower = true;
                }
            }
        }
        return  providesPower || original.call(world, tablePos, providerOffset);
    }

}
