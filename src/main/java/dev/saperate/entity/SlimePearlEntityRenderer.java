package dev.saperate.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static dev.saperate.WackyPearls.SLIMEPEARLITEM;

public class SlimePearlEntityRenderer extends EntityRenderer<SlimePearlEntity> {

    private final ItemRenderer itemRenderer;

    public static final ItemStack STACK = new ItemStack(SLIMEPEARLITEM);


    public SlimePearlEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }


    @Override
    public void render(SlimePearlEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        //Render the item

        matrices.translate(0.0D, 0.15D, 0.0D); // Adjust the position as needed
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(0.5F, 0.5F, 0.5F); // Adjust the scale as needed

        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.world, entity.getId());

        matrices.pop();
    }

    @Override
    public Identifier getTexture(SlimePearlEntity entity) {
        // Return the texture identifier for your entity
        return new Identifier("sapswackystuff", "textures/entity/slime_pearl.png");
    }

}
