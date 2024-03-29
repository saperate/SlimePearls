package dev.saperate.item;

import dev.saperate.WackyPearls;
import dev.saperate.entity.SlimePearlEntity;
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

public class SlimePearl extends Item {
    private static final int maxBounce = 8;

    public SlimePearl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        if(!user.isSneaking()){ //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            WackyPearls.coolDownPearls(user,20);
            if (!world.isClient) {
                SlimePearlEntity slimePearlEntity = new SlimePearlEntity(world, user);
                int numBounces = getNumBounces(handStack);
                slimePearlEntity.setNumBounces(numBounces);
                slimePearlEntity.setItem(handStack);
                slimePearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(slimePearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        }else if(offHandStack.getItem() == Items.SLIME_BALL && handStack.getCount() == 1){ //Adding bounces
            int diff = maxBounce - getNumBounces(handStack);
            int count = Math.min(offHandStack.getCount(), diff);

            if (!user.getAbilities().creativeMode) {
                offHandStack.decrement(count);
            }
            setNumBounces(handStack,getNumBounces(handStack) + count);
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public int getNumBounces(ItemStack itemStack){
        NbtCompound tag = itemStack.getOrCreateNbt();
        int count = tag.getInt("numBounces");

        if(count == 0){
            count++;
        }
        setNumBounces(itemStack,count);
        return count;
    }

    public void setNumBounces(ItemStack itemStack, int val){
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putInt("numBounces",val);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        addToTooltip(tooltip, "item.sapswackystuff.slime_pearl.tooltip", getNumBounces(itemStack));
    }
}
