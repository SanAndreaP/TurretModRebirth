package de.sanandrew.mods.turretmod.client.renderer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import javax.annotation.Nonnull;
import java.util.OptionalDouble;

public class TmrRenderTypes
        extends RenderType
{
    public static final RenderType TMR_LIGHTNING = lightning();

    @SuppressWarnings({"ConstantConditions", "java:S4449"})
    private TmrRenderTypes() {
        super("", null, 0, 0, false, false, null, null);

        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static RenderType tmrLine(final float width) {
        return RenderType.create(String.format("tmr_lines_%.5f", width), DefaultVertexFormats.POSITION_COLOR, 1, 256,
                                 RenderType.State.builder().setLineState(new RenderState.LineState(OptionalDouble.of(width)))
                                                 .setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                                 .setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    }

    @Nonnull
    private static RenderType tmrLightning() {
        return RenderType.create("tmr_lightning", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true,
                                 RenderType.State.builder().setWriteMaskState(COLOR_DEPTH_WRITE)
                                                 .setTransparencyState(LIGHTNING_TRANSPARENCY)
                                                 .setShadeModelState(SMOOTH_SHADE)
                                                 .setLightmapState(NO_LIGHTMAP)
                                                 .createCompositeState(false));
    }
}
