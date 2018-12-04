/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model.entity;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class ModelTurretBase
		extends ModelBase
		implements ModelJsonHandler<ModelTurretBase, ModelJsonLoader.ModelJson>
{
	private ModelRenderer head;
	private ModelRenderer healthBar;
	private ModelRenderer ammoBar;

	private final ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> modelJson;
	private final float scale;

	@SuppressWarnings("SimplifyStreamApiCallChains")
	public ModelTurretBase(float scale) {
		this.scale = scale;
		this.modelJson = ModelJsonLoader.create(this, this.getModelLocation(), this.getMandatoryBoxes().stream().toArray(String[]::new));
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale, entity);

		if( this.modelJson.isLoaded() ) {
			Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> box.render(scale));
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);

        this.head.rotateAngleY = rotYaw / (180.0F / (float)Math.PI);
        this.head.rotateAngleX = rotPitch / (180.0F / (float)Math.PI);

        ITurretInst turret = (ITurretInst) entity;

		float maxHealth = turret.isInGui() ? 2.0F : turret.get().getMaxHealth();
		float health = turret.isInGui() ? 1.0F : turret.get().getHealth();
		int maxAmmo = turret.isInGui() ? 2 : turret.getTargetProcessor().getMaxAmmoCapacity();
		int ammo = turret.isInGui() ? 1 : turret.getTargetProcessor().getAmmoCount();

        this.healthBar.rotateAngleZ = -((float)Math.PI / 2.0F) * ((maxHealth - health) / maxHealth);
        this.ammoBar.rotateAngleZ = ((float)Math.PI / 2.0F) * ((maxAmmo - ammo) / (float) maxAmmo);
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase, ModelJsonLoader.ModelJson> loader) {
		loader.load();

		this.head = loader.getBox("head");
		this.healthBar = loader.getBox("healthBar");
		this.ammoBar = loader.getBox("ammoBar");
	}

	@Override
	public void setTexture(String s) { }

	@Override
	public float getBaseScale() {
		return this.scale;
	}

	List<String> getMandatoryBoxes() {
		return Arrays.asList("head", "healthBar", "ammoBar");
	}

	ResourceLocation getModelLocation() {
		return Resources.TURRET_T1_BASE_MODEL.resource;
	}
}
