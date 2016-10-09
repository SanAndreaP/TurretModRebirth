/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;

import java.util.Arrays;

public class ModelTurretCrossbow
		extends ModelBase
		implements ModelJsonHandler<ModelTurretCrossbow, ModelJsonLoader.ModelJson>
{
	public ModelRenderer base;
	public ModelRenderer head;
	public ModelRenderer throat;
	public ModelRenderer healthBar;
	public ModelRenderer ammoBar;

	private ModelJsonLoader<ModelTurretCrossbow, ModelJsonLoader.ModelJson> modelJson;
	private final float scale;

	public ModelTurretCrossbow(float scale) {
		this.modelJson = ModelJsonLoader.create(this, Resources.TURRET_T1_CROSSBOW_MODEL.getResource(), "base", "head", "throat", "healthBar", "ammoBar");
		this.scale = scale;
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

        EntityTurret turret = (EntityTurret)entity;

		float maxHealth = turret.inGui ? 2.0F : turret.getMaxHealth();
		float health = turret.inGui ? 1.0F : turret.getHealth();
		int maxAmmo = turret.inGui ? 2 : turret.getTargetProcessor().getMaxAmmoCapacity();
		int ammo = turret.inGui ? 1 : turret.getTargetProcessor().getAmmoCount();

        this.healthBar.rotateAngleZ = -((float)Math.PI / 2.0F) * ((maxHealth - health) / maxHealth);
        this.ammoBar.rotateAngleZ = ((float)Math.PI / 2.0F) * ((maxAmmo - ammo) / (float) maxAmmo);
	}

	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretCrossbow, ModelJsonLoader.ModelJson> loader) {
		loader.addCustomModelRenderer("antennaI", ModelRendererCulled.class);
		loader.addCustomModelRenderer("antennaII", ModelRendererCulled.class);

		loader.load();

		this.base = loader.getBox("base");
		this.throat = loader.getBox("throat");
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
}
