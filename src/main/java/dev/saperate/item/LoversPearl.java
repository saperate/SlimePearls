package dev.saperate.item;

import dev.saperate.entity.LoversPearlEntity;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
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

        if(!user.isSneaking()) { //Launching pearl
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            user.getItemCooldownManager().set(this, 1);
            if (!world.isClient) {
                LoversPearlEntity enderPearlEntity = new LoversPearlEntity(world, user);
                //int numBounces = getNumBlocks(handStack);
                enderPearlEntity.setNumBounces(0);
                enderPearlEntity.setItem(handStack);
                enderPearlEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1f, 0f);
                world.spawnEntity(enderPearlEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!user.getAbilities().creativeMode) {
                handStack.decrement(1);
            }
        }else { //is sneaking
            if(!world.isClient && getOwner(handStack, world) == null){
                setOwner(handStack, user);
            }
        }
        return TypedActionResult.success(handStack, world.isClient());
    }

    public PlayerEntity getOwner(ItemStack itemStack, World world){
        NbtCompound tag = itemStack.getOrCreateNbt();
        UUID ownerUUID;
        try{
             ownerUUID = tag.getUuid("owner");
        }catch (Exception e){
            return null;
        }

        return world.getPlayerByUuid(ownerUUID);
    }

    public void setOwner(ItemStack itemStack, PlayerEntity owner){
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putUuid("owner", owner.getUuid());
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Entity owner = getOwner(itemStack,world);
        if(owner != null){
            addToTooltip(tooltip, "item.sapswackystuff.lovers_pearl.tooltip", owner.getDisplayName());
        }else{
            addTranslatable(tooltip, "item.sapswackystuff.lovers_pearl.tooltip.no_owner");
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        System.err.println("Tried to dispense lovers pearl");
        return stack;
    }

}