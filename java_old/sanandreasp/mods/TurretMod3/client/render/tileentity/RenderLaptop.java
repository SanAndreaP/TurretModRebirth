package sanandreasp.mods.TurretMod3.client.render.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sanandreasp.mods.turretmod3.block.BlockLaptop;
import sanandreasp.mods.turretmod3.client.model.ModelLaptop;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class RenderLaptop extends TileEntitySpecialRenderer {

	public ModelLaptop base = new ModelLaptop();
	public ModelLaptop illuminated = new ModelLaptop();

	public void renderTileEntityLapAt(TileEntityLaptop par1TileEntityLaptop, double par2, double par4, double par6, float par8)
    {
        int i;

        if (!par1TileEntityLaptop.hasWorldObj()) {
            i = 0;
        } else {
            i = BlockLaptop.getRotation(par1TileEntityLaptop.getBlockMetadata());
        }

        switch(BlockLaptop.getType(par1TileEntityLaptop.getBlockMetadata())) {
	        case 0:
	        	this.bindTexture(TM3ModRegistry.TEX_WHITELAP);
	        	break;
	        case 1:
	        	this.bindTexture(TM3ModRegistry.TEX_BLACKLAP);
	        	break;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short short1 = 0;

        if (i == 2)
        {
            short1 = 180;
        }

        if (i == 3)
        {
            short1 = 0;
        }

        if (i == 4)
        {
            short1 = 90;
        }

        if (i == 5)
        {
            short1 = -90;
        }

        GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0F, -1F, 0F);
        float f1 = par1TileEntityLaptop.prevScreenAngle + (par1TileEntityLaptop.screenAngle - par1TileEntityLaptop.prevScreenAngle) * par8;
        float f2;

        f1 = 1.0433F - f1;
        f1 = 1.133F - f1 * f1 * f1;
        this.base.LaptopScreen.rotateAngleX =
        this.illuminated.LaptopScreen.rotateAngleX = -(f1 * (float)Math.PI / 2.0F) + ((float)Math.PI / 2F);
        this.base.renderBlock();

	    this.bindTexture(new ResourceLocation(TM3ModRegistry.TEX_LAPGLOW + "(" + Integer.toString(par1TileEntityLaptop.randomLightmap) + ").png"));

        if (par1TileEntityLaptop.screenAngle >= 0.9999F) {
	        GL11.glPushMatrix();
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			char var5 = 0x000F0;
			int var6 = var5 % 65536;
			int var7 = var5 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6 / 1.0F, var7 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.illuminated.renderBlock();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntityLapAt((TileEntityLaptop)par1TileEntity, par2, par4, par6, par8);
    }

}
