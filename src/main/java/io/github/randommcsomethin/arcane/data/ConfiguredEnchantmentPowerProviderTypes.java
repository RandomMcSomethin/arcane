package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public record ConfiguredEnchantmentPowerProviderTypes<T extends EnchantmentPowerProviderType>(
        com.mojang.serialization.Codec<EnchantmentPowerProviderType> codec) {
    public static final Registry<ConfiguredEnchantmentPowerProviderTypes<?>> REGISTRY = new SimpleRegistry<>(
            RegistryKey.ofRegistry(Identifier.of("arcane", "configured_enchantment_power_provider_types")), Lifecycle.stable()
    );
}
