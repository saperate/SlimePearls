package net.fabricmc.slimepearls.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import static net.fabricmc.slimepearls.main.SLIMEPEARLITEM;;

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
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) + 180.0F));
        itemRenderer.renderItem(
            STACK,
            ModelTransformation.Mode.FIXED,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumers,
            0
        );
        matrices.pop();
    }

    @Override
    public Identifier getTexture(SlimePearlEntity entity) {
        // Return the texture identifier for your entity
        return new Identifier("sapswackystuff", "textures/entity/slime_pearl.png");
    }

}
