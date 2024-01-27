package dev.saperate;

import dev.saperate.entity.SlimePearlEntity;
import dev.saperate.entity.SlimePearlEntityRenderer;
import dev.saperate.item.SlimePearl;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WackyPearls implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wacky-pearls");

	public static final SlimePearl SLIMEPEARLITEM = Registry.register(Registries.ITEM,
			new Identifier("sapswackystuff", "slime_pearl"),
			new SlimePearl(new FabricItemSettings()
					.maxCount(16)));
	public static final EntityType<SlimePearlEntity> SLIMEPEARL = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("sapswackystuff", "slime_pearl"),
			FabricEntityTypeBuilder.<SlimePearlEntity>create(SpawnGroup.MISC, SlimePearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());


	@Override
	public void onInitialize() {
		EntityRendererRegistry.register(SLIMEPEARL, (context) -> new SlimePearlEntityRenderer(context));
		LOGGER.info("Hello from wacky pearls!");
	}
}