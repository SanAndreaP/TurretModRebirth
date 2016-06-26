package de.sanandrew.mods.turretmod.client.model.block;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * TurretConstructor - SanAndreasP
 * Created using Tabula 4.1.1
 */
public class ModelTurretAssembly
        extends ModelBase
{
    public ModelRenderer base;
    public ModelRenderer robotBinding;
    public ModelRenderer robotArm;
    public ModelRenderer robotHead;

    public ModelTurretAssembly() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.robotBinding = new ModelRenderer(this, 0, 9);
        this.robotBinding.setRotationPoint(2.0F, -4.0F, 5.0F);
        this.robotBinding.addBox(-1.0F, -1.5F, -1.5F, 2, 3, 2, 0.0F);
        this.robotArm = new ModelRenderer(this, 0, 39);
        this.robotArm.setRotationPoint(-0.5F, 0.0F, -9.0F);
        this.robotArm.addBox(0.0F, 0.0F, 0.0F, 1, 1, 12, 0.0F);
        this.robotHead = new ModelRenderer(this, 8, 10);
        this.robotHead.setRotationPoint(0.5F, 0.5F, 0.5F);
        this.robotHead.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(robotHead, 0.7853981633974483F, 0.0F, 0.0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.base.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.base.addChild(this.robotBinding);
        this.robotBinding.addChild(this.robotArm);
        this.robotArm.addChild(this.robotHead);
    }

    public void render(float scale, TileEntityTurretAssembly te, float armX, float armZ) {
        int meta = te.hasWorldObj() ? BlockRegistry.assemblyTable.getDirection(te.getBlockMetadata()).getHorizontalIndex() : 0;
        this.base.rotateAngleY = (float)(90.0D * meta / 180.0D * Math.PI);

        this.robotBinding.rotationPointX = armX;
        this.robotArm.rotationPointZ = armZ;

        this.base.render(scale);
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
