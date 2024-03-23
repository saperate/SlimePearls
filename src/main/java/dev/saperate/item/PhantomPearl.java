package dev.saperate.item;

import dev.saperate.WackyPearls;
import dev.saperate.entity.PhantomPearlEntity;
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

import static dev.saperate.utils.SapsUtils.addToTooltip;


public class PhantomPearl extends Item {
    public int maxBlocks = 8;

    public PhantomPearl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        if(!user.isSneaking()) { //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            WackyPearls.coolDownPearls(user,20);
            if (!world.isClient) {
                PhantomPearlEntity enderPearlEntity = new PhantomPearlEntity(world, user);
                int numPhases = getNumBlocks(handStack);
                enderPearlEntity.setNumPhases(numPhases);
                enderPearlEntity.setItem(handStack);
                enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(enderPearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        }else if(offHandStack.getItem() == Items.PHANTOM_MEMBRANE && handStack.getCount() == 1){
            int diff = maxBlocks - getNumBlocks(handStack);
            int count = Math.min(offHandStack.getCount(), diff);

            if (!user.getAbilities().creativeMode) {
                offHandStack.decrement(count);
            }
            setNumBlocks(handStack,getNumBlocks(handStack) + count);
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public int getNumBlocks(ItemStack itemStack){
        NbtCompound tag = itemStack.getOrCreateNbt();
        int count = tag.getInt("numPhases");

        if(count == 0){
            count++;
        }
        setNumBlocks(itemStack,count);
        return count;
    }

    public void setNumBlocks(ItemStack itemStack, int val){
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putInt("numPhases",val);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        addToTooltip(tooltip, "item.sapswackystuff.phantom_pearl.tooltip", getNumBlocks(itemStack));
    }
}