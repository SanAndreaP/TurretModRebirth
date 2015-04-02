package sanandreasp.mods.TurretMod3.client.model.turret;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_T2Minigun;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgInfAmmo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class Model_T2Minigun extends ModelTurret_Base {
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

	public ModelRenderer RightBarrelArm;
	public ModelRenderer LeftBarrelArm;
	public ModelRenderer LeftBarrelBase;
	public ModelRenderer LeftBarrel1;
	public ModelRenderer LeftBarrel2;
	public ModelRenderer LeftBarrel3;
	public ModelRenderer LeftBarrel4;
	public ModelRenderer RightBarrelBase;
	public ModelRenderer RightBarrel1;
	public ModelRenderer RightBarrel2;
	public ModelRenderer RightBarrel3;
	public ModelRenderer RightBarrel4;

	public Model_T2Minigun() {
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

		RightBarrelArm = new ModelRenderer(this, 24, 0);
	    RightBarrelArm.addBox(-8F, -0.5F, -0.5F, 5, 1, 1);
	    RightBarrelArm.setRotationPoint(0F, 0F, 0F);
	    RightBarrelArm.setTextureSize(128, 64);
	    setRotation(RightBarrelArm, 0F, 0F, 0F);
	    LeftBarrelArm = new ModelRenderer(this, 24, 0);
	    LeftBarrelArm.addBox(3F, -0.5F, -0.5F, 5, 1, 1);
	    LeftBarrelArm.setRotationPoint(0F, 0F, 0F);
	    LeftBarrelArm.setTextureSize(128, 64);
	    setRotation(LeftBarrelArm, 0F, 0F, 0F);
	    LeftBarrelBase = new ModelRenderer(this, 24, 2);
	    LeftBarrelBase.addBox(-1F, -1F, -2F, 2, 2, 4);
	    LeftBarrelBase.setRotationPoint(-8F, 0F, 0F);
	    LeftBarrelBase.setTextureSize(128, 64);
	    setRotation(LeftBarrelBase, 0F, 0F, 0.7853982F);
		LeftBarrel1 = new ModelRenderer(this, 36, 0);
		LeftBarrel1.addBox(-0.5F, -2F, -7F, 1, 1, 6);
		LeftBarrel1.setRotationPoint(-8F, 0F, 0F);
		LeftBarrel1.setTextureSize(128, 64);
		setRotation(LeftBarrel1, 0F, 0F, 0F);
		LeftBarrel2 = new ModelRenderer(this, 36, 0);
		LeftBarrel2.addBox(1F, -0.5F, -7F, 1, 1, 6);
		LeftBarrel2.setRotationPoint(-8F, 0F, 0F);
		LeftBarrel2.setTextureSize(128, 64);
		setRotation(LeftBarrel2, 0F, 0F, 0F);
		LeftBarrel3 = new ModelRenderer(this, 36, 0);
		LeftBarrel3.addBox(-2F, -0.5F, -7F, 1, 1, 6);
		LeftBarrel3.setRotationPoint(-8F, 0F, 0F);
		LeftBarrel3.setTextureSize(128, 64);
		setRotation(LeftBarrel3, 0F, 0F, 0F);
		LeftBarrel4 = new ModelRenderer(this, 36, 0);
		LeftBarrel4.addBox(-0.5F, 1F, -7F, 1, 1, 6);
		LeftBarrel4.setRotationPoint(-8F, 0F, 0F);
		LeftBarrel4.setTextureSize(128, 64);
		setRotation(LeftBarrel4, 0F, 0F, 0F);
		RightBarrelBase = new ModelRenderer(this, 24, 2);
		RightBarrelBase.addBox(-1F, -1F, -2F, 2, 2, 4);
		RightBarrelBase.setRotationPoint(8F, 0F, 0F);
		RightBarrelBase.setTextureSize(128, 64);
		setRotation(RightBarrelBase, 0F, 0F, 0.7853982F);
		RightBarrel1 = new ModelRenderer(this, 36, 0);
		RightBarrel1.addBox(-0.5F, -2F, -7F, 1, 1, 6);
		RightBarrel1.setRotationPoint(8F, 0F, 0F);
		RightBarrel1.setTextureSize(128, 64);
		setRotation(RightBarrel1, 0F, 0F, 0F);
		RightBarrel2 = new ModelRenderer(this, 36, 0);
		RightBarrel2.addBox(1F, -0.5F, -7F, 1, 1, 6);
		RightBarrel2.setRotationPoint(8F, 0F, 0F);
		RightBarrel2.setTextureSize(128, 64);
		setRotation(RightBarrel2, 0F, 0F, 0F);
		RightBarrel3 = new ModelRenderer(this, 36, 0);
		RightBarrel3.addBox(-2F, -0.5F, -7F, 1, 1, 6);
		RightBarrel3.setRotationPoint(8F, 0F, 0F);
		RightBarrel3.setTextureSize(128, 64);
		setRotation(RightBarrel3, 0F, 0F, 0F);
		RightBarrel4 = new ModelRenderer(this, 36, 0);
		RightBarrel4.addBox(-0.5F, 1F, -7F, 1, 1, 6);
		RightBarrel4.setRotationPoint(8F, 0F, 0F);
		RightBarrel4.setTextureSize(128, 64);
		setRotation(RightBarrel4, 0F, 0F, 0F);
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
		RightBarrelArm.render(f5);
		LeftBarrelArm.render(f5);
		GL11.glPushMatrix();
		GL11.glRotatef(f3, 0F, 1F, 0F);
		GL11.glRotatef(f4, 1F, 0F, 0F);
		RightBarrelBase.render(f5);
		LeftBarrelBase.render(f5);
		RightBarrel1.render(f5);
		RightBarrel2.render(f5);
		RightBarrel3.render(f5);
		RightBarrel4.render(f5);
		LeftBarrel1.render(f5);
		LeftBarrel2.render(f5);
		LeftBarrel3.render(f5);
		LeftBarrel4.render(f5);
		GL11.glPopMatrix();

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
        	this.RightBarrelArm.rotateAngleY =
        	this.LeftBarrelArm.rotateAngleY =
        		f3 / (180F / (float)Math.PI);
        this.TurretHead.rotateAngleX =
            this.TurretAntennaI.rotateAngleX =
            this.TurretAntennaII.rotateAngleX =
        	this.RightBarrelArm.rotateAngleX =
        	this.LeftBarrelArm.rotateAngleX =
            	f4 / (180F / (float)Math.PI);

        setStaticBody(f3);

        EntityTurret_T2Minigun turret = (EntityTurret_T2Minigun)entity;

        float healthRot = -((float)Math.PI / 2F) * ((float)(turret.getMaxHealth() - turret.getSrvHealth()) / (float)turret.getMaxHealth());
        this.HealthBar.rotateAngleZ = healthRot;
        float ammoRot = ((float)Math.PI / 2F) * ((float)(turret.getMaxAmmo() - turret.getAmmo()) / (float)turret.getMaxAmmo());

        if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
        	ammoRot = 0F;
        }
        this.AmmoBar.rotateAngleZ = ammoRot;

        float x = (float) (Math.cos(f3 / (180D / Math.PI)) * 8D);
        float z = (float) (-Math.sin(f3 / (180D / Math.PI)) * 8D);

        float leftRot = (float) ((45D / 180D * Math.PI) + (turret.barrelRot / 180D * Math.PI));
        float rightRot = (float) (turret.barrelRot / 180D * Math.PI);

        this.setRotation(this.RightBarrelBase, 0F, 0F, leftRot);
        this.setRotation(this.LeftBarrelBase, 0F, 0F, rightRot);
        this.setRotation(this.RightBarrel1, 0F, 0F, rightRot);
        this.setRotation(this.LeftBarrel1, 0F, 0F, leftRot);
        this.setRotation(this.RightBarrel2, 0F, 0F, rightRot);
        this.setRotation(this.LeftBarrel2, 0F, 0F, leftRot);
        this.setRotation(this.RightBarrel3, 0F, 0F, rightRot);
        this.setRotation(this.LeftBarrel3, 0F, 0F, leftRot);
        this.setRotation(this.RightBarrel4, 0F, 0F, rightRot);
        this.setRotation(this.LeftBarrel4, 0F, 0F, leftRot);
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
