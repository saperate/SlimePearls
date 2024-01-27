package dev.saperate.item;

import dev.saperate.entity.SlimePearlEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class SlimePearl extends Item {

    public SlimePearl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        user.getItemCooldownManager().set(this, 1);
        if (!world.isClient) {
            SlimePearlEntity enderPearlEntity = new SlimePearlEntity(world, user);
            int numBounces = this.getNumBounces(itemStack);
            enderPearlEntity.setNumBounces(numBounces);
            enderPearlEntity.setItem(itemStack);
            enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
            world.spawnEntity(enderPearlEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }



    public int getNumBounces(ItemStack itemStack){
        NbtCompound tag = itemStack.getOrCreateNbt();
        return tag.getInt("numBounces");
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.sapswackystuff.slime_pearl.tooltip", getNumBounces(itemStack)));
    }
}
