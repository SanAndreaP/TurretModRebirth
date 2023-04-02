package de.sanandrew.mods.turretmod.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelItemTransmitter - SanAndreasP
 * Created using Tabula 4.1.1
 */
public class ModelItemTransmitter extends ModelBase {
    public ModelRenderer floor;
    public ModelRenderer connectorN;
    public ModelRenderer connectorS;
    public ModelRenderer connectorE;
    public ModelRenderer connectorW;
    public ModelRenderer coreBottom;
    public ModelRenderer coreTop;
    public ModelRenderer connectorPipeN;
    public ModelRenderer connectorPipeS;
    public ModelRenderer connectorPipeE;
    public ModelRenderer connectorPipeW;

    public ModelItemTransmitter() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.coreBottom = new ModelRenderer(this, 0, 27);
        this.coreBottom.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.coreBottom.addBox(-4.0F, -2.0F, -4.0F, 8, 8, 8, 0.0F);
        this.floor = new ModelRenderer(this, 0, 0);
        this.floor.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.floor.addBox(-8.0F, 6.0F, -8.0F, 16, 2, 16, 0.0F);
        this.connectorPipeE = new ModelRenderer(this, 52, 0);
        this.connectorPipeE.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorPipeE.addBox(-7.5F, 1.0F, -1.0F, 4, 2, 2, 0.0F);
        this.connectorW = new ModelRenderer(this, 46, 19);
        this.connectorW.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorW.addBox(7.0F, -2.0F, -4.0F, 1, 8, 8, 0.0F);
        this.connectorPipeW = new ModelRenderer(this, 52, 4);
        this.connectorPipeW.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorPipeW.addBox(3.5F, 1.0F, -1.0F, 4, 2, 2, 0.0F);
        this.connectorN = new ModelRenderer(this, 0, 18);
        this.connectorN.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorN.addBox(-4.0F, -2.0F, -8.0F, 8, 8, 1, 0.0F);
        this.connectorPipeN = new ModelRenderer(this, 0, 0);
        this.connectorPipeN.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorPipeN.addBox(-1.0F, 1.0F, -7.5F, 2, 2, 4, 0.0F);
        this.connectorS = new ModelRenderer(this, 18, 18);
        this.connectorS.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorS.addBox(-4.0F, -2.0F, 7.0F, 8, 8, 1, 0.0F);
        this.coreTop = new ModelRenderer(this, 32, 35);
        this.coreTop.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.coreTop.addBox(-2.5F, -8.0F, -2.5F, 5, 6, 5, 0.0F);
        this.connectorPipeS = new ModelRenderer(this, 0, 6);
        this.connectorPipeS.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorPipeS.addBox(-1.0F, 1.0F, 3.5F, 2, 2, 4, 0.0F);
        this.connectorE = new ModelRenderer(this, 28, 19);
        this.connectorE.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.connectorE.addBox(-8.0F, -2.0F, -4.0F, 1, 8, 8, 0.0F);
    }

    public void render(float f5) {
        this.coreBottom.render(f5);
        this.floor.render(f5);
        this.connectorPipeE.render(f5);
        this.connectorW.render(f5);
        this.connectorPipeW.render(f5);
        this.connectorN.render(f5);
        this.connectorPipeN.render(f5);
        this.connectorS.render(f5);
        this.coreTop.render(f5);
        this.connectorPipeS.render(f5);
        this.connectorE.render(f5);
    }
}
