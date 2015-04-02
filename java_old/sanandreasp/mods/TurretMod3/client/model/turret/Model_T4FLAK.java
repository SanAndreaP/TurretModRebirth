package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgInfAmmo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class Model_T4FLAK extends ModelTurret_Base {
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
    public ModelRenderer BarrelArm1;
    public ModelRenderer BarrelArm2;
    public ModelRenderer BarrelArm3;
    public ModelRenderer BarrelArm4;
    public ModelRenderer Barrel1;
    public ModelRenderer Barrel2;
    public ModelRenderer Barrel3;
    public ModelRenderer Barrel4;

	public Model_T4FLAK() {
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
		TurretHead = new ModelRenderer(this, 0, 0);
		TurretHead.addBox(-4F, -4F, -4F, 8, 8, 8);
		TurretHead.setRotationPoint(0F, 0F, 0F);
		TurretHead.setTextureSize(128, 64);
		setRotation(TurretHead, 0F, 0F, 0F);
		TurretThroatI = new ModelRenderer(this, 8, 16);
		TurretThroatI.addBox(0F, 0F, 0F, 1, 20, 1);
		TurretThroatI.setRotationPoint(0F, 0F, 0F);
		TurretThroatI.setTextureSize(128, 64);
		setRotation(TurretThroatI, 0.0523599F, 0F, -0.0523599F);
		TurretThroatII = new ModelRenderer(this, 8, 16);
		TurretThroatII.addBox(-1F, 0F, 0F, 1, 20, 1);
		TurretThroatII.setRotationPoint(0F, 0F, 0F);
		TurretThroatII.setTextureSize(128, 64);
		setRotation(TurretThroatII, 0.0523599F, 0F, 0.0523599F);
		TurretThroatIII = new ModelRenderer(this, 8, 16);
		TurretThroatIII.addBox(0F, 0F, -1F, 1, 20, 1);
		TurretThroatIII.setRotationPoint(0F, 0F, 0F);
		TurretThroatIII.setTextureSize(128, 64);
		setRotation(TurretThroatIII, -0.0523599F, 0F, -0.0523599F);
		TurretThroatIV = new ModelRenderer(this, 8, 16);
		TurretThroatIV.addBox(-1F, 0F, -1F, 1, 20, 1);
		TurretThroatIV.setRotationPoint(0F, 0F, 0F);
		TurretThroatIV.setTextureSize(128, 64);
		setRotation(TurretThroatIV, -0.0523599F, 0F, 0.0523599F);
		TurretThroatV = new ModelRenderer(this, 0, 16);
		TurretThroatV.addBox(-1F, 0F, -1F, 2, 20, 2);
		TurretThroatV.setRotationPoint(0F, 0F, 0F);
		TurretThroatV.setTextureSize(128, 64);
		setRotation(TurretThroatV, 0F, 0F, 0F);
		TurretAntennaI = new ModelRenderer(this, 14, 15);
		TurretAntennaI.addBox(0F, -8F, -0.5F, 0, 5, 1);
		TurretAntennaI.setRotationPoint(0F, 0F, 0F);
		TurretAntennaI.setTextureSize(128, 64);
		setRotation(TurretAntennaI, 0F, 0F, 0F);
		TurretAntennaII = new ModelRenderer(this, 12, 16);
		TurretAntennaII.addBox(-0.5F, -8F, 0F, 1, 5, 0);
		TurretAntennaII.setRotationPoint(0F, 0F, 0F);
		TurretAntennaII.setTextureSize(128, 64);
		setRotation(TurretAntennaII, 0F, 0F, 0F);
		HealthBar = new ModelRenderer(this, 12, 21);
		HealthBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1);
		HealthBar.setRotationPoint(-2F, 19F, 0F);
		HealthBar.setTextureSize(128, 64);
		setRotation(HealthBar, 0F, 0F, 0F);
		AmmoBar = new ModelRenderer(this, 12, 27);
		AmmoBar.addBox(-0.5F, -4F, -0.5F, 1, 5, 1);
		AmmoBar.setRotationPoint(2F, 19F, 0F);
		AmmoBar.setTextureSize(128, 64);
		setRotation(AmmoBar, 0F, 0F, 0F);
		BarrelArm1 = new ModelRenderer(this, 0, 0);
		BarrelArm1.addBox(-6F, -2.5F, -0.5F, 3, 1, 1);
		BarrelArm1.setRotationPoint(0F, 0F, 0F);
		BarrelArm1.setTextureSize(128, 64);
		setRotation(BarrelArm1, 0F, 0F, 0F);
		BarrelArm2 = new ModelRenderer(this, 0, 0);
		BarrelArm2.addBox(-6F, 1.5F, -0.5F, 3, 1, 1);
		BarrelArm2.setRotationPoint(0F, 0F, 0F);
		BarrelArm2.setTextureSize(128, 64);
		setRotation(BarrelArm2, 0F, 0F, 0F);
		BarrelArm3 = new ModelRenderer(this, 0, 0);
		BarrelArm3.addBox(3F, -2.5F, -0.5F, 3, 1, 1);
		BarrelArm3.setRotationPoint(0F, 0F, 0F);
		BarrelArm3.setTextureSize(128, 64);
		setRotation(BarrelArm3, 0F, 0F, 0F);
		BarrelArm4 = new ModelRenderer(this, 0, 0);
		BarrelArm4.addBox(3F, 1.5F, -0.5F, 3, 1, 1);
		BarrelArm4.setRotationPoint(0F, 0F, 0F);
		BarrelArm4.setTextureSize(128, 64);
		setRotation(BarrelArm4, 0F, 0F, 0F);
		Barrel1 = new ModelRenderer(this, 36, 36);
		Barrel1.addBox(-8F, -3F, -4F, 2, 2, 8);
		Barrel1.setRotationPoint(0F, 0F, 0F);
		Barrel1.setTextureSize(128, 64);
		setRotation(Barrel1, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 36, 36);
		Barrel2.addBox(-8F, 1F, -4F, 2, 2, 8);
		Barrel2.setRotationPoint(0F, 0F, 0F);
		Barrel2.setTextureSize(128, 64);
		setRotation(Barrel2, 0F, 0F, 0F);
		Barrel3 = new ModelRenderer(this, 36, 36);
		Barrel3.addBox(6F, -3F, -4F, 2, 2, 8);
		Barrel3.setRotationPoint(0F, 0F, 0F);
		Barrel3.setTextureSize(128, 64);
		setRotation(Barrel3, 0F, 0F, 0F);
		Barrel4 = new ModelRenderer(this, 36, 36);
		Barrel4.addBox(6F, 1F, -4F, 2, 2, 8);
		Barrel4.setRotationPoint(0F, 0F, 0F);
		Barrel4.setTextureSize(128, 64);
		setRotation(Barrel4, 0F, 0F, 0F);
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
		BarrelArm1.render(f5);
		BarrelArm2.render(f5);
		BarrelArm3.render(f5);
		BarrelArm4.render(f5);
		Barrel1.render(f5);
		Barrel2.render(f5);
		Barrel3.render(f5);
		Barrel4.render(f5);

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
		AmmoBar.render(f5);
		GL11.glPopMatrix();
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.TurretHead.rotateAngleY =
        	this.TurretAntennaI.rotateAngleY =
        	this.TurretAntennaII.rotateAngleY =
        	this.Barrel1.rotateAngleY =
        	this.Barrel2.rotateAngleY =
        	this.Barrel3.rotateAngleY =
        	this.Barrel4.rotateAngleY =
        	this.BarrelArm1.rotateAngleY =
        	this.BarrelArm2.rotateAngleY =
        	this.BarrelArm3.rotateAngleY =
        	this.BarrelArm4.rotateAngleY =
        		f3 / (180F / (float)Math.PI);
        this.TurretHead.rotateAngleX =
            	this.TurretAntennaI.rotateAngleX =
            	this.TurretAntennaII.rotateAngleX =
            	this.Barrel1.rotateAngleX =
            	this.Barrel2.rotateAngleX =
            	this.Barrel3.rotateAngleX =
            	this.Barrel4.rotateAngleX =
            	this.BarrelArm1.rotateAngleX =
            	this.BarrelArm2.rotateAngleX =
            	this.BarrelArm3.rotateAngleX =
            	this.BarrelArm4.rotateAngleX =
            		f4 / (180F / (float)Math.PI);

        setStaticBody(f3);

        EntityTurret_Base turret = (EntityTurret_Base)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getHealth() - turret.getSrvHealth()) / (float)turret.getHealth());
        this.HealthBar.rotateAngleZ = healthRot;
        float ammoRot = ((float)Math.PI / 2F) * ((float)(turret.getMaxAmmo() - turret.getAmmo()) / (float)turret.getMaxAmmo());

        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
        	ammoRot = 0F;
        }
        this.AmmoBar.rotateAngleZ = ammoRot;
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

}
