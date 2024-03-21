package dev.saperate.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.WackyPearls.SLIMEPEARL;
import static dev.saperate.WackyPearls.SLIMEPEARLITEM;

public class SlimePearlEntity extends ThrownItemEntity {
    private final World world = getWorld();
    private int numBounces = 1;

    public SlimePearlEntity(EntityType<SlimePearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimePearlEntity(World world, LivingEntity owner) {
        super(SLIMEPEARL, owner, world);
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
            this.world.addParticle(ParticleTypes.ITEM_SLIME, this.getX() + this.random.nextDouble(),
                    this.getY() + this.random.nextDouble(),
                    this.getZ() + this.random.nextDouble(),
                    this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }

        if (!this.world.isClient && !this.isRemoved()) {
            Entity entity = this.getOwner();
            if(entity == null){
                this.discard();
                return;
            }
            if (numBounces <= 0) {
                tpToPearl(entity, this);
            }
            if (hitResult.getType() == HitResult.Type.BLOCK) {

                BlockHitResult blockHit = (BlockHitResult) hitResult;
                Vec3d entityPos = this.getPos();
                Vec3d blockPos = hitResult.getPos();
                Vec3d normal = entityPos.subtract(blockPos).normalize();

                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
                        SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

                numBounces = numBounces - 1;

                int velocity = 1;

                if (blockHit.getSide() == Direction.SOUTH || blockHit.getSide() == Direction.NORTH) {
                    this.setVelocity(normal.x * -1, normal.y * -1, normal.z, velocity, 0f);
                } else if (blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST) {
                    this.setVelocity(normal.x, normal.y * -1, normal.z * -1, velocity, 0f);
                } else {
                    this.setVelocity(normal.x * -1, normal.y, normal.z * -1, velocity, 0f);
                }

            }
        }
        if (numBounces <= 0) {
            world.playSound(null,
                    this.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_TELEPORT,
                    SoundCategory.PLAYERS);
        }
    }

    public static void tpToPearl(Entity target, Entity pearl) {
        if (target.hasVehicle() && target instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.requestTeleportAndDismount(pearl.getX(), pearl.getY(), pearl.getZ());
        } else {
            target.requestTeleport(pearl.getX(), pearl.getY(), pearl.getZ());
        }
        target.damage(pearl.getDamageSources().fall(), 5.0f);
        pearl.discard();
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity && !entity.isAlive()) {
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
        ItemStack stack = new ItemStack(SLIMEPEARLITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.setNbt(tag);
        return stack;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL;
    }

    public void setNumBounces(int num) {
        this.numBounces = num;
    }
}
