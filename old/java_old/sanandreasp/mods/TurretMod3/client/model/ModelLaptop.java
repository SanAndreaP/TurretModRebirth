package sanandreasp.mods.TurretMod3.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelLaptop extends ModelBase
{
	//fields
	public ModelRenderer LaptopBase;
	public ModelRenderer LaptopScreen;

	public ModelLaptop()
	{
		textureWidth = 64;
		textureHeight = 32;

		LaptopBase = new ModelRenderer(this, 0, 0);
		LaptopBase.addBox(-8F, 0F, -7F, 16, 1, 13);
		LaptopBase.setRotationPoint(0F, 23F, 0F);
		setRotation(LaptopBase, 0F, 0F, 0F);
		LaptopScreen = new ModelRenderer(this, 0, 14);
		LaptopScreen.addBox(-8F, -12F, -0.5F, 16, 13, 1);
		LaptopScreen.setRotationPoint(0F, 22.5F, 5F);
		setRotation(LaptopScreen, -0.2094395F, 0F, 0F);
	}

	public void renderBlock() {
		this.LaptopBase.render(0.0625F);
		GL11.glPushMatrix();
		this.LaptopScreen.render(0.0625F);
//		this.Shape2.render(0.0625F);
		GL11.glPopMatrix();
//		this.Shape1.rotationPointY = 13F;
//		this.Shape2.rotationPointY = 13F;
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
