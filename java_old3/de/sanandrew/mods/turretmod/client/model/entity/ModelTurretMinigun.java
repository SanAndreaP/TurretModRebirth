/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretMinigun<E extends LivingEntity & ITurretInst>
        extends ModelTurretBase<E>
{
    private ModelRenderer barrelBaseLeft;
    private ModelRenderer barrelBaseRight;

    public ModelTurretMinigun() {
        super(Resources.TURRET_T2_MINIGUN_MODEL.resource);
    }

    @Override
    public List<String> getMandatoryBoxes() {
        return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrelBaseLeft", "barrelBaseRight")).collect(Collectors.toList());
    }

    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
        super.onReload(iResourceManager, loader);

        this.barrelBaseLeft = loader.getBox("barrelBaseLeft");
        this.barrelBaseRight = loader.getBox("barrelBaseRight");
    }

    @Override
    public void setLivingAnimations(@Nonnull E turretInst, float limbSwing, float limbSwingAmount, float partTicks) {
        super.setLivingAnimations(turretInst, limbSwing, limbSwingAmount, partTicks);

        if( turretInst.getTurret() instanceof TurretMinigun ) {
            TurretMinigun.MyRAM ram = turretInst.getRAM(TurretMinigun.MyRAM::new);

            if( ram != null ) {
                float barrelDeltaL = ram.prevBarrelLeft + (ram.barrelLeft - ram.prevBarrelLeft) * partTicks;
                float barrelDeltaR = ram.prevBarrelRight + (ram.barrelRight - ram.prevBarrelRight) * partTicks;

                this.barrelBaseLeft.rotateAngleZ = barrelDeltaL / 180.0F * (float) Math.PI;
                this.barrelBaseRight.rotateAngleZ = barrelDeltaR / 180.0F * (float) Math.PI;
            }
        }
    }
}
