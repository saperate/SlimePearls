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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static dev.saperate.WackyPearls.REDSTONEPEARL;
import static dev.saperate.WackyPearls.REDSTONEPEARLITEM;

public class RedstonePearlEntity extends ThrownItemEntity {
    private final World world = getWorld();
    private int tickCount = 0, numTime = 200;
    private Boolean stuck = false;
    private Entity stuckEntity;

    public RedstonePearlEntity(EntityType<RedstonePearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public RedstonePearlEntity(World world, LivingEntity owner) {
        super(REDSTONEPEARL, owner, world);
        setOwner(owner);
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
        if (stuckEntity != null){
            return;
        }
        Entity entity = entityHitResult.getEntity();
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_LEVER_CLICK,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        entity.setVelocity(entity.getVelocity().add(this.getVelocity()));
        stuckEntity = entity;
        stuck = true;
        this.setVelocity(new Vec3d(0,0,0));
        this.setNoGravity(true);
        this.setInvisible(true);

    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!stuck) {
            if(getOwner() == null){
                discard();
                return;
            }
            world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_LEVER_CLICK,
                    SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            makeParticles(ParticleTypes.POOF,16,this);
            stuck = true;
            this.setVelocity(new Vec3d(0,0,0));
            this.setNoGravity(true);
            this.setInvisible(true);
        }
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
        }
        super.tick();
        this.age = 1;

        if(stuck) {
            tickCount++;
            if(stuckEntity != null){
                if(stuckEntity.isSneaking()){
                    tpToPearl(entity);
                    this.discard();
                }
                this.requestTeleport(stuckEntity.getX(),stuckEntity.getY(),stuckEntity.getZ());
                makeParticles(ParticleTypes.PORTAL,16,stuckEntity);
            }
            if (tickCount >= numTime) {
                tpToPearl(entity);
                this.discard();
            }
        }
    }

    public void makeParticles(ParticleEffect effect, int amount,Entity curr){
        for (int i = 0; i < amount; ++i) {
            this.world.addParticle(effect, curr.getX() + this.random.nextDouble() *  0.5f,
                    curr.getY() + this.random.nextDouble() * 0.5f,
                    curr.getZ() + this.random.nextDouble() *  0.5f,
                    this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
    }

    public void tpToPearl(Entity entity){
        if (entity.hasVehicle()) {
            entity.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
        } else {
            entity.requestTeleport(this.getX(), this.getY(), this.getZ());
        }
        entity.damage(this.getDamageSources().fall(), 5.0f);
        world.playSound(null,
                this.getBlockPos(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS);
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
        ItemStack stack = new ItemStack(REDSTONEPEARLITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.setNbt(tag);
        return stack;
    }

}