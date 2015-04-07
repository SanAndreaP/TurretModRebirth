package de.sanandrew.mods.turretmod.client.model.turret.techii;

import de.sanandrew.mods.turretmod.client.model.turret.IModelTurret;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelTurretRevolver
		extends ModelBase
		implements IModelTurret
{
	public ModelRenderer TurretFeetI;
	public ModelRenderer TurretFeetII;
	public ModelRenderer TurretFeetIII;
	public ModelRenderer TurretFeetIV;
	public ModelRenderer TurretFeetV;
	public ModelRenderer TurretHead;
	public ModelRenderer TurretThroatI;
	public ModelRenderer TurretThroatII;
	public ModelRenderer TurretThroatIII;
	public ModelRenderer TurretThroatIV;
	public ModelRenderer TurretThroatV;
	public ModelRenderer TurretAntennaI;
	public ModelRenderer TurretAntennaII;
	public ModelRenderer HealthBar;
	public ModelRenderer AmmoBar;
	public ModelRenderer LeftBarrel;
	public ModelRenderer RightBarrel;

	public ModelTurretRevolver(float scale) {
		textureWidth = 128;
		textureHeight = 64;

		TurretFeetI = new ModelRenderer(this, 0, 49);
		TurretFeetI.addBox(-7F, 23F, -7F, 14, 1, 14, scale);
		TurretFeetI.setRotationPoint(0F, 0F, 0F);
		TurretFeetI.setTextureSize(128, 64);
		setRotation(TurretFeetI, 0F, 0F, 0F);
		TurretFeetII = new ModelRenderer(this, 0, 36);
		TurretFeetII.addBox(-6F, 22F, -6F, 12, 1, 12, scale);
		TurretFeetII.setRotationPoint(0F, 0F, 0F);
		TurretFeetII.setTextureSize(128, 64);
		setRotation(TurretFeetII, 0F, 0F, 0F);
		TurretFeetIII = new ModelRenderer(this, 12, 25);
		TurretFeetIII.addBox(-5F, 21F, -5F, 10, 1, 10, scale);
		TurretFeetIII.setRotationPoint(0F, 0F, 0F);
		TurretFeetIII.setTextureSize(128, 64);
		setRotation(TurretFeetIII, 0F, 0F, 0F);
		TurretFeetIV = new ModelRenderer(this, 16, 16);
		TurretFeetIV.addBox(-4F, 20F, -4F, 8, 1, 8, scale);
		TurretFeetIV.setRotationPoint(0F, 0F, 0F);
		TurretFeetIV.setTextureSize(128, 64);
		setRotation(TurretFeetIV, 0F, 0F, 0F);
		TurretFeetV = new ModelRenderer(this, 32, 9);
		TurretFeetV.addBox(-3F, 19F, -3F, 6, 1, 6, scale);
		TurretFeetV.setRotationPoint(0F, 0F, 0F);
		TurretFeetV.setTextureSize(128, 64);
		setRotation(TurretFeetV, 0F, 0F, 0F);
		TurretHead = new ModelRenderer(this, 0, 0);
		TurretHead.addBox(-4F, -4F, -4F, 8, 8, 8, scale);
		TurretHead.setRotationPoint(0F, 0F, 0F);
		TurretHead.setTextureSize(128, 64);
		setRotation(TurretHead, 0F, 0F, 0F);
		TurretThroatI = new ModelRenderer(this, 8, 16);
		TurretThroatI.addBox(0F, 0F, 0F, 1, 20, 1, scale);
		TurretThroatI.setRotationPoint(0F, 0F, 0F);
		TurretThroatI.setTextureSize(128, 64);
		setRotation(TurretThroatI, 0.0523599F, 0F, -0.0523599F);
		TurretThroatII = new ModelRenderer(this, 8, 16);
		TurretThroatII.addBox(-1F, 0F, 0F, 1, 20, 1, scale);
		TurretThroatII.setRotationPoint(0F, 0F, 0F);
		TurretThroatII.setTextureSize(128, 64);
		setRotation(TurretThroatII, 0.0523599F, 0F, 0.0523599F);
		TurretThroatIII = new ModelRenderer(this, 8, 16);
		TurretThroatIII.addBox(0F, 0F, -1F, 1, 20, 1, scale);
		TurretThroatIII.setRotationPoint(0F, 0F, 0F);
		TurretThroatIII.setTextureSize(128, 64);
		setRotation(TurretThroatIII, -0.0523599F, 0F, -0.0523599F);
		TurretThroatIV = new ModelRenderer(this, 8, 16);
		TurretThroatIV.addBox(-1F, 0F, -1F, 1, 20, 1, scale);
		TurretThroatIV.setRotationPoint(0F, 0F, 0F);
		TurretThroatIV.setTextureSize(128, 64);
		setRotation(TurretThroatIV, -0.0523599F, 0F, 0.0523599F);
		TurretThroatV = new ModelRenderer(this, 0, 16);
		TurretThroatV.addBox(-1F, 0F, -1F, 2, 20, 2, scale);
		TurretThroatV.setRotationPoint(0F, 0F, 0F);
		TurretThroatV.setTextureSize(128, 64);
		setRotation(TurretThroatV, 0F, 0F, 0F);
		TurretAntennaI = new ModelRenderer(this, 14, 15);
		TurretAntennaI.addBox(0F, -8F, -0.5F, 0, 5, 1, scale);
		TurretAntennaI.setRotationPoint(0F, 0F, 0F);
		TurretAntennaI.setTextureSize(128, 64);
		setRotation(TurretAntennaI, 0F, 0F, 0F);
		TurretAntennaII = new ModelRenderer(this, 12, 16);
		TurretAntennaII.addBox(-0.5F, -8F, 0F, 1, 5, 0, scale);
		TurretAntennaII.setRotationPoint(0F, 0F, 0F);
		TurretAntennaII.setTextureSize(128, 64);
		setRotation(TurretAntennaII, 0F, 0F, 0F);
		HealthBar = new ModelRenderer(this, 12, 21);
		HealthBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1, scale);
		HealthBar.setRotationPoint(-2F, 19F, 0F);
		HealthBar.setTextureSize(128, 64);
		setRotation(HealthBar, 0F, 0F, 0F);
		AmmoBar = new ModelRenderer(this, 12, 27);
		AmmoBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1, scale);
		AmmoBar.setRotationPoint(2F, 19F, 0F);
		AmmoBar.setTextureSize(128, 64);
		setRotation(AmmoBar, 0F, 0F, 0F);
		LeftBarrel = new ModelRenderer(this, 32, 0);
		LeftBarrel.addBox(1F, 1F, -10F, 1, 1, 8, scale);
		LeftBarrel.setRotationPoint(0F, 0F, 0F);
		LeftBarrel.setTextureSize(128, 64);
		setRotation(LeftBarrel, 0F, 0F, 0F);
		RightBarrel = new ModelRenderer(this, 32, 0);
		RightBarrel.addBox(-2F, 1F, -10F, 1, 1, 8, scale);
		RightBarrel.setRotationPoint(0F, 0F, 0F);
		RightBarrel.setTextureSize(128, 64);
		setRotation(RightBarrel, 0F, 0F, 0F);

	}

	@Override
	public void renderBase() {
		TurretFeetI.render(0.0625F);
		TurretFeetII.render(0.0625F);
		TurretFeetIII.render(0.0625F);
		TurretFeetIV.render(0.0625F);
		TurretFeetV.render(0.0625F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		TurretFeetI.render(f5);
		TurretFeetII.render(f5);
		TurretFeetIII.render(f5);
		TurretFeetIV.render(f5);
		TurretFeetV.render(f5);
		TurretHead.render(f5);
		TurretThroatI.render(f5);
		TurretThroatII.render(f5);
		TurretThroatIII.render(f5);
		TurretThroatIV.render(f5);
		TurretThroatV.render(f5);
		TurretAntennaI.render(f5);
		TurretAntennaII.render(f5);
		RightBarrel.render(f5);
		LeftBarrel.render(f5);

		GL11.glPushMatrix();
//		if (this.isGlowTexture) {
//			GL11.glScalef(1.1F, 1.1F, 1.1F);
//			GL11.glTranslatef(0.010F, -0.11F, -0.00F);
//		}
		HealthBar.render(f5);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
//		if (this.isGlowTexture) {
//			GL11.glScalef(1.1F, 1.1F, 1.1F);
//			GL11.glTranslatef(-0.010F, -0.11F, -0.00F);
//		}
		AmmoBar.render(f5);
		GL11.glPopMatrix();
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.TurretHead.rotateAngleY =
        	this.TurretAntennaI.rotateAngleY =
        	this.TurretAntennaII.rotateAngleY =
        	this.RightBarrel.rotateAngleY =
        	this.LeftBarrel.rotateAngleY =
        		f3 / (180F / (float)Math.PI);
        this.TurretHead.rotateAngleX =
            this.TurretAntennaI.rotateAngleX =
            this.TurretAntennaII.rotateAngleX =
        	this.RightBarrel.rotateAngleX =
        	this.LeftBarrel.rotateAngleX =
            	f4 / (180F / (float)Math.PI);

        setStaticBody(f3);

		EntityTurretRevolver turret = (EntityTurretRevolver)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getMaxHealth() - turret.getHealth()) / turret.getMaxHealth());
        this.HealthBar.rotateAngleZ = healthRot;
