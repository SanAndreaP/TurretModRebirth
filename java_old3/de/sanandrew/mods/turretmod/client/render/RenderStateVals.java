package de.sanandrew.mods.turretmod.client.render;

import net.minecraft.client.renderer.RenderState;

public final class RenderStateVals
        extends RenderState
{
    private RenderStateVals(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, setupTaskIn, clearTaskIn);
    }

    public static final TransparencyState LIGHTNING_TRANSPARENCY = RenderState.LIGHTNING_TRANSPARENCY;
    public static final WriteMaskState COLOR_DEPTH_WRITE = RenderState.COLOR_DEPTH_WRITE;
    public static final RenderState.ShadeModelState SHADE_ENABLED = RenderState.SHADE_ENABLED;
    public static final RenderState.LightmapState LIGHTMAP_DISABLED = RenderState.LIGHTMAP_DISABLED;
}
