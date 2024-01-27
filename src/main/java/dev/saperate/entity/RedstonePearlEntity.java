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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.WackyPearls.REDSTONEPEARL;
import static dev.saperate.WackyPearls.REDSTONEPEARLITEM;

public class RedstonePearlEntity extends ThrownItemEntity {
    private int tickCount = 0;
    private int numTime = 200;
    private Vec3d initPos;
    private Vec3d initVel;
    private Boolean stuck = false;

    public RedstonePearlEntity(EntityType<RedstonePearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public RedstonePearlEntity(World world, LivingEntity owner) {
        super(REDSTONEPEARL, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE;
    }

    public void setNumTime(int num) {
        this.numTime = num * 20;
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
        if (!stuck) {
            System.out.println("New Pearl");
            for (int i = 0; i < 32; ++i) {
                this.world.addParticle(ParticleTypes.POOF, this.getX() + this.random.nextDouble() * 1.0,
                        this.getY() + this.random.nextDouble() * 1.0,
                        this.getZ() + this.random.nextDouble() * 1.0,
                        this.random.nextGaussian(), 0.0, this.random.nextGaussian());
            }
            world.playSound(null, this.getOwner().getX(), this.getOwner().getY(), this.getOwner().getZ(),
                    SoundEvents.BLOCK_DISPENSER_FAIL,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            initPos = new Vec3d(this.getX(), this.getY(), this.getZ()).add(hitResult.getPos()).add(hitResult.getPos())
                    .add(hitResult.getPos()).multiply(0.25);
            initVel = new Vec3d(this.getVelocity().x, this.getVelocity().y, this.getVelocity().z).multiply(0.001);
            stuck = true;
        }
        this.requestTeleport(initPos.x, initPos.y, initPos.z);
        this.setVelocity(initVel);
        this.setNoGravity(true);
    }


    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if(entity == null){
            this.discard();
            return;
        }
        if (entity instanceof PlayerEntity && !entity.isAlive()) {
            this.discard();
        } else if(!stuck ) {
            super.tick();
        }

        if(stuck) {
            tickCount++;
            if (tickCount >= numTime) {
                if (entity.hasVehicle()) {
                    entity.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
                } else {
                    entity.requestTeleport(this.getX(), this.getY(), this.getZ());
                }
                entity.damage(this.getDamageSources().fall(), 5.0f);
                this.discard();
            }
        }
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        Entity entity = this.getOwner();
        if (entity != null && entity.world.getRegistryKey() != destination.getRegistryKey()) {
            this.setOwner(null);
        }
        return super.moveToWorld(destination);
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(REDSTONEPEARLITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.setNbt(tag);
        return stack;
    }
}
