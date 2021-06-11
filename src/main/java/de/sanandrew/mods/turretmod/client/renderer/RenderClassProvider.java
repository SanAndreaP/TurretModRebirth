package de.sanandrew.mods.turretmod.client.renderer;

import de.sanandrew.mods.turretmod.client.renderer.projectile.CrossbowBoltRenderer;
import de.sanandrew.mods.turretmod.init.IRenderClassProvider;

public class RenderClassProvider
        implements IRenderClassProvider
{
    public static final RenderClassProvider INSTANCE = new RenderClassProvider();

    private RenderClassProvider() { }

    @Override
    public String getCrossbowBoltRenderClass() {
        return CrossbowBoltRenderer.class.getName();
    }
}
