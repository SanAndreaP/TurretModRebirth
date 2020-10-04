package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

public class ComponentCustomImage
        implements ICustomComponent
{
    private int x;
    private int y;

    @VariableHolder
    public String image;
    public int    u;
    public int    v;
    public int    width;
    public int    height;
    @SerializedName("texture_width")
    public int    textureWidth  = 256;
    @SerializedName("texture_height")
    public int    textureHeight = 256;
    public float  scale         = 1.0F;
    @SerializedName("z_index")
    public float  zIndex        = 0.0F;

    transient ResourceLocation resource;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        this.resource = new ResourceLocation(this.image);
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        if( this.scale != 0.0F ) {
            context.getGui().mc.renderEngine.bindTexture(this.resource);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) this.x, (float) this.y, this.zIndex);
            GlStateManager.scale(this.scale, this.scale, this.scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            Gui.drawModalRectWithCustomSizedTexture(0, 0, (float) this.u, (float) this.v, this.width, this.height, (float) this.textureWidth, (float) this.textureHeight);
            GlStateManager.popMatrix();
        }
    }
}
