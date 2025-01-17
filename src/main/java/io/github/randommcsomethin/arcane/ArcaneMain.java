package io.github.randommcsomethin.arcane;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import io.github.randommcsomethin.arcane.data.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ArcaneMain implements ModInitializer {
	public static final String MOD_ID = "arcane";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EnchantmentPowerProviderTypes<ConstantEnchantmentPowerProviderType> CONSTANT = register("constant", new EnchantmentPowerProviderTypes<>(ConstantEnchantmentPowerProviderType.CODEC));
	public static final EnchantmentPowerProviderTypes<BlockStateEnchantmentPowerProviderType> BLOCK_STATE = register("block_state", new EnchantmentPowerProviderTypes<>(BlockStateEnchantmentPowerProviderType.CODEC));

	public static final RegistryKey<Registry<EnchantmentPowerProviderType>> CONFIGURED_ENCHANTMENT_POWER_PROVIDER_TYPES = RegistryKey.ofRegistry(Identifier.of(MOD_ID, "enchantment_power_provider"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Codec<EnchantmentPowerProviderTypes<?>> powerTypesCodec = EnchantmentPowerProviderTypes.REGISTRY.getCodec();
		Codec<EnchantmentPowerProviderType> powerCodec = powerTypesCodec.dispatch("type", EnchantmentPowerProviderType::getType, EnchantmentPowerProviderTypes::codec);
		DynamicRegistries.registerSynced(CONFIGURED_ENCHANTMENT_POWER_PROVIDER_TYPES, powerCodec);

		DynamicRegistrySetupCallback.EVENT.register(dynamicRegistryView ->
		{
			dynamicRegistryView.registerEntryAdded(CONFIGURED_ENCHANTMENT_POWER_PROVIDER_TYPES,
			(rawid, id, object) -> {
				//LOGGER.info("Loaded enchantment power provider type " + id);
			});
		});
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
			new SimpleSynchronousResourceReloadListener() {
			   @Override
			   public Identifier getFabricId() {
				   return Identifier.of("arcane", "resources");
			   }

			   @Override
			   public void reload(ResourceManager manager) {
				   // clear
				   // load entries
				   // types
				   for (Map.Entry<Identifier, Resource> entry : manager.findResources("arcane/enchantment_power_provider_type", path -> path.getPath().endsWith(".json")).entrySet()) {
					   try (InputStream stream = manager.getResource(entry.getKey()).get().getInputStream()) {
						   String jsonString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
						   JsonElement json = JsonHelper.deserialize(jsonString);
						   // LOGGER.info("Loaded enchantment power provider type " + entry.getKey() + " with data {}", json);
					   } catch(Exception e) {
						   LOGGER.error("Could not load enchantment power provider type " + entry.getKey() + ":\n" + e.getMessage());
					   }
				   }
			   }
		   }
		);
	}

	// helper methods
	public static <T extends EnchantmentPowerProviderType> EnchantmentPowerProviderTypes<T> register(String id, EnchantmentPowerProviderTypes<T> type) {
		return Registry.register(EnchantmentPowerProviderTypes.REGISTRY, Identifier.of(MOD_ID, id), type);
	}

	public static boolean canReachBlock(World world, BlockPos table, BlockPos offset) {
		return world.getBlockState(table.add(offset.getX()/2, offset.getY(), offset.getZ()/2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
	}
}