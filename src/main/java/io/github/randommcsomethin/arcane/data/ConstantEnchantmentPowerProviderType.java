package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.randommcsomethin.arcane.ArcaneMain;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConstantEnchantmentPowerProviderType implements EnchantmentPowerProviderType {
    // codec
    public static final MapCodec<ConstantEnchantmentPowerProviderType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount").forGetter(ConstantEnchantmentPowerProviderType::getAmount),
            Codecs.TAG_ENTRY_ID.fieldOf("provider_blocks").forGetter(ConstantEnchantmentPowerProviderType::getProviderBlocks)
    ).apply(instance, ConstantEnchantmentPowerProviderType::new));

    // variables
    public final float amount;
    public final Codecs.TagEntryId providerBlocks;
    public final TagKey<Block> providerBlocksTag;

    // constructor
    public ConstantEnchantmentPowerProviderType(float constant, Codecs.TagEntryId providerBlocks) {
        this.amount = constant;
        this.providerBlocks = providerBlocks;
        this.providerBlocksTag = TagKey.of(RegistryKeys.BLOCK, providerBlocks.id());
    }
    // interface methods
    @Override
    public float getEnchantmentPower(World world, BlockPos pos) {
        return this.amount;
    }

    @Override
    public boolean canProvidePower(World world, BlockPos pos) { return world.getBlockState(pos).isIn(providerBlocksTag); }

    @Override
    public float providePower(World world, BlockPos pos) { return amount; }

    @Override
    public EnchantmentPowerProviderTypes<?> getType() {
        return ArcaneMain.CONSTANT;
    }

    // getters
    public float getAmount() {return this.amount;}
    public Codecs.TagEntryId getProviderBlocks() {return this.providerBlocks;}
}