//        float ammoRot = ((float)Math.PI / 2F) * ((float)(turret.getMaxAmmo() - turret.getAmmo()) / (float)turret.getMaxAmmo());
//
//        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
//        	ammoRot = 0F;
//        }
//        this.AmmoBar.rotateAngleZ = ammoRot;

        double xR = Math.sin(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getRightBarrelOffset()) / (double)turret.getMaxShootTicks());
        double zR = Math.cos(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getRightBarrelOffset()) / (double)turret.getMaxShootTicks());
        double yR = -Math.sin(turret.rotationPitch / (180F / (float)Math.PI)) * ((double)(turret.getRightBarrelOffset()) / (double)turret.getMaxShootTicks());

        double xL = Math.sin(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getLeftBarrelOffset()) / (double)turret.getMaxShootTicks());
        double zL = Math.cos(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getLeftBarrelOffset()) / (double)turret.getMaxShootTicks());
        double yL = -Math.sin(turret.rotationPitch / (180F / (float)Math.PI)) * ((double)(turret.getLeftBarrelOffset()) / (double)turret.getMaxShootTicks());

        xR *= (Math.cos(turret.rotationPitch / (180F / (float)Math.PI)));
        zR *= (Math.cos(turret.rotationPitch / (180F / (float)Math.PI)));
        xL *= (Math.cos(turret.rotationPitch / (180F / (float)Math.PI)));
        zL *= (Math.cos(turret.rotationPitch / (180F / (float)Math.PI)));

