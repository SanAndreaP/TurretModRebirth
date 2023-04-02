/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretShotgun<E extends LivingEntity & ITurretEntity>
		extends ModelTurretBase<E>
{
	private ModelRenderer barrel;

	@Override
	public List<String> getMandatoryBoxes() {
		return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrel")).collect(Collectors.toList());
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
		super.onReload(iResourceManager, loader);

        this.barrel = loader.getBox("barrel");
    }

	//TODO: reimplement shotgun
	@Override
	public void prepareMobModel(@Nonnull E turretInst, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(turretInst, limbSwing, limbSwingAmount, partialTick);

//		if( turretInst.getTurret() instanceof TurretShotgun ) {
//			TurretShotgun.MyRAM ram = turretInst.getRAM(TurretShotgun.MyRAM::new);
//
//			float barrelDelta = ram.prevBarrelPos + (ram.barrelPos - ram.prevBarrelPos) * partialTick;
//			this.barrel.rotationPointZ = 3.0F - 3.0F * barrelDelta;
//		}
	}
}
