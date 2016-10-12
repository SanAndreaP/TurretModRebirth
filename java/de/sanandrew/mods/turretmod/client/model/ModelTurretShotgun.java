/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretShotgun
		extends ModelTurretBase
{
	public ModelRenderer barrel;

	public ModelTurretShotgun(float scale) {
		super(scale);
	}

	@Override
	public List<String> getMandatoryBoxes() {
		return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrel")).collect(Collectors.toList());
	}

	@Override
	public ResourceLocation getModelLocation() {
		return Resources.TURRET_T1_SHOTGUN_MODEL.getResource();
	}

    @Override
    public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> loader) {
        super.onReload(iResourceManager, loader);

        this.barrel = loader.getBox("barrel");
    }

    @Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partTicks) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partTicks);

		if( entity instanceof EntityTurretShotgun ) {
			EntityTurretShotgun shotgun = (EntityTurretShotgun) entity;
			float barrelDelta = shotgun.prevBarrelPos + (shotgun.barrelPos - shotgun.prevBarrelPos) * partTicks;
			this.barrel.rotationPointZ = 3.0F - 3.0F * barrelDelta;
		}
	}
}
