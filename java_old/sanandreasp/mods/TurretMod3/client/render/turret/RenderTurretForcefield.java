package sanandreasp.mods.TurretMod3.client.render.turret;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.Random;

public class RenderTurretForcefield extends RenderTurret_Base {
	private static long rndSeed = 0L;
    public static final ResourceLocation TEX_SHIELD		= new ResourceLocation("turretmod3:textures/entities/shield_1.png");

	public RenderTurretForcefield(ModelBase par1ModelBase) {
		super(par1ModelBase);
	}

	@Override
	public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
		EntityTurret_TSForcefield fsTurret = ((EntityTurret_TSForcefield)par1EntityLiving);
		if (!fsTurret.isActive() || fsTurret.isInGui())
			return;
        Tessellator tessellator = Tessellator.instance;

        if (fsTurret.isShieldOnline()) {
			rndSeed++;
	        Random random = new Random(rndSeed);
	        GL11.glPushMatrix();
	        GL11.glTranslatef((float)par2, (float)par4+2.85F, (float)par6);
	        GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
	        float f2 = 0.010F;
	        GL11.glScalef(f2, f2, f2);
	        GL11.glTranslatef(0.5F, 0.3125F, 0.0F);
	        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
	        renderLightning(tessellator);
	        GL11.glColor3f(1F, 1F, 1F);
	        GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
	        GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
	        GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
	        GL11.glScalef(1.0F / f2, 1.0F / f2, 1.0F / f2);
	        GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		char var5 = 0x000F0;
		int var6 = var5 % 65536;
		int var7 = var5 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6 / 1.0F, var7 / 1.0F);
        renderShield(tessellator, ((EntityTurret_Base)par1EntityLiving).wdtRange, (EntityTurret_TSForcefield)par1EntityLiving, par9);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
	}

	public static void renderShield(Tessellator tess, double size, EntityTurret_TSForcefield fsTurret, double partialTicks) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(TEX_SHIELD);
		double texScaleX = 1.25D;
		double texScaleY = 1.25D;
		double ticks = (double)fsTurret.ticksExisted + partialTicks;

		float alpha = Math.min(0.50F, fsTurret.isShieldOnline() ? 1F : ((float)fsTurret.getShieldPts() / ((float)fsTurret.getMaxShieldPts()/2F))*0.5F);
		float whiteness = 1F;

		if ((float)fsTurret.getShieldPts() / (float)fsTurret.getMaxShieldPts() < 0.2F && fsTurret.isShieldOnline()) {
			whiteness = 1F - (float) Math.abs(Math.sin((ticks * 0.1D) % 180D) * 0.6F);
		} else if (!fsTurret.isShieldOnline()) {
			whiteness = 0F;
		}

		for (int i = 0; i < 8; i++) {
			GL11.glPushMatrix();
			  GL11.glRotatef(45F*(float)i - (float)(ticks * 0.5D), 0F, 1F, 0F);
              GL11.glDisable(GL11.GL_LIGHTING);
			  tess.startDrawing(7);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, 0D, size, 0.0D, 0.0D);
			  tess.addVertexWithUV(size*0.7071D, 0D, size*0.7071D, 0.0D, texScaleY);
			  tess.addVertexWithUV(size*0.7071D*0.7071D, size*0.7071D, size*0.7071D*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(0D, size*0.7071D, size*0.7071D, texScaleX, 0.0D);
			  tess.draw();
			  tess.startDrawing(7);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, size*0.7071D, size*0.7071D, texScaleX, 0.0D);
			  tess.addVertexWithUV(size*0.7071D*0.7071D, size*0.7071D, size*0.7071D*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(size*0.7071D, 0D, size*0.7071D, 0.0D, texScaleY);
			  tess.addVertexWithUV(0D, 0D, size, 0.0D, 0.0D);
			  tess.draw();
			  tess.startDrawing(5);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, size*0.7071D, size*0.7071D, texScaleX, 0.0D);
			  tess.addVertexWithUV(size*0.7071D*0.7071D, size*0.7071D, size*0.7071D*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(0D, size, 0D, 0D, texScaleY);
			  tess.draw();
			  tess.startDrawing(5);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, size, 0D, 0D, texScaleY);
			  tess.addVertexWithUV(size*0.7071D*0.7071D, size*0.7071D, size*0.7071D*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(0D, size*0.7071D, size*0.7071D, texScaleX, 0.0D);
			  tess.draw();
			  tess.startDrawing(5);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, -0.5D, 0D, 0.0D, texScaleY);
			  tess.addVertexWithUV(size*0.7071D, 0D, size*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(0D, 0D, size, texScaleX, 0.0D);
			  tess.draw();
			  tess.startDrawing(5);
			  tess.setColorRGBA_F(1F, whiteness, whiteness, alpha);
			  tess.addVertexWithUV(0D, 0D, size, texScaleX, 0.0D);
			  tess.addVertexWithUV(size*0.7071D, 0D, size*0.7071D, texScaleX, texScaleY);
			  tess.addVertexWithUV(0D, -0.5D, 0D, 0.0D, texScaleY);
			  tess.draw();
              GL11.glEnable(GL11.GL_LIGHTING);

			GL11.glPopMatrix();
		}
	}

	public static void renderLightning(Tessellator tessellator)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        double ad[] = new double[8];
        double ad1[] = new double[8];
        double d = 0.0D;
        double d1 = 0.0D;
        Random random = new Random(rndSeed);

        for (int i = 7; i >= 0; i--)
        {
            ad[i] = d;
            ad1[i] = d1;
            d += random.nextInt(11) - 5;
            d1 += random.nextInt(11) - 5;
        }

        for (int j = 0; j < 4; j++)
        {
            Random random1 = new Random(rndSeed);

            for (int k = 0; k < 3; k++)
            {
                int l = 7;
                int i1 = 0;

                if (k > 0)
                {
                    l = 7 - k;
                }

                if (k > 0)
                {
                    i1 = l - 2;
                }

                double d2 = ad[l] - d;
                double d3 = ad1[l] - d1;

                for (int j1 = l-0; j1 >= i1; j1--)
                {
                    double d4 = d2;
                    double d5 = d3;

                    if (k == 0)
                    {
                        d2 += random1.nextInt(10) - 5;
                        d3 += random1.nextInt(10) - 5;
                    }
                    else
                    {
                        d2 += random1.nextInt(31) - 15;
                        d3 += random1.nextInt(31) - 15;
                    }

                    tessellator.startDrawing(5);
                    float f = 0.5F;
                    tessellator.setColorRGBA_F(0.9F * f, 0.2F * f, 0.2F * f, 0.3F);
                    double d6 = 0.10000000000000001D + (double)j * 0.20000000000000001D;

                    if (k == 0)
                    {
                        d6 *= (double)j1 * 0.10000000000000001D + 1.0D;
                    }

                    double d7 = 0.10000000000000001D + (double)j * 0.20000000000000001D;

                    if (k == 0)
                    {
                        d7 *= (double)(j1 - 1) * 0.10000000000000001D + 1.0D;
                    }

                    for (int k1 = 0; k1 < 5; k1++)
                    {
                        double d8 = 0.5D - d6;
                        double d9 = 0.5D - d6;

                        if (k1 == 1 || k1 == 2)
                        {
                            d8 += d6 * 2D;
                        }

                        if (k1 == 2 || k1 == 3)
                        {
                            d9 += d6 * 2D;
                        }

                        double d10 = 0.5D - d7;
                        double d11 = 0.5D - d7;

                        if (k1 == 1 || k1 == 2)
                        {
                            d10 += d7 * 2D;
                        }

                        if (k1 == 2 || k1 == 3)
                        {
                            d11 += d7 * 2D;
                        }

                        tessellator.addVertex(d10 + d2, j1 * 16, d11 + d3);
                        tessellator.addVertex(d8 + d4, (j1 + 1) * 16, d9 + d5);
                    }

                    tessellator.draw();
                }
            }
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

	@Override
	protected void renderStats(EntityTurret_Base par1Turret, double par2, double par4, double par6) {
		this.renderStats((EntityTurret_TSForcefield)par1Turret, par2, par4, par6);
	}

    private void renderStats(EntityTurret_TSForcefield par1Turret, double par2, double par4, double par6)
    {
        if (Minecraft.isGuiEnabled() && TM3ModRegistry.proxy.getPlayerTM3Data(Minecraft.getMinecraft().thePlayer).getBoolean("renderLabels") && !par1Turret.isInGui())
        {
            float var8 = 1.0F;
            float var9 = 0.016666668F * var8;
            double var10 = par1Turret.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float var12 = TM3ModRegistry.labelRenderRange;

            if (var10 < (double)(var12 * var12))
            {
                String var13 = par1Turret.getTurretName();
                String var131 = par1Turret.getPlayerName();

                FontRenderer var14 = this.getFontRendererFromRenderManager();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)par2 + 0.0F, (float)par4 + 2.8F, (float)par6);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-var9, -var9, var9);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Tessellator var15 = Tessellator.instance;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                var15.startDrawingQuads();

                // name render bkg
                int var16 = var14.getStringWidth(var13) / 2;
                var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                var15.addVertex((double)(-var16 - 1), -1.0D, 0.0D);
                var15.addVertex((double)(-var16 - 1), 8.0D, 0.0D);
                var15.addVertex((double)(var16 + 1), 8.0D, 0.0D);
                var15.addVertex((double)(var16 + 1), -1.0D, 0.0D);

                var16 = var14.getStringWidth(var131) / 2;
                var15.addVertex((double)(-var16 - 1), 17.0D, 0.0D);
                var15.addVertex((double)(-var16 - 1), 26.0D, 0.0D);
                var15.addVertex((double)(var16 + 1), 26.0D, 0.0D);
                var15.addVertex((double)(var16 + 1), 17.0D, 0.0D);

                var15.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDepthMask(true);
                var14.drawString(var13, -var14.getStringWidth(var13) / 2, 0, 0xFFFFFF);
                var14.drawString(var131, -var14.getStringWidth(var131) / 2, 18, 0xBBBBBB);
                GL11.glDisable(GL11.GL_TEXTURE_2D);

                double health = ((double)par1Turret.getSrvHealth() / (double)par1Turret.getMaxHealth()) * 50D - 25D;
                double shield = ((double)par1Turret.getShieldPts() / (double)par1Turret.getMaxShieldPts()) * 50D - 25D;

                //bars bkg
                var15.startDrawingQuads();
                var15.setColorRGBA_F(0F, 0F, 0F, 1F);
                var15.addVertex(health, 9D, 0D);
                var15.addVertex(health, 11D, 0D);
                var15.addVertex(25D, 11D, 0D);
                var15.addVertex(25D, 9D, 0D);

                var15.addVertex(shield, 11.5D, 0D);
                var15.addVertex(shield, 13.5D, 0D);
                var15.addVertex(25D, 13.5D, 0D);
                var15.addVertex(25D, 11.5D, 0D);

                var15.addVertex(-25.5D, 8.5D, 0D);
                var15.addVertex(-25.5D, 9D, 0D);
                var15.addVertex(25.5D, 9D, 0D);
                var15.addVertex(25.5D, 8.5D, 0D);

                var15.addVertex(-25.5D, 8.5D, 0D);
                var15.addVertex(-25.5D, 11.5D, 0D);
                var15.addVertex(-25D, 11.5D, 0D);
                var15.addVertex(-25D, 8.5D, 0D);
                var15.addVertex(25D, 8.5D, 0D);
                var15.addVertex(25D, 11.5D, 0D);
                var15.addVertex(25.5D, 11.5D, 0D);
                var15.addVertex(25.5D, 8.5D, 0D);
                var15.addVertex(-25D, 11D, 0D);
                var15.addVertex(-25D, 11.5D, 0D);
                var15.addVertex(25D, 11.5D, 0D);
                var15.addVertex(25D, 11D, 0D);

                var15.addVertex(-25.5D, 11D, 0D);
                var15.addVertex(-25.5D, 14D, 0D);
                var15.addVertex(-25D, 14D, 0D);
                var15.addVertex(-25D, 11D, 0D);
                var15.addVertex(25D, 11D, 0D);
                var15.addVertex(25D, 14D, 0D);
                var15.addVertex(25.5D, 14D, 0D);
                var15.addVertex(25.5D, 11D, 0D);
                var15.addVertex(-25D, 13.5D, 0D);
                var15.addVertex(-25D, 14D, 0D);
                var15.addVertex(25D, 14D, 0D);
                var15.addVertex(25D, 13.5D, 0D);

                //health bar
                var15.setColorRGBA_F(1F, 0F, 0F, 1F);
                var15.addVertex(-25D, 9D, 0D);
                var15.addVertex(-25D, 11D, 0D);
                var15.addVertex(health, 11D, 0D);
                var15.addVertex(health, 9D, 0D);

                //exp bar
                var15.setColorRGBA_F(1F, 1F, 0F, 1F);
                var15.addVertex(-25D, 11.5D, 0D);
                var15.addVertex(-25D, 13.5D, 0D);
                var15.addVertex(shield, 13.5D, 0D);
                var15.addVertex(shield, 11.5D, 0D);

                var15.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }
    }
}
