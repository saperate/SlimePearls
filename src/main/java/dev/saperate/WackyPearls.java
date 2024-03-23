package dev.saperate;

import dev.saperate.entity.*;
import dev.saperate.item.LoversPearl;
import dev.saperate.item.PhantomPearl;
import dev.saperate.item.RedstonePearl;
import dev.saperate.item.SlimePearl;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WackyPearls implements ModInitializer {
	public static final String MODID = "wacky-pearls";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final SlimePearl SLIMEPEARLITEM = Registry.register(Registries.ITEM,
			new Identifier("sapswackystuff", "slime_pearl"),
			new SlimePearl(new FabricItemSettings()
					.maxCount(16)));
	public static final EntityType<SlimePearlEntity> SLIMEPEARL = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("sapswackystuff", "slime_pearl"),
			FabricEntityTypeBuilder.<SlimePearlEntity>create(SpawnGroup.MISC, SlimePearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	public static final RedstonePearl REDSTONEPEARLITEM = Registry.register(Registries.ITEM,
			new Identifier("sapswackystuff", "redstone_pearl"),
			new RedstonePearl(new FabricItemSettings()
					.maxCount(16)));

	public static final EntityType<RedstonePearlEntity> REDSTONEPEARL = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("sapswackystuff", "redstone_pearl"),
			FabricEntityTypeBuilder.<RedstonePearlEntity>create(SpawnGroup.MISC, RedstonePearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	public static final PhantomPearl PHANTOMPEARLITEM = Registry.register(Registries.ITEM,
			new Identifier("sapswackystuff", "phantom_pearl"),
			new PhantomPearl(new FabricItemSettings()
					.maxCount(16)));

	public static final EntityType<PhantomPearlEntity> PHANTOMPEARL = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("sapswackystuff", "phantom_pearl"),
			FabricEntityTypeBuilder.<PhantomPearlEntity>create(SpawnGroup.MISC, PhantomPearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	public static final LoversPearl LOVERSPEARLITEM = Registry.register(Registries.ITEM,
			new Identifier("sapswackystuff", "lovers_pearl"),
			new LoversPearl(new FabricItemSettings()
					.maxCount(1)));

	public static final EntityType<LoversPearlEntity> LOVERSPEARL = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("sapswackystuff", "lovers_pearl"),
			FabricEntityTypeBuilder.<LoversPearlEntity>create(SpawnGroup.MISC, LoversPearlEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	@Override
	public void onInitialize() {
		EntityRendererRegistry.register(SLIMEPEARL, SlimePearlEntityRenderer::new);
		EntityRendererRegistry.register(REDSTONEPEARL, RedstonePearlEntityRenderer::new);
		EntityRendererRegistry.register(PHANTOMPEARL, PhantomPearlEntityRenderer::new);
		EntityRendererRegistry.register(LOVERSPEARL, LoversPearlEntityRenderer::new);
		DispenserBlock.registerBehavior(LOVERSPEARLITEM,LOVERSPEARLITEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(SLIMEPEARLITEM);
			content.add(PHANTOMPEARLITEM);
			content.add(REDSTONEPEARLITEM);
			content.add(LOVERSPEARLITEM);
		});


		LOGGER.info("Hello from wacky pearls!");
	}

	public static void coolDownPearls(PlayerEntity user, int amount){
		user.getItemCooldownManager().set(Items.ENDER_PEARL, amount);
		coolDownModdedPearls(user,amount);
	}
	public static void coolDownModdedPearls(PlayerEntity user, int amount){
		user.getItemCooldownManager().set(SLIMEPEARLITEM, amount);
		user.getItemCooldownManager().set(PHANTOMPEARLITEM, amount);
		user.getItemCooldownManager().set(REDSTONEPEARLITEM, amount);
		user.getItemCooldownManager().set(LOVERSPEARLITEM, amount);
	}

}