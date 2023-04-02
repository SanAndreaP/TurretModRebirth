/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretRevolver<E extends LivingEntity & ITurretEntity>
		extends ModelTurretBase<E>
{
	private ModelRenderer barrelLeft;
	private ModelRenderer barrelRight;

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

	//TODO: reimplement revolver
	@Override
	public void prepareMobModel(@Nonnull E turretInst, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(turretInst, limbSwing, limbSwingAmount, partialTick);

//		if( turretInst.getTurret() instanceof TurretRevolver ) {
//            TurretRevolver.MyRAM ram = turretInst.getRAM(TurretRevolver.MyRAM::new);
//
//			float barrelDeltaL = ram.prevBarrelLeft + (ram.barrelLeft - ram.prevBarrelLeft) * partialTick;
//			float barrelDeltaR = ram.prevBarrelRight + (ram.barrelRight - ram.prevBarrelRight) * partialTick;
//
//			this.barrelLeft.rotationPointZ = 3.0F - 3.0F * barrelDeltaL;
//			this.barrelRight.rotationPointZ = 3.0F - 3.0F * barrelDeltaR;
//		}
	}
}
