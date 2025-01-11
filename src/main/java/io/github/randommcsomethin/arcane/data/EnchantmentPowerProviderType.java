package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EnchantmentPowerProviderType {
    Codec<EnchantmentPowerProviderType> CODEC = EnchantmentPowerProviderTypes.REGISTRY.getCodec().dispatch("type", EnchantmentPowerProviderType::getType, EnchantmentPowerProviderTypes::codec);
    public EnchantmentPowerProviderTypes<?> getType();
    public float getEnchantmentPower(World world, BlockPos pos);
    public boolean canProvidePower(World world, BlockPos pos);
    public float providePower(World world, BlockPos pos);
}
