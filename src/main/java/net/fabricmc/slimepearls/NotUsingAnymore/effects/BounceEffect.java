/*package net.fabricmc.slimepearls.NotUsingAnymore.effects;

import java.io.Console;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class BounceEffect extends StatusEffect {
    public BounceEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.world.isClient()) {
            Box playerBox = pLivingEntity.getBoundingBox().expand(0.01);
            Iterable<VoxelShape> block = pLivingEntity.getWorld().getBlockCollisions(pLivingEntity, playerBox);
            block.forEach((element) -> {
                Vec3d pos1 = new Vec3d(element.getMin(Direction.Axis.X), element.getMin(Direction.Axis.Y),
                        element.getMin(Direction.Axis.Z));
                Vec3d pos2 = new Vec3d(element.getMax(Direction.Axis.X), element.getMax(Direction.Axis.Y),
                        element.getMax(Direction.Axis.Z));
                Vec3d plrPos = pLivingEntity.getPos();
                Vec3d blockPos;
                if (plrPos == pos1) {
                    blockPos = pos2;
                } else {
                    blockPos = pos1;
                }

                Entity entity = pLivingEntity;
                Vec3d normal = plrPos.subtract(blockPos).normalize();



                
                entity.setVelocity(10, 10000, normal.z * -1);
                System.out.println(normal.y + 10);

            });
        }

        super.applyUpdateEffect(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int pDuration, int pAmplifier) {
        return true;
    }
}*/