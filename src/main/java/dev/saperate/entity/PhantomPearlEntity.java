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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.WackyPearls.PHANTOMPEARL;
import static dev.saperate.WackyPearls.PHANTOMPEARLITEM;

public class PhantomPearlEntity extends ThrownItemEntity {
    private final World world = getWorld();
    private int numBounces = 8;
    private Vec3d lastBlockPos;

    public PhantomPearlEntity(EntityType<PhantomPearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public PhantomPearlEntity(World world, LivingEntity owner) {
        super(PHANTOMPEARL, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL;
    }

    public void setNumBounces(int num) {
        this.numBounces = num;
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
            world.addParticle(ParticleTypes.ASH, this.getX() + this.random.nextDouble() * 1.0,
                    this.getY() + this.random.nextDouble() * 1.0,
                    this.getZ() + this.random.nextDouble() * 1.0,
                    this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.world.isClient && !this.isRemoved()) {
            Entity entity = this.getOwner();
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
            if (entity instanceof ServerPlayerEntity) {
                if (numBounces <= 0) {
                    if (entity.hasVehicle()) {
                        serverPlayerEntity.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
                    } else {
                        entity.requestTeleport(this.getX(), this.getY(), this.getZ());
                    }
                    entity.damage(this.getDamageSources().fall(), 5.0f);
                    this.discard();
                }
                if (hitResult.getType() == HitResult.Type.BLOCK) {

                    Vec3d entityPos = this.getPos();
                    Vec3d blockPos = hitResult.getPos();
                    Vec3d normal = entityPos.subtract(blockPos).normalize();

                    if (lastBlockPos != null && lastBlockPos != blockPos) {
                        numBounces = numBounces - 1;

                        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                SoundEvents.BLOCK_STONE_BREAK,
                                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                    }
                    lastBlockPos = blockPos;

                    this.setVelocity(normal.x, normal.y, normal.z, -1f, 0f);

                    System.out.print(" " + this.numBounces);

                }
            }
        }
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
        ItemStack stack = new ItemStack(PHANTOMPEARLITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid()); // Store the UUID of the entity in the item's tag
        stack.setNbt(tag);
        return stack;
    }
}