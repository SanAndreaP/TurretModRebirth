package de.sanandrew.mods.turretmod.client.util;

import net.minecraft.client.renderer.RenderState;

public abstract class RenderStateAccessor
        extends RenderState
{
    public static final RenderState.WriteMaskState COLOR_DEPTH_WRITE = RenderState.COLOR_DEPTH_WRITE;
    public static final RenderState.TransparencyState LIGHTNING_TRANSPARENCY = RenderState.LIGHTNING_TRANSPARENCY;
    public static final RenderState.ShadeModelState SMOOTH_SHADE = RenderState.SMOOTH_SHADE;
    public static final LightmapState NO_LIGHTMAP = RenderState.NO_LIGHTMAP;

    private RenderStateAccessor(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
    }
}