//        if (turret.getAmmo() <= 0) {
//        	xR = 0D;
//        	yR = 0D;
//        	zR = 0D;
//        	xL = 0D;
//        	yL = 0D;
//        	zL = 0D;
//        }

        RightBarrel.rotationPointX = (float) xR * 3F;
        RightBarrel.rotationPointZ = (float) zR * 3F;
        RightBarrel.rotationPointY = (float) yR * 3F;

        LeftBarrel.rotationPointX = (float) xL * 3F;
        LeftBarrel.rotationPointZ = (float) zL * 3F;
        LeftBarrel.rotationPointY = (float) yL * 3F;
	}

	private void setStaticBody(float f) {
		TurretFeetI.rotateAngleY = 0;
		TurretFeetII.rotateAngleY = 0;
		TurretFeetIII.rotateAngleY = 0;
		TurretFeetIV.rotateAngleY = 0;
		TurretFeetV.rotateAngleY = 0;
		TurretThroatI.rotateAngleY = 0;
		TurretThroatII.rotateAngleY = 0;
		TurretThroatIII.rotateAngleY = 0;
		TurretThroatIV.rotateAngleY = 0;
		TurretThroatV.rotateAngleY = 0;
		HealthBar.rotateAngleY = 0;
		AmmoBar.rotateAngleY = 0;
	}

	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
