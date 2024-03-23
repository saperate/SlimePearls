package dev.saperate.item;

import dev.saperate.WackyPearls;
import dev.saperate.entity.LoversPearlEntity;
import dev.saperate.utils.SapsUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static dev.saperate.utils.SapsUtils.*;

public class LoversPearl extends Item implements DispenserBehavior {

    public LoversPearl(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        UUID ownerUUID = getOwnerUUID(handStack);

        if (!user.isSneaking() && ownerUUID != null && world.getPlayerByUuid(ownerUUID) != null) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            WackyPearls.coolDownPearls(user,20);
            if (!world.isClient) {
                LoversPearlEntity loversPearlEntity = new LoversPearlEntity(world, world.getPlayerByUuid(ownerUUID),
                        user.getX(),
                        user.getEyeY() - 0.10000000149011612,
                        user.getZ()
                );
                loversPearlEntity.setItem(handStack);
                loversPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(loversPearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        } else {
            if (user.isSneaking() && !world.isClient && ownerUUID == null) {
                setOwner(handStack, user);
            }
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public static String getOwnerName(ItemStack itemStack) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        String name;
        try {
            name = tag.getString("ownerName");
        } catch (Exception e) {
            return "";
        }

        return name;
    }

    public static UUID getOwnerUUID(ItemStack itemStack) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        UUID ownerUUID;
        try {
            ownerUUID = tag.getUuid("owner");
        } catch (Exception e) {
            return null;
        }

        return ownerUUID;
    }

    public static void setOwner(ItemStack itemStack, PlayerEntity owner) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putUuid("owner", owner.getUuid());
        tag.putString("ownerName", owner.getEntityName());
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        String ownerName = getOwnerName(itemStack);
        if (!ownerName.isEmpty()) {
            addToTooltip(tooltip, "item.sapswackystuff.lovers_pearl.tooltip", ownerName);
        } else {
            addTranslatable(tooltip, "item.sapswackystuff.lovers_pearl.tooltip.no_owner");
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        UUID ownerUUID = getOwnerUUID(stack);

        if (ownerUUID == null) {
            return stack;
        }
        PlayerEntity owner = pointer.getWorld().getPlayerByUuid(ownerUUID);
        if(owner == null){
            return stack;
        }

        owner.getWorld().playSound(null, pointer.getPos(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (owner.getWorld().getRandom().nextFloat() * 0.4f + 0.8f));

        if (!owner.getWorld().isClient) {
            stack.decrement(1);
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            LoversPearlEntity loversPearlEntity = new LoversPearlEntity(owner.getEntityWorld(), owner,
                    pointer.getPos().getX() + direction.getOffsetX() + 0.5,
                    pointer.getPos().getY() + direction.getOffsetY() + 0.5,
                    pointer.getPos().getZ() + direction.getOffsetZ() + 0.5
            );

            loversPearlEntity.setItem(stack);
            loversPearlEntity.setVelocity(
                    direction.getOffsetX(),
                    direction.getOffsetY(),
                    direction.getOffsetZ(),
                    1f, 0f
            );
            owner.getWorld().spawnEntity(loversPearlEntity);
        }
        return stack;
    }

}