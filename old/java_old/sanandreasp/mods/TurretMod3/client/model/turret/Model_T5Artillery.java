package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgInfAmmo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class Model_T5Artillery extends ModelTurret_Base {
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
	public ModelRenderer TurretAntennaIII;
	public ModelRenderer TurretAntennaIV;
	public ModelRenderer BarrelArmI;
	public ModelRenderer BarrelArmII;
	public ModelRenderer BarrelArmIII;
	public ModelRenderer BarrelBase;

	public Model_T5Artillery() {
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
		setRotation(TurretAntennaI, 0F, 0F, (float)-Math.PI / 2F);
		TurretAntennaII = new ModelRenderer(this, 12, 16);
		TurretAntennaII.addBox(-0.5F, -8F, 0F, 1, 5, 0);
		TurretAntennaII.setRotationPoint(0F, 0F, 0F);
		TurretAntennaII.setTextureSize(128, 64);
		setRotation(TurretAntennaII, 0F, 0F, (float)-Math.PI / 2F);
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
		TurretAntennaIII = new ModelRenderer(this, 12, 16);
		TurretAntennaIII.addBox(-0.5F, -8F, 0F, 1, 5, 0);
		TurretAntennaIII.setRotationPoint(0F, 0F, 0F);
		TurretAntennaIII.setTextureSize(128, 64);
		setRotation(TurretAntennaIII, 0F, 0F, (float)Math.PI / 2F);
		TurretAntennaIV = new ModelRenderer(this, 14, 15);
		TurretAntennaIV.addBox(0F, -8F, -0.5F, 0, 5, 1);
		TurretAntennaIV.setRotationPoint(0F, 0F, 0F);
		TurretAntennaIV.setTextureSize(128, 64);
		setRotation(TurretAntennaIV, 0F, 0F, (float)Math.PI / 2F);
		BarrelArmI = new ModelRenderer(this, 0, 0);
		BarrelArmI.addBox(-1.5F, -6F, -1.5F, 1, 3, 1);
		BarrelArmI.setRotationPoint(0F, 0F, 0F);
		BarrelArmI.setTextureSize(128, 64);
		setRotation(BarrelArmI, 0F, 0F, 0F);
		BarrelArmII = new ModelRenderer(this, 0, 0);
		BarrelArmII.addBox(0.5F, -6F, -1.5F, 1, 3, 1);
		BarrelArmII.setRotationPoint(0F, 0F, 0F);
		BarrelArmII.setTextureSize(128, 64);
		setRotation(BarrelArmII, 0F, 0F, 0F);
		BarrelArmIII = new ModelRenderer(this, 0, 0);
		BarrelArmIII.addBox(-0.5F, -6F, 1F, 1, 3, 1);
		BarrelArmIII.setRotationPoint(0F, 0F, 0F);
		BarrelArmIII.setTextureSize(128, 64);
		setRotation(BarrelArmIII, 0F, 0F, 0F);
		BarrelBase = new ModelRenderer(this, 46, 6);
		BarrelBase.addBox(-2F, -9F, -5F, 4, 3, 10);
		BarrelBase.setRotationPoint(0F, 0F, 0F);
		BarrelBase.setTextureSize(128, 64);
		setRotation(BarrelBase, 0F, 0F, 0F);
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
		GL11.glPushMatrix();
		GL11.glRotatef(f3 , 0F, 1F, 0F);
		TurretAntennaI.render(f5);
		TurretAntennaII.render(f5);
		TurretAntennaIII.render(f5);
		TurretAntennaIV.render(f5);
		GL11.glPopMatrix();
		BarrelArmI.render(f5);
		BarrelArmII.render(f5);
		BarrelArmIII.render(f5);
		BarrelBase.render(f5);

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
        	this.TurretAntennaIII.rotateAngleY =
        	this.TurretAntennaIV.rotateAngleY =
        	this.BarrelArmI.rotateAngleY =
        	this.BarrelArmII.rotateAngleY =
        	this.BarrelArmIII.rotateAngleY =
        	this.BarrelBase.rotateAngleY =
        		f3 / (180F / (float)Math.PI);
        this.TurretHead.rotateAngleX =
            	this.TurretAntennaI.rotateAngleY =
            	this.TurretAntennaII.rotateAngleY =
            		f4 / (180F / (float)Math.PI);
    	this.TurretAntennaIII.rotateAngleY =
    	this.TurretAntennaIV.rotateAngleY = - (f4 / (180F / (float)Math.PI));
    	this.BarrelArmI.rotateAngleX =
    	this.BarrelArmII.rotateAngleX =
    	this.BarrelArmIII.rotateAngleX =
    	this.BarrelBase.rotateAngleX =
        		f4 / (180F / (float)Math.PI) - (float)(Math.PI / 4);

        setStaticBody(f3);

        EntityTurret_Base turret = (EntityTurret_Base)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getMaxHealth() - turret.getSrvHealth()) / (float)turret.getMaxHealth());
        this.HealthBar.rotateAngleZ = healthRot;
        float ammoRot = ((float)Math.PI / 2F) * ((float)(turret.getMaxAmmo() - turret.getAmmo()) / (float)turret.getMaxAmmo());

        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
        	ammoRot = 0F;
        }
        this.AmmoBar.rotateAngleZ = ammoRot;

        double x = Math.sin(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getShootTicks()) / (double)turret.getMaxShootTicks());
        double z = Math.cos(turret.rotationYawHead / (180D / Math.PI)) * ((double)(turret.getShootTicks()) / (double)turret.getMaxShootTicks());
        double y = -Math.sin((turret.rotationPitch-45) / (180F / (float)Math.PI)) * ((double)(turret.getShootTicks()) / (double)turret.getMaxShootTicks());

        x *= (Math.cos((turret.rotationPitch-45) / (180F / (float)Math.PI)));
        z *= (Math.cos((turret.rotationPitch-45) / (180F / (float)Math.PI)));

        if (turret.getAmmo() <= 0) {
        	x = 0D;
        	y = 0D;
        	z = 0D;
        }

        BarrelBase.rotationPointX = (float) x * 2F;
        BarrelBase.rotationPointZ = (float) z * 2F;
        BarrelBase.rotationPointY = (float) y * 2F;
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
