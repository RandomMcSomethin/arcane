package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.randommcsomethin.arcane.ArcaneMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockStateEnchantmentPowerProviderType implements EnchantmentPowerProviderType {
    // codec
    public static final MapCodec<BlockStateEnchantmentPowerProviderType> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
            StatePowerProvider.CODEC.codec().listOf().fieldOf("providers").forGetter(BlockStateEnchantmentPowerProviderType::getProviders),
            Codecs.TAG_ENTRY_ID.fieldOf("provider_blocks").forGetter(BlockStateEnchantmentPowerProviderType::getProviderBlocks),
            Codec.BOOL.optionalFieldOf("additive", false).forGetter(BlockStateEnchantmentPowerProviderType::getIsAdditive)
    ).apply(instance, BlockStateEnchantmentPowerProviderType::new)
    )
    //.validate(BlockStateEnchantmentPowerProviderType::blocksHaveProperties)
    ;

    // variables
    public final List<StatePowerProvider> providers;
    public final Codecs.TagEntryId providerBlocks;
    public final TagKey<Block> providerBlocksTag;
    public final boolean isAdditive;

    // constructor
    public BlockStateEnchantmentPowerProviderType(List<StatePowerProvider> providers, Codecs.TagEntryId providerBlocks, boolean isAdditive) {
        this.providerBlocks = providerBlocks;
        this.providers = providers;
        this.providerBlocksTag = TagKey.of(RegistryKeys.BLOCK, providerBlocks.id());
        this.isAdditive = isAdditive;
    }

    // validator
    /* (commenting this out until I get it working)
    private static DataResult<BlockStateEnchantmentPowerProviderType> blocksHaveProperties(BlockStateEnchantmentPowerProviderType providerType) {
        // go through each block in tag
        for (RegistryEntry<Block> entry : Registries.BLOCK.iterateEntries(providerType.providerBlocksTag)) {
            // check entry against each state provider
            for (int i = 0; i < providerType.providers.size(); i++) {
                StatePowerProvider provider = providerType.providers.get(i);
                // check each state provider's states
                for (int j = 0; j < provider.getStates().size(); j++) {
                    StatePredicate statePredicate = provider.getStates().get(j);
                    statePredicate.findMissing(entry.value().getStateManager()).map(property -> DataResult.error(() -> "Block " + entry.value() + " has no property " + property
                    ));
                }
            }
        }
        return DataResult.success(providerType);
    }
    */

    // interface methods
    @Override
    public float getEnchantmentPower(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        float amount = 0.0F;
        for (StatePowerProvider provider : providers) {
            for (int j = 0; j < provider.getStates().size(); j++) {
                StatePredicate statePredicate = provider.getStates().get(j);
                if (statePredicate.test(state)) {
                    if (isAdditive) {
                        amount += provider.getAmount();
                    } else return provider.getAmount();
                }
            }
        }
        return amount;
    }

    @Override
    public boolean canProvidePower(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isIn(providerBlocksTag)) return false;
        // test states if it passes
        for (StatePowerProvider provider : providers) {
            for (int j = 0; j < provider.getStates().size(); j++) {
                StatePredicate statePredicate = provider.getStates().get(j);
                if (statePredicate.test(state)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public float providePower(World world, BlockPos pos) {
        return getEnchantmentPower(world, pos);
    }

    @Override
    public EnchantmentPowerProviderTypes<?> getType() {
        return ArcaneMain.BLOCK_STATE;
    }

    // getters
    public Codecs.TagEntryId getProviderBlocks() {return this.providerBlocks;}
    public List<StatePowerProvider> getProviders() { return this.providers;}
    public boolean getIsAdditive() { return this.isAdditive;}
}
