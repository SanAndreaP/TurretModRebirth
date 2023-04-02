/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret;

import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.client.renderer.turret.layer.LayerTurretUpgrades;
import dev.sanandrea.mods.turretmod.client.renderer.turret.layer.TurretGlowLayer;
import dev.sanandrea.mods.turretmod.client.renderer.turret.layer.TurretRangeLayer;
import dev.sanandrea.mods.turretmod.client.renderer.turret.layer.TurretShieldLayer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TurretRenderBase<T extends LivingEntity & ITurretEntity, M extends EntityModel<T>>
        extends LivingRenderer<T, M>
{

    public TurretRenderBase(EntityRendererManager manager, Supplier<M> modelFactory) {
        super(manager, modelFactory.get(), 0.5F);

        this.addTurretLayers();
    }

    protected void addTurretLayers() {
        this.addLayer(new LayerTurretUpgrades<>(this));
        this.addLayer(new TurretGlowLayer<>(this));
        this.addLayer(new TurretRangeLayer<>(this));
        this.addLayer(new TurretShieldLayer<>(this));
    }

    @Override
    protected boolean shouldShowName(@Nonnull T entity) {
        return false;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return entity.getDelegate().getBaseTexture(entity);
    }
}
