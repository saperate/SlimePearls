package dev.saperate.item;

import dev.saperate.WackyPearls;
import dev.saperate.entity.RedstonePearlEntity;
import dev.saperate.utils.SapsUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static dev.saperate.utils.SapsUtils.addToTooltip;

public class RedstonePearl extends Item {
    private static final int maxTime = 3600;

    public RedstonePearl(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        if (!user.isSneaking()) { //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            WackyPearls.coolDownPearls(user,20);
            if (!world.isClient) {
                RedstonePearlEntity redstonePearlEntity = new RedstonePearlEntity(world, user);
                int numTime = SapsUtils.getCustomInt(handStack, "numTime");
                redstonePearlEntity.setOwner(user);
                redstonePearlEntity.setNumTime(numTime);
                redstonePearlEntity.setItem(handStack);
                redstonePearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 1f);
                world.spawnEntity(redstonePearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        } else if (offHandStack.getItem() == Items.REDSTONE && handStack.getCount() == 1) { //Adding bounces
            int diff = maxTime - SapsUtils.getCustomInt(handStack, "numTime");
            int count = Math.min(offHandStack.getCount(), diff);

            if (!user.getAbilities().creativeMode) {
                offHandStack.decrement(count);
            }
            SapsUtils.setCustomInt(handStack, SapsUtils.getCustomInt(handStack, "numTime") + count, "numTime");
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        addToTooltip(tooltip, "item.sapswackystuff.redstone_pearl.tooltip", SapsUtils.getCustomInt(stack, "numTime"));
    }
}