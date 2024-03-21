package dev.saperate.item;

import dev.saperate.entity.LoversPearlEntity;
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

        UUID ownerUUID = getOwnerUUID(handStack, world);

        if (!user.isSneaking() && ownerUUID != null && world.getPlayerByUuid(ownerUUID) != null) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.getItemCooldownManager().set(this, 1);
            if (!world.isClient) {
                LoversPearlEntity loversPearlEntity = new LoversPearlEntity(world, world.getPlayerByUuid(ownerUUID));
                loversPearlEntity.setItem(handStack);
                loversPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(loversPearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        } else { //is sneaking
            if (!world.isClient && ownerUUID == null) {
                setOwner(handStack, user);
            }
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public static String getOwnerName(ItemStack itemStack, World world){
        NbtCompound tag = itemStack.getOrCreateNbt();
        String name;
        try {
            name = tag.getString("ownerName");
        } catch (Exception e) {
            return "";
        }

        return name;
    }

    public static UUID getOwnerUUID(ItemStack itemStack, World world) {
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
        tag.putString("ownerName", owner.getNameForScoreboard());
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        String ownerName = getOwnerName(itemStack,world);
        if (!ownerName.isEmpty()) {
            addToTooltip(tooltip, "item.sapswackystuff.lovers_pearl.tooltip", ownerName);
        } else {
            addTranslatable(tooltip, "item.sapswackystuff.lovers_pearl.tooltip.no_owner");
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        PlayerEntity owner = pointer.world().getPlayerByUuid(getOwnerUUID(stack, pointer.world()));

        Direction direction = pointer.state().get(DispenserBlock.FACING);
        double offsetX = direction.getOffsetX();
        double offsetY = direction.getOffsetY();
        double offsetZ = direction.getOffsetZ();

        double yaw = Math.atan2(offsetX, offsetZ);
        yaw = Math.toDegrees(yaw);

        double distanceXZ = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
        double pitch = Math.atan2(offsetY, distanceXZ);
        pitch = Math.toDegrees(pitch);

        System.out.println(yaw);
        System.out.println(pitch);
        System.out.println("----------------");
        System.out.println(owner.getYaw());
        System.out.println(owner.getPitch());



        if (owner == null) {
            return stack;
        }

        owner.getWorld().playSound(null, pointer.pos(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (owner.getWorld().getRandom().nextFloat() * 0.4f + 0.8f));

        if (!owner.getWorld().isClient) {
            stack.decrement(1);



            LoversPearlEntity loversPearlEntity = new LoversPearlEntity(owner.getEntityWorld(), owner,
                    pointer.pos().getX() + offsetX,
                    pointer.pos().getY() + offsetY,
                    pointer.pos().getZ() + offsetZ
            );




            loversPearlEntity.setItem(stack);
            loversPearlEntity.setVelocity(pitch, yaw, 0.0f, 1f, 0f);
            owner.getWorld().spawnEntity(loversPearlEntity);
        }
        return stack;
    }

}