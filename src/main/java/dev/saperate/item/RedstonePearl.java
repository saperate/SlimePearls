package dev.saperate.item;

import dev.saperate.entity.RedstonePearlEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class RedstonePearl extends Item {
    private static final int maxTime = 8;

    public RedstonePearl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        if (!user.isSneaking()) { //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_LEVER_CLICK,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.getItemCooldownManager().set(this, 1);
            if (!world.isClient) {
                RedstonePearlEntity redstonePearlEntity = new RedstonePearlEntity(world, user);
                int numBounces = getNumTime(handStack);
                redstonePearlEntity.setNumTime(numBounces);
                redstonePearlEntity.setItem(handStack);
                redstonePearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 1f);
                world.spawnEntity(redstonePearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        } else if (offHandStack.getItem() == Items.REDSTONE && handStack.getCount() == 1) { //Adding bounces
            int diff = maxTime - getNumTime(handStack);
            int count = Math.min(offHandStack.getCount(), diff);

            if (!user.getAbilities().creativeMode) {
                offHandStack.decrement(count);
            }
            setNumTime(handStack, getNumTime(handStack) + count);
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public int getNumTime(ItemStack itemStack) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        int count = tag.getInt("numBounces");

        if (count == 0) {
            count++;
        }
        setNumTime(itemStack, count);
        return count;
    }

    public void setNumTime(ItemStack itemStack, int val) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putInt("numBounces", val);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.sapswackystuff.redstone_pearl.tooltip", getNumTime(itemStack)));
    }
}