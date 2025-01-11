package io.github.randommcsomethin.arcane.data;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public record EnchantmentPowerProviderTypes<T extends EnchantmentPowerProviderType>(MapCodec<T> codec) {
    public static final Registry<EnchantmentPowerProviderTypes<?>> REGISTRY = new SimpleRegistry<>(
            RegistryKey.ofRegistry(Identifier.of("arcane", "enchantment_power_provider_types")), Lifecycle.stable()
    );
}
