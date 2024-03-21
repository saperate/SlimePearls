package dev.saperate.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.WackyPearls.*;

public class LoversPearlEntity extends ThrownItemEntity {
    private final World world = getWorld();

    public LoversPearlEntity(EntityType<LoversPearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public LoversPearlEntity(World world, LivingEntity owner) {
        super(LOVERSPEARL, owner, world);
    }

    public LoversPearlEntity(World world, LivingEntity owner, double x, double y, double z){
        super(LOVERSPEARL,x,y,z,world);
        setOwner(owner);
    }

    @Override
    protected Item getDefaultItem() {
        return LOVERSPEARLITEM;
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.setVelocity(entity.getVelocity().add(this.getVelocity()));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        for (int i = 0; i < 32; ++i) {
            world.addParticle(ParticleTypes.HEART, this.getX() + this.random.nextDouble(),
                    this.getY() + this.random.nextDouble(),
                    this.getZ() + this.random.nextDouble(),
                    this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }

        if (!this.world.isClient && !this.isRemoved()) {
            Entity entity = this.getOwner();
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            if (entity instanceof ServerPlayerEntity) {
                if (entity.hasVehicle()) {
                    serverPlayerEntity.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
                } else {
                    entity.requestTeleport(this.getX(), this.getY(), this.getZ());
                }
                entity.damage(this.getDamageSources().fall(), 5.0f);
                this.discard();
            }
        }
        world.playSound(null,
                this.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_TELEPORT,
                SoundCategory.PLAYERS);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity && !entity.isAlive() || this.getY() == -10) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        Entity entity = this.getOwner();
        if (entity != null && entity.getWorld().getRegistryKey() != destination.getRegistryKey()) {
            this.setOwner(null);
        }
        return super.moveToWorld(destination);
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(LOVERSPEARLITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.setNbt(tag);
        return stack;
    }
}