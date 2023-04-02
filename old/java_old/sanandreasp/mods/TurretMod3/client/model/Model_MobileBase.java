package sanandreasp.mods.TurretMod3.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Model_MobileBase extends ModelBase
{
	public ModelRenderer wheel4;
	public ModelRenderer wheel3;
	public ModelRenderer wheel2;
	public ModelRenderer wheel1;
	public ModelRenderer base4;
	public ModelRenderer base3;
	public ModelRenderer base2;
	public ModelRenderer base1;
	public ModelRenderer Shape1;
	public ModelRenderer Shape3;
	public ModelRenderer Shape2;
	public ModelRenderer Shape4;
	public ModelRenderer Shape5;
	public ModelRenderer Shape6;
	public ModelRenderer Shape7;
	public ModelRenderer Shape8;
	public ModelRenderer Shape9;
	public ModelRenderer Shape10;

	public Model_MobileBase() {
		textureWidth = 64;
		textureHeight = 32;

		wheel4 = new ModelRenderer(this, 0, 0);
		wheel4.addBox(-1F, -1F, -1F, 2, 2, 2);
		wheel4.setRotationPoint(-6F, 23F, 6F);
		wheel4.setTextureSize(64, 32);
		setRotation(wheel4, 0.7853982F, 0F, -0.6108652F);
		wheel3 = new ModelRenderer(this, 0, 0);
		wheel3.addBox(-1F, -1F, -1F, 2, 2, 2);
		wheel3.setRotationPoint(6F, 23F, -6F);
		wheel3.setTextureSize(64, 32);
		setRotation(wheel3, 0.7853982F, 0F, -0.6108652F);
		wheel2 = new ModelRenderer(this, 0, 0);
		wheel2.addBox(-1F, -1F, -1F, 2, 2, 2);
		wheel2.setRotationPoint(-6F, 23F, -6F);
		wheel2.setTextureSize(64, 32);
		setRotation(wheel2, 0.7853982F, 0F, 0.6108652F);
		wheel1 = new ModelRenderer(this, 0, 0);
		wheel1.addBox(-1F, -1F, -1F, 2, 2, 2);
		wheel1.setRotationPoint(6F, 23F, 6F);
		wheel1.setTextureSize(64, 32);
		setRotation(wheel1, 0.7853982F, 0F, 0.6108652F);
		base4 = new ModelRenderer(this, 0, 0);
		base4.addBox(5F, -2F, -8F, 3, 1, 13);
		base4.setRotationPoint(0F, 23F, 0F);
		base4.setTextureSize(64, 32);
		setRotation(base4, 0F, 0F, 0F);
		base3 = new ModelRenderer(this, 0, 0);
		base3.addBox(5F, -2F, -8F, 3, 1, 13);
		base3.setRotationPoint(0F, 23F, 0F);
		base3.setTextureSize(64, 32);
		setRotation(base3, 0F, (float) Math.PI / 2F, 0F);
		base2 = new ModelRenderer(this, 0, 0);
		base2.addBox(5F, -2F, -8F, 3, 1, 13);
		base2.setRotationPoint(0F, 23F, 0F);
		base2.setTextureSize(64, 32);
		setRotation(base2, 0F, (float) Math.PI, 0F);
		base1 = new ModelRenderer(this, 0, 0);
		base1.addBox(5F, -2F, -8F, 3, 1, 13);
		base1.setRotationPoint(0F, 23F, 0F);
		base1.setTextureSize(64, 32);
		setRotation(base1, 0F, -(float) Math.PI/2F, 0F);
		Shape1 = new ModelRenderer(this, 16, 0);
		Shape1.addBox(-0.5F, -1.9F, -8F, 1, 1, 16);
		Shape1.setRotationPoint(0F, 23F, 0F);
		Shape1.setTextureSize(64, 32);
		setRotation(Shape1, 0F, 0.7853982F, 0F);
		Shape3 = new ModelRenderer(this, 16, 0);
		Shape3.addBox(-0.5F, -1.9F, -8F, 1, 1, 16);
		Shape3.setRotationPoint(0F, 23F, 0F);
		Shape3.setTextureSize(64, 32);
		setRotation(Shape3, 0F, -0.7853982F, 0F);
		Shape2 = new ModelRenderer(this, 0, 4);
		Shape2.addBox(-7F, -4F, -7.9F, 1, 3, 1);
		Shape2.setRotationPoint(0F, 23F, 0F);
		Shape2.setTextureSize(64, 32);
		setRotation(Shape2, 0F, 0F, 0F);
		Shape4 = new ModelRenderer(this, 0, 4);
		Shape4.addBox(-7.9F, -4F, -7F, 1, 3, 1);
		Shape4.setRotationPoint(0F, 23F, 0F);
		Shape4.setTextureSize(64, 32);
		setRotation(Shape4, 0F, 0F, 0F);
		Shape5 = new ModelRenderer(this, 0, 4);
		Shape5.addBox(-7.9F, -4F, 6F, 1, 3, 1);
		Shape5.setRotationPoint(0F, 23F, 0F);
		Shape5.setTextureSize(64, 32);
		setRotation(Shape5, 0F, 0F, 0F);
		Shape6 = new ModelRenderer(this, 0, 4);
		Shape6.addBox(-7F, -4F, 6.9F, 1, 3, 1);
		Shape6.setRotationPoint(0F, 23F, 0F);
		Shape6.setTextureSize(64, 32);
		setRotation(Shape6, 0F, 0F, 0F);
		Shape7 = new ModelRenderer(this, 0, 4);
		Shape7.addBox(6F, -4F, 6.9F, 1, 3, 1);
		Shape7.setRotationPoint(0F, 23F, 0F);
		Shape7.setTextureSize(64, 32);
		setRotation(Shape7, 0F, 0F, 0F);
		Shape8 = new ModelRenderer(this, 0, 4);
		Shape8.addBox(6.9F, -4F, 6F, 1, 3, 1);
		Shape8.setRotationPoint(0F, 23F, 0F);
		Shape8.setTextureSize(64, 32);
		setRotation(Shape8, 0F, 0F, 0F);
		Shape9 = new ModelRenderer(this, 0, 4);
		Shape9.addBox(6.9F, -4F, -7F, 1, 3, 1);
		Shape9.setRotationPoint(0F, 23F, 0F);
		Shape9.setTextureSize(64, 32);
		setRotation(Shape9, 0F, 0F, 0F);
		Shape10 = new ModelRenderer(this, 0, 4);
		Shape10.addBox(6F, -4F, -7.9F, 1, 3, 1);
		Shape10.setRotationPoint(0F, 23F, 0F);
		Shape10.setTextureSize(64, 32);
		setRotation(Shape10, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		wheel4.render(f5);
		wheel3.render(f5);
		wheel2.render(f5);
		wheel1.render(f5);
		base4.render(f5);
		base3.render(f5);
		base2.render(f5);
		base1.render(f5);
		Shape1.render(f5);
		Shape3.render(f5);
		Shape2.render(f5);
		Shape4.render(f5);
		Shape5.render(f5);
		Shape6.render(f5);
		Shape7.render(f5);
		Shape8.render(f5);
		Shape9.render(f5);
		Shape10.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
