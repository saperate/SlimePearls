package net.fabricmc.slimepearls;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.slimepearls.entity.PhantomPearlEntity;
import net.fabricmc.slimepearls.entity.PhantomPearlEntityRenderer;
import net.fabricmc.slimepearls.entity.RedstonePearlEntity;
import net.fabricmc.slimepearls.entity.RedstonePearlEntityRenderer;
import net.fabricmc.slimepearls.entity.SlimePearlEntity;
import net.fabricmc.slimepearls.entity.SlimePearlEntityRenderer;
import net.fabricmc.slimepearls.item.PhantomPearl;
import net.fabricmc.slimepearls.item.RedstonePearl;
import net.fabricmc.slimepearls.item.SlimePearl;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class main implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	// Slime Pearls
	public static final SlimePearl SLIMEPEARLITEM = Registry.register(Registry.ITEM,
			new Identifier("sapswackystuff", "slime_pearl"),
			new SlimePearl(new FabricItemSettings()
					.maxCount(16)));

	public static final EntityType<SlimePearlEntity> SLIMEPEARL = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("sapswackystuff", "slime_pearl"),
			FabricEntityTypeBuilder.<SlimePearlEntity>create(SpawnGroup.MISC, SlimePearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	public static final PhantomPearl PHANTOMPEARLITEM = Registry.register(Registry.ITEM,
			new Identifier("sapswackystuff", "phantom_pearl"),
			new PhantomPearl(new FabricItemSettings()
					.maxCount(16)));

	public static final EntityType<PhantomPearlEntity> PHANTOMPEARL = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("sapswackystuff", "phantom_pearl"),
			FabricEntityTypeBuilder.<PhantomPearlEntity>create(SpawnGroup.MISC, PhantomPearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	public static final RedstonePearl REDSTONEPEARLITEM = Registry.register(Registry.ITEM,
			new Identifier("sapswackystuff", "redstone_pearl"),
			new RedstonePearl(new FabricItemSettings()
					.maxCount(16)));

	public static final EntityType<RedstonePearlEntity> REDSTONEPEARL = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("sapswackystuff", "redstone_pearl"),
			FabricEntityTypeBuilder.<RedstonePearlEntity>create(SpawnGroup.MISC, RedstonePearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	@Override
	public void onInitialize() {
		EntityRendererRegistry.register(SLIMEPEARL, (context) -> new SlimePearlEntityRenderer(context));
		EntityRendererRegistry.register(PHANTOMPEARL, (context) -> new PhantomPearlEntityRenderer(context));
		EntityRendererRegistry.register(REDSTONEPEARL, (context) -> new RedstonePearlEntityRenderer(context));
		LOGGER.info("Hello Fabric world!");
	}
}