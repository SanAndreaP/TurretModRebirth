package de.sanandrew.mods.turretmod.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * ElectrolyteGenerator - SanAndreasP
 * Created using Tabula 4.1.1
 */
public class ModelElectrolyteGenerator
        extends ModelBase
{
    public ModelRenderer base;
    public ModelRenderer rodMain;
    public ModelRenderer rodBall;
    public ModelRenderer rod1;
    public ModelRenderer cablePlane1;
    public ModelRenderer rod2;
    public ModelRenderer cablePlane2;
    public ModelRenderer rod3;
    public ModelRenderer cablePlane3;
    public ModelRenderer rod4;
    public ModelRenderer cablePlane4;
    public ModelRenderer rod5;
    public ModelRenderer cablePlane5;
    public ModelRenderer rod6;
    public ModelRenderer cablePlane6;
    public ModelRenderer rod7;
    public ModelRenderer cablePlane7;
    public ModelRenderer rod8;
    public ModelRenderer cablePlane8;
    public ModelRenderer rod9;
    public ModelRenderer cablePlane9;

    public ModelElectrolyteGenerator() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.rod2 = new ModelRenderer(this, 12, 0);
        this.rod2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod2.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod2, 0.0F, 0.6981317007977318F, 0.0F);
        this.cablePlane5 = new ModelRenderer(this, 48, 0);
        this.cablePlane5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane5.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane5, 0.0F, 2.792526803190927F, 0.0F);
        this.cablePlane7 = new ModelRenderer(this, 48, 0);
        this.cablePlane7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane7.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane7, 0.0F, -2.0943951023931953F, 0.0F);
        this.cablePlane9 = new ModelRenderer(this, 48, 0);
        this.cablePlane9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane9.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane9, 0.0F, -0.6981317007977318F, 0.0F);
        this.cablePlane1 = new ModelRenderer(this, 48, 0);
        this.cablePlane1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane1.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.rod9 = new ModelRenderer(this, 12, 0);
        this.rod9.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod9.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod9, 0.0F, -0.6981317007977318F, 0.0F);
        this.cablePlane6 = new ModelRenderer(this, 48, 0);
        this.cablePlane6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane6.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane6, 0.0F, -2.792526803190927F, 0.0F);
        this.rod1 = new ModelRenderer(this, 12, 0);
        this.rod1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod1.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.cablePlane2 = new ModelRenderer(this, 48, 0);
        this.cablePlane2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane2.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane2, 0.0F, 0.6981317007977318F, 0.0F);
        this.rodMain = new ModelRenderer(this, 0, 0);
        this.rodMain.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.rodMain.addBox(-0.5F, 0.0F, -0.5F, 1, 9, 1, 0.0F);
        this.cablePlane4 = new ModelRenderer(this, 48, 0);
        this.cablePlane4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane4.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane4, 0.0F, 2.0943951023931953F, 0.0F);
        this.cablePlane8 = new ModelRenderer(this, 48, 0);
        this.cablePlane8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane8.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane8, 0.0F, -1.3962634015954636F, 0.0F);
        this.rod4 = new ModelRenderer(this, 12, 0);
        this.rod4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod4.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod4, 0.0F, 2.0943951023931953F, 0.0F);
        this.rodBall = new ModelRenderer(this, 4, 0);
        this.rodBall.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rodBall.addBox(-1.0F, -3.0F, -1.0F, 2, 2, 2, 0.0F);
        this.rod7 = new ModelRenderer(this, 12, 0);
        this.rod7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod7.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod7, 0.0F, -2.0943951023931953F, 0.0F);
        this.rod6 = new ModelRenderer(this, 12, 0);
        this.rod6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod6.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod6, 0.0F, -2.792526803190927F, 0.0F);
        this.cablePlane3 = new ModelRenderer(this, 48, 0);
        this.cablePlane3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cablePlane3.addBox(0.3F, -2.1F, 0.0F, 5, 6, 0, 0.0F);
        this.setRotateAngle(cablePlane3, 0.0F, 1.3962634015954636F, 0.0F);
        this.rod8 = new ModelRenderer(this, 12, 0);
        this.rod8.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod8.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod8, 0.0F, -1.3962634015954636F, 0.0F);
        this.rod3 = new ModelRenderer(this, 12, 0);
        this.rod3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod3.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod3, 0.0F, 1.3962634015954636F, 0.0F);
        this.rod5 = new ModelRenderer(this, 12, 0);
        this.rod5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rod5.addBox(5.0F, -2.0F, -0.5F, 1, 10, 1, 0.0F);
        this.setRotateAngle(rod5, 0.0F, 2.792526803190927F, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.base.addBox(-8.0F, 8.0F, -8.0F, 16, 16, 16, 0.0F);
    }

    public void render(float f5, short enableCableFlags) {
        this.base.render(f5);
        this.rodMain.render(f5);
        this.rodBall.render(f5);
        this.rod1.render(f5);
        this.rod2.render(f5);
        this.rod3.render(f5);
        this.rod4.render(f5);
        this.rod5.render(f5);
        this.rod6.render(f5);
        this.rod7.render(f5);
        this.rod8.render(f5);
        this.rod9.render(f5);

        GL11.glEnable(GL11.GL_CULL_FACE);
        if( (enableCableFlags & 1) == 1 ) { this.cablePlane1.render(f5); }
        if( (enableCableFlags & 2) == 2 ) { this.cablePlane2.render(f5); }
        if( (enableCableFlags & 4) == 4 ) { this.cablePlane3.render(f5); }
        if( (enableCableFlags & 8) == 8 ) { this.cablePlane4.render(f5); }
        if( (enableCableFlags & 16) == 16 ) { this.cablePlane5.render(f5); }
        if( (enableCableFlags & 32) == 32 ) { this.cablePlane6.render(f5); }
        if( (enableCableFlags & 64) == 64 ) { this.cablePlane7.render(f5); }
        if( (enableCableFlags & 128) == 128 ) { this.cablePlane8.render(f5); }
        if( (enableCableFlags & 256) == 256 ) { this.cablePlane9.render(f5); }
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
