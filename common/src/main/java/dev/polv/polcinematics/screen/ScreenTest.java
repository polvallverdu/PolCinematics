package dev.polv.polcinematics.screen;

import dev.polv.polcinematics.client.players.BrowserView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

public class ScreenTest {

    public static Vec3d POS = new Vec3d(0, 0, 0);

    private static BrowserView browserView = null;

    public static void create(Vec3d pos) {
        POS = pos;
        if (browserView == null)
            browserView = new BrowserView("https://google.com");
    }

    public static void render(MatrixStack poseStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity cameraEntity = mc.getCameraEntity();

        if (cameraEntity == null || browserView == null)
            return;

        poseStack.push();


        Vec3d view = mc.gameRenderer.getCamera().getPos();
        poseStack.translate(POS.x - view.x, POS.y - view.y, POS.z - view.z);

        Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
        matrix4f.rotate(new AxisAngle4f((float) Math.toRadians(180), 0, 1, 0));

        browserView.renderWorld(matrix4f, 0, 0, 50, 30);

        poseStack.pop();
    }

//    public static void render(MatrixStack poseStack) {
//        MinecraftClient mc = MinecraftClient.getInstance();
//        Entity cameraEntity = mc.getCameraEntity();
//
//        if (cameraEntity == null || browserView == null)
//            return;
//
//        poseStack.push();
//
//
//        Vec3d view = mc.gameRenderer.getCamera().getPos();
//        poseStack.translate(POS.x - view.x, POS.y - view.y, POS.z - view.z);
//
//        Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
//        VertexConsumerProvider.Immediate renderTypeBuffer = mc.getBufferBuilders().getEntityVertexConsumers();
//
//        RenderLayer bulletType = ScreenRenderType.getScreenRenderLayer();
//        VertexConsumer builder = renderTypeBuffer.getBuffer(bulletType);
//        builder.vertex(matrix4f, 10, 0, -10).texture(1f, 0f).color(red, green, blue, alpha).next();
//        builder.vertex(matrix4f, -10, 0, -10).texture(1f, 0f).color(red, green, blue, alpha).next();
//        builder.vertex(matrix4f, -10, 0, 10).texture(1f, 0f).color(red, green, blue, alpha).next();
//        builder.vertex(matrix4f, 10, 0, 10).texture(1f, 0f).color(red, green, blue, alpha).next();
//
//        MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw(bulletType);
//        poseStack.pop();
//    }

}
