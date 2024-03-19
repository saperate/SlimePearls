package dev.saperate.item;

import dev.saperate.entity.LoversPearlEntity;
import dev.saperate.entity.PhantomPearlEntity;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.World;

import java.util.List;

public class LoversPearl extends Item implements DispenserBehavior {
    private Entity owner;

    public LoversPearl(Settings settings) {
        super(settings);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack handStack = user.getStackInHand(hand);
        ItemStack offHandStack = user.getOffHandStack();

        if(!user.isSneaking()) { //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.getItemCooldownManager().set(this, 1);
            if (!world.isClient) {
                LoversPearlEntity enderPearlEntity = new LoversPearlEntity(world, user);
                int numBounces = getNumBlocks(handStack);
                enderPearlEntity.setNumBounces(numBounces);
                enderPearlEntity.setItem(handStack);
                enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(enderPearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        }else { //is sneaking
            if(owner == null){
                owner = user;
            }
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public int getNumBlocks(ItemStack itemStack){
        NbtCompound tag = itemStack.getOrCreateNbt();
        int count = tag.getInt("numBounces");

        if(count == 0){
            count++;
        }
        setNumBlocks(itemStack,count);
        return count;
    }

    public void setNumBlocks(ItemStack itemStack, int val){
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putInt("numBounces",val);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if(Screen.hasShiftDown()){//todo make this better
            tooltip.add(Text.translatable("item.sapswackystuff.lovers_pearl.tooltip.shifting"));
        }else if(owner != null){
            tooltip.add(Text.translatable("item.sapswackystuff.lovers_pearl.tooltip", owner.getDisplayName()));
        }else{
            tooltip.add(Text.translatable("item.sapswackystuff.lovers_pearl.tooltip.no_owner", "\n"));
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        System.err.println("Tried to dispense lovers pearl");
        return null;
    }

}