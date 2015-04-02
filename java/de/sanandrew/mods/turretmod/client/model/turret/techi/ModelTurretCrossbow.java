/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model.turret.techi;


import de.sanandrew.mods.turretmod.client.model.turret.IModelTurret;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelTurretCrossbow
		extends ModelBase
		implements IModelTurret
{
	public ModelRenderer turretFeetI;
	public ModelRenderer turretFeetII;
	public ModelRenderer turretFeetIII;
	public ModelRenderer turretFeetIV;
	public ModelRenderer turretFeetV;
	public ModelRenderer turretHead;
	public ModelRenderer turretThroatI;
	public ModelRenderer turretThroatII;
	public ModelRenderer turretThroatIII;
	public ModelRenderer turretThroatIV;
	public ModelRenderer turretThroatV;
	public ModelRenderer turretAntennaI;
	public ModelRenderer turretAntennaII;
	public ModelRenderer healthBar;
	public ModelRenderer ammoBar;

	public ModelTurretCrossbow(float scale) {
		textureWidth = 128;
		textureHeight = 64;

		turretFeetI = new ModelRenderer(this, 0, 49);
		turretFeetI.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretFeetI.setTextureSize(128, 64);
		turretFeetI.addBox(-7.0F, 23.0F, -7.0F, 14, 1, 14, scale);
		setRotation(turretFeetI, 0.0F, 0.0F, 0.0F);
		turretFeetII = new ModelRenderer(this, 0, 36);
		turretFeetII.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretFeetII.setTextureSize(128, 64);
		turretFeetII.addBox(-6.0F, 22.0F, -6.0F, 12, 1, 12, scale);
		setRotation(turretFeetII, 0.0F, 0.0F, 0.0F);
		turretFeetIII = new ModelRenderer(this, 12, 25);
		turretFeetIII.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretFeetIII.setTextureSize(128, 64);
		turretFeetIII.addBox(-5.0F, 21.0F, -5.0F, 10, 1, 10, scale);
		setRotation(turretFeetIII, 0.0F, 0.0F, 0.0F);
		turretFeetIV = new ModelRenderer(this, 16, 16);
		turretFeetIV.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretFeetIV.setTextureSize(128, 64);
		turretFeetIV.addBox(-4.0F, 20.0F, -4.0F, 8, 1, 8, scale);
		setRotation(turretFeetIV, 0.0F, 0.0F, 0.0F);
		turretFeetV = new ModelRenderer(this, 32, 9);
		turretFeetV.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretFeetV.setTextureSize(128, 64);
		turretFeetV.addBox(-3.0F, 19.0F, -3.0F, 6, 1, 6, scale);
		setRotation(turretFeetV, 0.0F, 0.0F, 0.0F);
		turretHead = new ModelRenderer(this, 0, 0);
		turretHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretHead.setTextureSize(128, 64);
		turretHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, scale);
		setRotation(turretHead, 0.0F, 0.0F, 0.0F);
		turretThroatI = new ModelRenderer(this, 8, 16);
		turretThroatI.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretThroatI.setTextureSize(128, 64);
		turretThroatI.addBox(0.0F, 0.0F, 0.0F, 1, 20, 1, scale);
		setRotation(turretThroatI, 0.0523599F, 0.0F, -0.0523599F);
		turretThroatII = new ModelRenderer(this, 8, 16);
		turretThroatII.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretThroatII.setTextureSize(128, 64);
		turretThroatII.addBox(-1.0F, 0.0F, 0.0F, 1, 20, 1, scale);
		setRotation(turretThroatII, 0.0523599F, 0.0F, 0.0523599F);
		turretThroatIII = new ModelRenderer(this, 8, 16);
		turretThroatIII.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretThroatIII.setTextureSize(128, 64);
		turretThroatIII.addBox(0.0F, 0.0F, -1.0F, 1, 20, 1, scale);
		setRotation(turretThroatIII, -0.0523599F, 0.0F, -0.0523599F);
		turretThroatIV = new ModelRenderer(this, 8, 16);
		turretThroatIV.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretThroatIV.setTextureSize(128, 64);
		turretThroatIV.addBox(-1.0F, 0.0F, -1.0F, 1, 20, 1, scale);
		setRotation(turretThroatIV, -0.0523599F, 0.0F, 0.0523599F);
		turretThroatV = new ModelRenderer(this, 0, 16);
		turretThroatV.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretThroatV.setTextureSize(128, 64);
		turretThroatV.addBox(-1.0F, 0.0F, -1.0F, 2, 20, 2, scale);
		setRotation(turretThroatV, 0.0F, 0.0F, 0.0F);
		turretAntennaI = new ModelRenderer(this, 14, 15);
		turretAntennaI.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretAntennaI.setTextureSize(128, 64);
		turretAntennaI.addBox(0.0F, -8.0F, -0.5F, 0, 5, 1, scale);
		setRotation(turretAntennaI, 0.0F, 0.0F, 0.0F);
		turretAntennaII = new ModelRenderer(this, 12, 16);
		turretAntennaII.setRotationPoint(0.0F, 0.0F, 0.0F);
		turretAntennaII.setTextureSize(128, 64);
		turretAntennaII.addBox(-0.5F, -8.0F, 0.0F, 1, 5, 0, scale);
		setRotation(turretAntennaII, 0.0F, 0.0F, 0.0F);
		healthBar = new ModelRenderer(this, 12, 21);
		healthBar.setRotationPoint(-2.0F, 19.0F, 0.0F);
		healthBar.setTextureSize(128, 64);
		healthBar.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);
		setRotation(healthBar, 0.0F, 0.0F, 0.0F);
		ammoBar = new ModelRenderer(this, 12, 27);
		ammoBar.setRotationPoint(2.0F, 19.0F, 0.0F);
		ammoBar.setTextureSize(128, 64);
		ammoBar.addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1, scale);
		setRotation(ammoBar, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void renderBase() {
		turretFeetI.render(0.0625F);
		turretFeetII.render(0.0625F);
		turretFeetIII.render(0.0625F);
		turretFeetIV.render(0.0625F);
		turretFeetV.render(0.0625F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		turretFeetI.render(f5);
		turretFeetII.render(f5);
		turretFeetIII.render(f5);
		turretFeetIV.render(f5);
		turretFeetV.render(f5);
		turretHead.render(f5);
		turretThroatI.render(f5);
		turretThroatII.render(f5);
		turretThroatIII.render(f5);
		turretThroatIV.render(f5);
		turretThroatV.render(f5);
		turretAntennaI.render(f5);
		turretAntennaII.render(f5);

		GL11.glPushMatrix();
//		if( this.isGlowTexture ) {
//			GL11.glScalef(1.1F, 1.1F, 1.1F);
//			GL11.glTranslatef(0.010F, -0.11F, -0.00F);
//		}
		healthBar.render(f5);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
//		if (this.isGlowTexture) {
//			GL11.glScalef(1.1F, 1.1F, 1.1F);
//			GL11.glTranslatef(-0.010F, -0.11F, -0.00F);
//		}
		ammoBar.render(f5);
		GL11.glPopMatrix();
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.turretHead.rotateAngleY = this.turretAntennaI.rotateAngleY = this.turretAntennaII.rotateAngleY = f3 / (180.0F / (float)Math.PI);
        this.turretHead.rotateAngleX = this.turretAntennaI.rotateAngleX = this.turretAntennaII.rotateAngleX = f4 / (180.0F / (float)Math.PI);

        setStaticBody(f3);

        AEntityTurretBase turret = (AEntityTurretBase)entity;

        float healthRot = -((float)Math.PI / 2.0F) * ((turret.getMaxHealth() - turret.getHealth()) / turret.getMaxHealth());
        this.healthBar.rotateAngleZ = healthRot;
//        float ammoRot = ((float)Math.PI / 2.0F) * ((float)(turret.getMaxAmmo() - turret.getAmmo()) / (float)turret.getMaxAmmo());

//        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
//        	ammoRot = 0.0F;
//        }
//        this.ammoBar.rotateAngleZ = ammoRot;
	}

	private void setStaticBody(float f) {
		turretFeetI.rotateAngleY = 0;
		turretFeetII.rotateAngleY = 0;
		turretFeetIII.rotateAngleY = 0;
		turretFeetIV.rotateAngleY = 0;
		turretFeetV.rotateAngleY = 0;
		turretThroatI.rotateAngleY = 0;
		turretThroatII.rotateAngleY = 0;
		turretThroatIII.rotateAngleY = 0;
		turretThroatIV.rotateAngleY = 0;
		turretThroatV.rotateAngleY = 0;
		healthBar.rotateAngleY = 0;
		ammoBar.rotateAngleY = 0;
	}

	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
