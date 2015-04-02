package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgInfAmmo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class Model_T3Flamethrower extends ModelTurret_Base {
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
	public ModelRenderer tankLeft;
	public ModelRenderer tankRight;
	public ModelRenderer pipe1;
	public ModelRenderer pipe2;
	public ModelRenderer Barrel;
	public ModelRenderer pipe3;
	public ModelRenderer pipe4;
	public ModelRenderer pipe5;
	public ModelRenderer pipe6;
	public ModelRenderer pipe7;

	public Model_T3Flamethrower() {
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
		tankLeft = new ModelRenderer(this, 50, 0);
		tankLeft.addBox(-8F, 1F, -1F, 4, 6, 4);
		tankLeft.setRotationPoint(0F, 0F, 0F);
		tankLeft.setTextureSize(128, 64);
		setRotation(tankLeft, 0F, 0F, 0F);
		tankRight = new ModelRenderer(this, 50, 0);
		tankRight.addBox(4F, 1F, -1F, 4, 6, 4);
		tankRight.setRotationPoint(0F, 0F, 0F);
		tankRight.setTextureSize(128, 64);
		setRotation(tankRight, 0F, 0F, 0F);
		pipe1 = new ModelRenderer(this, 0, 0);
		pipe1.addBox(-5F, -1F, 0F, 1, 2, 1);
		pipe1.setRotationPoint(0F, 0F, 0F);
		pipe1.setTextureSize(128, 64);
		setRotation(pipe1, 0F, 0F, 0F);
		pipe2 = new ModelRenderer(this, 0, 0);
		pipe2.addBox(4F, -1F, 0F, 1, 2, 1);
		pipe2.setRotationPoint(0F, 0F, 0F);
		pipe2.setTextureSize(128, 64);
		setRotation(pipe2, 0F, 0F, 0F);
		Barrel = new ModelRenderer(this, 32, 0);
		Barrel.addBox(-1F, 1F, -10F, 2, 2, 7);
		Barrel.setRotationPoint(0F, 0F, 0F);
		Barrel.setTextureSize(128, 64);
		setRotation(Barrel, 0F, 0F, 0F);
		pipe3 = new ModelRenderer(this, 0, 0);
		pipe3.addBox(-5F, 0F, -5F, 1, 2, 1);
		pipe3.setRotationPoint(0F, 0F, 0F);
		pipe3.setTextureSize(128, 64);
		setRotation(pipe3, 0F, 0F, 0F);
		pipe4 = new ModelRenderer(this, 0, 0);
		pipe4.addBox(4F, 0F, -5F, 1, 2, 1);
		pipe4.setRotationPoint(0F, 0F, 0F);
		pipe4.setTextureSize(128, 64);
		setRotation(pipe4, 0F, 0F, 0F);
		pipe5 = new ModelRenderer(this, 24, 0);
		pipe5.addBox(-5F, -1F, -5F, 1, 1, 5);
		pipe5.setRotationPoint(0F, 0F, 0F);
		pipe5.setTextureSize(128, 64);
		setRotation(pipe5, 0F, 0F, 0F);
		pipe6 = new ModelRenderer(this, 24, 0);
		pipe6.addBox(4F, -1F, -5F, 1, 1, 5);
		pipe6.setRotationPoint(0F, 0F, 0F);
		pipe6.setTextureSize(128, 64);
		setRotation(pipe6, 0F, 0F, 0F);
		pipe7 = new ModelRenderer(this, 50, 10);
		pipe7.addBox(-4F, 1.01F, -5F, 8, 1, 1);
		pipe7.setRotationPoint(0F, 0F, 0F);
		pipe7.setTextureSize(128, 64);
		setRotation(pipe7, 0F, 0F, 0F);
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
		tankLeft.render(f5);
		tankRight.render(f5);
		pipe1.render(f5);
		pipe2.render(f5);
		pipe3.render(f5);
		pipe4.render(f5);
		pipe5.render(f5);
		pipe6.render(f5);
		pipe7.render(f5);
		Barrel.render(f5);
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
        	this.tankLeft.rotateAngleY =
        	this.tankRight.rotateAngleY =
        	this.pipe1.rotateAngleY =
        	this.pipe2.rotateAngleY =
        	this.pipe3.rotateAngleY =
        	this.pipe4.rotateAngleY =
        	this.pipe5.rotateAngleY =
        	this.pipe6.rotateAngleY =
        	this.pipe7.rotateAngleY =
        	this.Barrel.rotateAngleY =
        		f3 / (180F / (float)Math.PI);
        this.TurretHead.rotateAngleX =
            	this.TurretAntennaI.rotateAngleX =
            	this.TurretAntennaII.rotateAngleX =
            	this.tankLeft.rotateAngleX =
            	this.tankRight.rotateAngleX =
            	this.pipe1.rotateAngleX =
            	this.pipe2.rotateAngleX =
            	this.pipe3.rotateAngleX =
            	this.pipe4.rotateAngleX =
            	this.pipe5.rotateAngleX =
            	this.pipe6.rotateAngleX =
            	this.pipe7.rotateAngleX =
            	this.Barrel.rotateAngleX =
            		f4 / (180F / (float)Math.PI);

        setStaticBody(f3);

        EntityTurret_Base turret = (EntityTurret_Base)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getMaxHealth() - turret.getSrvHealth()) / (float)turret.getMaxHealth());
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
