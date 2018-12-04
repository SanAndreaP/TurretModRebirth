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
import de.sanandrew.mods.turretmod.registry.turret.TurretRevolver;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretRevolver
		extends ModelTurretBase
{
	private ModelRenderer barrelLeft;
	private ModelRenderer barrelRight;

	public ModelTurretRevolver(float scale) {
		super(scale);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partTicks) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partTicks);

        ITurretInst turretInst = (ITurretInst) entity;
		if( turretInst.getTurret() instanceof TurretRevolver ) {
            TurretRevolver.MyRAM ram = turretInst.getRAM(TurretRevolver.MyRAM::new);

			float barrelDeltaL = ram.prevBarrelLeft + (ram.barrelLeft - ram.prevBarrelLeft) * partTicks;
			float barrelDeltaR = ram.prevBarrelRight + (ram.barrelRight - ram.prevBarrelRight) * partTicks;

			this.barrelLeft.rotationPointZ = 3.0F - 3.0F * barrelDeltaL;
			this.barrelRight.rotationPointZ = 3.0F - 3.0F * barrelDeltaR;
		}
	}

	@Override
	public List<String> getMandatoryBoxes() {
		return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrelLeft", "barrelRight")).collect(Collectors.toList());
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> loader) {
		super.onReload(iResourceManager, loader);

		this.barrelLeft = loader.getBox("barrelLeft");
		this.barrelRight = loader.getBox("barrelRight");
	}

	@Override
	public ResourceLocation getModelLocation() {
		return Resources.TURRET_T2_REVOLVER_MODEL.resource;
	}
}
