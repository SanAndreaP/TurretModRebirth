package de.sanandrew.mods.turretmod.api.client.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretRender;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface ITurretRenerRegistry
{
    <T extends ModelBase> boolean registerRender(@Nonnull ITurret turret, @Nonnull ITurretRender<T> render);

    ITurretRender<?> removeRender(ITurret turret);

    <T extends ModelBase> void addStandardLayers(List<LayerRenderer<?>> layerList, ITurretRender<T> render);

    RenderLivingBase<? extends EntityLiving> getRenderer();
}
