package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

public class Model_TSCollector extends ModelTurret_Base {
	public ModelRenderer TurretFeetI;
	public ModelRenderer TurretFeetII;
	public ModelRenderer TurretFeetIII;
	public ModelRenderer TurretFeetIV;
	public ModelRenderer TurretFeetV;
	public ModelRenderer TurretThroatI;
	public ModelRenderer TurretThroatII;
	public ModelRenderer TurretThroatIII;
	public ModelRenderer TurretThroatIV;
	public ModelRenderer TurretThroatV;
	public ModelRenderer HealthBar;
	public ModelRenderer ExpBar;
	public ModelRenderer PlateBase;
	public ModelRenderer PlateHead;
	public ModelRenderer AntennaI;
	public ModelRenderer AntennaII;
	public ModelRenderer AntennaIII;
	public ModelRenderer AntennaIV;

	public Model_TSCollector() {
		textureWidth = 128;
		textureHeight = 64;

		TurretFeetI = new ModelRenderer(this, 0, 49);
		TurretFeetI.addBox(-7F, 23F, -7F, 14, 1, 14);
		TurretFeetI.setRotationPoint(0F, 0F, 0F);
		TurretFeetI.setTextureSize(128, 64);
		setRotation(TurretFeetI, 0F, 0F, 0F);
		TurretFeetII = new ModelRenderer(this, 0, 36);
		TurretFeetII.addBox(-6F, 22F, -6F, 12, 1, 12);
		TurretFeetII.setRotationPoint(0F, 0F, 0F);
		TurretFeetII.setTextureSize(128, 64);
		setRotation(TurretFeetII, 0F, 0F, 0F);
		TurretFeetIII = new ModelRenderer(this, 12, 25);
		TurretFeetIII.addBox(-5F, 21F, -5F, 10, 1, 10);
		TurretFeetIII.setRotationPoint(0F, 0F, 0F);
		TurretFeetIII.setTextureSize(128, 64);
		setRotation(TurretFeetIII, 0F, 0F, 0F);
		TurretFeetIV = new ModelRenderer(this, 16, 16);
		TurretFeetIV.addBox(-4F, 20F, -4F, 8, 1, 8);
		TurretFeetIV.setRotationPoint(0F, 0F, 0F);
		TurretFeetIV.setTextureSize(128, 64);
		setRotation(TurretFeetIV, 0F, 0F, 0F);
		TurretFeetV = new ModelRenderer(this, 32, 9);
		TurretFeetV.addBox(-3F, 19F, -3F, 6, 1, 6);
		TurretFeetV.setRotationPoint(0F, 0F, 0F);
		TurretFeetV.setTextureSize(128, 64);
		setRotation(TurretFeetV, 0F, 0F, 0F);
		TurretThroatI = new ModelRenderer(this, 8, 16);
		TurretThroatI.addBox(0F, 0F, 0F, 1, 9, 1);
		TurretThroatI.setRotationPoint(0F, 11F, 0F);
		TurretThroatI.setTextureSize(128, 64);
		setRotation(TurretThroatI, 0.0523599F, 0F, -0.0523599F);
		TurretThroatII = new ModelRenderer(this, 8, 16);
		TurretThroatII.addBox(-1F, 0F, 0F, 1, 9, 1);
		TurretThroatII.setRotationPoint(0F, 11F, 0F);
		TurretThroatII.setTextureSize(128, 64);
		setRotation(TurretThroatII, 0.0523599F, 0F, 0.0523599F);
		TurretThroatIII = new ModelRenderer(this, 8, 16);
		TurretThroatIII.addBox(0F, 0F, -1F, 1, 9, 1);
		TurretThroatIII.setRotationPoint(0F, 11F, 0F);
		TurretThroatIII.setTextureSize(128, 64);
		setRotation(TurretThroatIII, -0.0523599F, 0F, -0.0523599F);
		TurretThroatIV = new ModelRenderer(this, 8, 16);
		TurretThroatIV.addBox(-1F, 0F, -1F, 1, 9, 1);
		TurretThroatIV.setRotationPoint(0F, 11F, 0F);
		TurretThroatIV.setTextureSize(128, 64);
		setRotation(TurretThroatIV, -0.0523599F, 0F, 0.0523599F);
		TurretThroatV = new ModelRenderer(this, 0, 16);
		TurretThroatV.addBox(-1F, 0F, -1F, 2, 13, 2);
		TurretThroatV.setRotationPoint(0F, 7F, 0F);
		TurretThroatV.setTextureSize(128, 64);
		setRotation(TurretThroatV, 0F, 0F, 0F);
		HealthBar = new ModelRenderer(this, 12, 21);
		HealthBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1);
		HealthBar.setRotationPoint(-2F, 19F, 0F);
		HealthBar.setTextureSize(128, 64);
		setRotation(HealthBar, 0F, 0F, 0F);
		ExpBar = new ModelRenderer(this, 12, 27);
		ExpBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1);
		ExpBar.setRotationPoint(2F, 19F, 0F);
		ExpBar.setTextureSize(128, 64);
		setRotation(ExpBar, 0F, 0F, 0F);
		PlateBase = new ModelRenderer(this, 36, 37);
		PlateBase.addBox(-5F, 0F, -5F, 10, 1, 10);
		PlateBase.setRotationPoint(0F, 11F, 0F);
		PlateBase.setTextureSize(128, 64);
		setRotation(PlateBase, 0F, 0F, 0F);
		PlateHead = new ModelRenderer(this, 0, 0);
		PlateHead.addBox(-2F, -2F, -2F, 4, 2, 4);
		PlateHead.setRotationPoint(0F, 11F, 0F);
		PlateHead.setTextureSize(128, 64);
		setRotation(PlateHead, 0F, 0F, 0F);
		AntennaI = new ModelRenderer(this, 68, 0);
		AntennaI.addBox(-5.5F, -18F, 4.5F, 1, 30, 1);
		AntennaI.setRotationPoint(0F, 11F, 0F);
		AntennaI.setTextureSize(128, 64);
		setRotation(AntennaI, 0F, 0F, 0F);
		AntennaII = new ModelRenderer(this, 56, 0);
		AntennaII.addBox(4.5F, -18F, 4.5F, 1, 30, 1);
		AntennaII.setRotationPoint(0F, 11F, 0F);
		AntennaII.setTextureSize(128, 64);
		setRotation(AntennaII, 0F, 0F, 0F);
		AntennaIII = new ModelRenderer(this, 60, 0);
		AntennaIII.addBox(4.5F, -18F, -5.5F, 1, 30, 1);
		AntennaIII.setRotationPoint(0F, 11F, 0F);
		AntennaIII.setTextureSize(128, 64);
		setRotation(AntennaIII, 0F, 0F, 0F);
		AntennaIV = new ModelRenderer(this, 64, 0);
		AntennaIV.addBox(-5.5F, -18F, -5.5F, 1, 30, 1);
		AntennaIV.setRotationPoint(0F, 11F, 0F);
		AntennaIV.setTextureSize(128, 64);
		setRotation(AntennaIV, 0F, 0F, 0F);
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
		TurretThroatI.render(f5);
		TurretThroatII.render(f5);
		TurretThroatIII.render(f5);
		TurretThroatIV.render(f5);
		TurretThroatV.render(f5);
		PlateBase.render(f5);
		PlateHead.render(f5);
		if (this.isGlowTexture) {
			GL11.glPushMatrix();
			GL11.glScalef(1.002F, 1F, 1.002F);
			GL11.glTranslatef(0.001F, 0F, -0.001F);
			AntennaI.render(f5);
			GL11.glTranslatef(-0.002F, 0F, 0F);
			AntennaII.render(f5);
			GL11.glTranslatef(0F, 0F, 0.002F);
			AntennaIII.render(f5);
			GL11.glTranslatef(0.002F, 0F, 0F);
			AntennaIV.render(f5);
			GL11.glPopMatrix();
		} else {
			AntennaI.render(f5);
			AntennaII.render(f5);
			AntennaIII.render(f5);
			AntennaIV.render(f5);
		}

		GL11.glPushMatrix();
		if (this.isGlowTexture) {
			GL11.glScalef(1.1F, 1.1F, 1.1F);
			GL11.glTranslatef(0.010F, -0.11F, -0.00F);
		}
		HealthBar.render(f5);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		if (this.isGlowTexture) {
			GL11.glScalef(1.1F, 1.1F, 1.1F);
			GL11.glTranslatef(-0.010F, -0.11F, -0.00F);
		}
		ExpBar.render(f5);
		GL11.glPopMatrix();
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        setStaticBody(f3);

        EntityTurret_Base turret = (EntityTurret_Base)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getMaxHealth() - turret.getSrvHealth()) / (float)turret.getMaxHealth());
        this.HealthBar.rotateAngleZ = healthRot;
        float ammoRot = ((float)Math.PI / 2F) * ((float)(turret.getExpCap() - turret.getExperience()) / (float)turret.getExpCap());

//        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
//        	ammoRot = 0F;
//        }
        this.ExpBar.rotateAngleZ = ammoRot;
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
		ExpBar.rotateAngleY = 0;
		PlateBase.rotateAngleY = 0;
		PlateHead.rotateAngleY = 0;
		AntennaI.rotateAngleY = 0;
		AntennaII.rotateAngleY = 0;
		AntennaIII.rotateAngleY = 0;
		AntennaIV.rotateAngleY = 0;
	}

}
