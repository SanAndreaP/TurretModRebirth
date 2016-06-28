/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelTurretRevolver
		extends ModelBase
{
	public ModelRenderer turretBase;
	public ModelRenderer turretHead;
	public ModelRenderer turretThroat;
	public ModelRenderer healthBar;
	public ModelRenderer ammoBar;
	public ModelRenderer barrelLeft;
	public ModelRenderer barrelRight;

	public ModelTurretRevolver(float scale) {
		this.textureWidth = 128;
		this.textureHeight = 64;

		this.turretBase = new ModelRenderer(this, 0, 49);
		this.turretBase.setTextureSize(128, 64);
		this.turretBase.addBox(-7.0F, 23.0F, -7.0F, 14, 1, 14, scale);

		this.turretThroat = new ModelRenderer(this, 0, 16);
		this.turretThroat.setTextureSize(128, 64);
		this.turretThroat.addBox(-1.0F, 0.0F, -1.0F, 2, 20, 2, scale);

		this.turretHead = new ModelRenderer(this, 0, 0);
		this.turretHead.setTextureSize(128, 64);
		this.turretHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, scale);

		this.healthBar = new ModelRenderer(this, 12, 21);
		this.healthBar.setRotationPoint(-2.0F, 19.0F, 0.0F);
		this.healthBar.setTextureSize(128, 64);
		this.healthBar.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);

		this.ammoBar = new ModelRenderer(this, 12, 27);
		this.ammoBar.setRotationPoint(2.0F, 19.0F, 0.0F);
		this.ammoBar.setTextureSize(128, 64);
		this.ammoBar.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);

        this.barrelLeft = new ModelRenderer(this, 32, 0);
        this.barrelLeft.addBox(1.0F, 1.0F, -10.0F, 1, 1, 8, scale);
        this.barrelLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.barrelLeft.setTextureSize(128, 64);
		this.turretHead.addChild(this.barrelLeft);

        this.barrelRight = new ModelRenderer(this, 32, 0);
        this.barrelRight.addBox(-2.0F, 1.0F, -10.0F, 1, 1, 8, scale);
        this.barrelRight.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.barrelRight.setTextureSize(128, 64);
		this.turretHead.addChild(this.barrelRight);

		ModelRenderer turretFeetII = new ModelRenderer(this, 0, 36);
		turretFeetII.setTextureSize(128, 64);
		turretFeetII.addBox(-6.0F, 22.0F, -6.0F, 12, 1, 12, scale);
		this.turretBase.addChild(turretFeetII);

		ModelRenderer turretFeetIII = new ModelRenderer(this, 12, 25);
		turretFeetIII.setTextureSize(128, 64);
		turretFeetIII.addBox(-5.0F, 21.0F, -5.0F, 10, 1, 10, scale);
		this.turretBase.addChild(turretFeetIII);

		ModelRenderer turretFeetIV = new ModelRenderer(this, 16, 16);
		turretFeetIV.setTextureSize(128, 64);
		turretFeetIV.addBox(-4.0F, 20.0F, -4.0F, 8, 1, 8, scale);
		this.turretBase.addChild(turretFeetIV);

		ModelRenderer turretFeetV = new ModelRenderer(this, 32, 9);
		turretFeetV.setTextureSize(128, 64);
		turretFeetV.addBox(-3.0F, 19.0F, -3.0F, 6, 1, 6, scale);
		this.turretBase.addChild(turretFeetV);

		float angle = (float) Math.PI / 60.0F;
		ModelRenderer turretThroatI = new ModelRenderer(this, 8, 16);
		turretThroatI.setTextureSize(128, 64);
		turretThroatI.addBox(0.0F, 0.0F, 0.0F, 1, 20, 1, scale);
		setRotation(turretThroatI, angle, 0.0F, -angle);
		this.turretThroat.addChild(turretThroatI);

		ModelRenderer turretThroatII = new ModelRenderer(this, 8, 16);
		turretThroatII.setTextureSize(128, 64);
		turretThroatII.addBox(-1.0F, 0.0F, 0.0F, 1, 20, 1, scale);
		setRotation(turretThroatII, angle, 0.0F, angle);
		this.turretThroat.addChild(turretThroatII);

		ModelRenderer turretThroatIII = new ModelRenderer(this, 8, 16);
		turretThroatIII.setTextureSize(128, 64);
		turretThroatIII.addBox(0.0F, 0.0F, -1.0F, 1, 20, 1, scale);
		setRotation(turretThroatIII, -angle, 0.0F, -angle);
		this.turretThroat.addChild(turretThroatIII);

		ModelRenderer turretThroatIV = new ModelRenderer(this, 8, 16);
		turretThroatIV.setTextureSize(128, 64);
		turretThroatIV.addBox(-1.0F, 0.0F, -1.0F, 1, 20, 1, scale);
		setRotation(turretThroatIV, -angle, 0.0F, angle);
		this.turretThroat.addChild(turretThroatIV);

		ModelRenderer turretAntennaI = new ModelRenderer(this, 14, 15);
		turretAntennaI.setTextureSize(128, 64);
		turretAntennaI.addBox(0.0F, -8.0F, -0.5F, 0, 5, 1, scale);
		this.turretHead.addChild(turretAntennaI);

		ModelRenderer turretAntennaII = new ModelRenderer(this, 12, 16);
		turretAntennaII.setTextureSize(128, 64);
		turretAntennaII.addBox(-0.5F, -8.0F, 0.0F, 1, 5, 0, scale);
		this.turretHead.addChild(turretAntennaII);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale);
		this.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale, entity);

		this.turretBase.render(scale);
		this.turretHead.render(scale);
		this.turretThroat.render(scale);
		this.healthBar.render(scale);
		this.ammoBar.render(scale);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partTicks) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partTicks);

		if( entity instanceof EntityTurretRevolver ) {
			EntityTurretRevolver shotgun = (EntityTurretRevolver) entity;
			float barrelDeltaL = shotgun.prevBarrelPosLeft + (shotgun.barrelPosLeft - shotgun.prevBarrelPosLeft) * partTicks;
			float barrelDeltaR = shotgun.prevBarrelPosRight + (shotgun.barrelPosRight - shotgun.prevBarrelPosRight) * partTicks;
			this.barrelLeft.rotationPointZ = 3.0F - 3.0F * barrelDeltaL;
			this.barrelRight.rotationPointZ = 3.0F - 3.0F * barrelDeltaR;
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale, entity);

        this.turretHead.rotateAngleY = rotYaw / (180.0F / (float)Math.PI);
        this.turretHead.rotateAngleX = rotPitch / (180.0F / (float)Math.PI);

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
}
