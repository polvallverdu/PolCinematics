package dev.polv.polcinematics.screen;

import dev.polv.polcinematics.PolCinematics;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class ScreenRenderType extends RenderLayer {

    private static final RenderLayer SCREEN_RENDER_LAYER = RenderLayer.of(PolCinematics.MOD_ID + ":screen",
            VertexFormats.POSITION_TEXTURE_COLOR,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            false,
            MultiPhaseParameters
                    .builder()
                    .program(RenderPhase.POSITION_TEXTURE_PROGRAM)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                    .transparency(RenderPhase.ADDITIVE_TRANSPARENCY)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                    .build(false)
    );

    public ScreenRenderType(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static RenderLayer getScreenRenderLayer() {
        return SCREEN_RENDER_LAYER;
    }

}
