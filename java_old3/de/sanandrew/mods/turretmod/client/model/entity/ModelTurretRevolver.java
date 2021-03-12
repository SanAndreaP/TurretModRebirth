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
import de.sanandrew.mods.turretmod.registry.turret.TurretRevolver;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretRevolver<E extends LivingEntity & ITurretInst>
		extends ModelTurretBase<E>
{
	private ModelRenderer barrelLeft;
	private ModelRenderer barrelRight;

	public ModelTurretRevolver() {
		super(Resources.TURRET_T2_REVOLVER_MODEL.resource);
	}

	@Override
	public List<String> getMandatoryBoxes() {
		return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrelLeft", "barrelRight")).collect(Collectors.toList());
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
		super.onReload(iResourceManager, loader);

		this.barrelLeft = loader.getBox("barrelLeft");
		this.barrelRight = loader.getBox("barrelRight");
	}

	@Override
	public void setLivingAnimations(@Nonnull E turretInst, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(turretInst, limbSwing, limbSwingAmount, partialTick);

		if( turretInst.getTurret() instanceof TurretRevolver ) {
            TurretRevolver.MyRAM ram = turretInst.getRAM(TurretRevolver.MyRAM::new);

			float barrelDeltaL = ram.prevBarrelLeft + (ram.barrelLeft - ram.prevBarrelLeft) * partialTick;
			float barrelDeltaR = ram.prevBarrelRight + (ram.barrelRight - ram.prevBarrelRight) * partialTick;

			this.barrelLeft.rotationPointZ = 3.0F - 3.0F * barrelDeltaL;
			this.barrelRight.rotationPointZ = 3.0F - 3.0F * barrelDeltaR;
		}
	}
}
