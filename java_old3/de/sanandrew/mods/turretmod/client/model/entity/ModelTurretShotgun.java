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
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelTurretShotgun<E extends LivingEntity & ITurretInst>
		extends ModelTurretBase<E>
{
	private ModelRenderer barrel;

	public ModelTurretShotgun() {
		super(Resources.TURRET_T1_SHOTGUN_MODEL.resource);
	}

	@Override
	public List<String> getMandatoryBoxes() {
		return Stream.concat(super.getMandatoryBoxes().stream(), Stream.of("barrel")).collect(Collectors.toList());
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
		super.onReload(iResourceManager, loader);

        this.barrel = loader.getBox("barrel");
    }

	@Override
	public void setLivingAnimations(@Nonnull E turretInst, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(turretInst, limbSwing, limbSwingAmount, partialTick);

		if( turretInst.getTurret() instanceof TurretShotgun ) {
			TurretShotgun.MyRAM ram = turretInst.getRAM(TurretShotgun.MyRAM::new);

			float barrelDelta = ram.prevBarrelPos + (ram.barrelPos - ram.prevBarrelPos) * partialTick;
			this.barrel.rotationPointZ = 3.0F - 3.0F * barrelDelta;
		}
	}
}
