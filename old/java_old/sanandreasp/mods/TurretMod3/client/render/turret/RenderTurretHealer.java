package sanandreasp.mods.TurretMod3.client.render.turret;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSHealer;

public class RenderTurretHealer extends RenderTurret_Base {
    public static final ResourceLocation TEX_HEALBEAM = new ResourceLocation("turretmod3:textures/entities/healBeam.png");

	public RenderTurretHealer(ModelBase par1ModelBase) {
		super(par1ModelBase);
	}

	@Override
	public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
		EntityTurret_TSHealer healer = (EntityTurret_TSHealer)par1EntityLiving;
		if (healer.getCurrentTargetStr() != null && healer.getCurrentTargetStr().length() > 0 && healer.getAmmo() > 0) {
			renderHealBeam(healer, par2, par4, par6, par8, par9);
		}
	}

	public void renderHealBeam(EntityTurret_TSHealer par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
		EntityLiving target = (EntityLiving) Minecraft.getMinecraft().theWorld.getEntityByID(par1Entity.getTargetEID());
		if (target == null) return;

		float rotYaw = par1Entity.rotationYawHead;
		float rotPtc = par1Entity.rotationPitch;

		par1Entity.faceEntity(target, 10.0F, par1Entity.getVerticalFaceSpeed());
        float var4 = (float)par1Entity.ticksExisted * 8;
        this.bindTexture(TEX_HEALBEAM);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
		char var8 = 0x000F0;
		int var9 = var8 % 65536;
		int var7 = var8 / 65536;
        float var5 = var4 * 0.01F;
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glTranslatef(-var5, 0.0F, 0.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glTranslatef((float)par2, (float)par4 + 1.2F, (float)par6);
        float rotation = par1Entity.rotationYawHead - 270F;
        GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
        GL11.glRotatef(-par1Entity.prevRotationPitch + (par1Entity.rotationPitch - par1Entity.prevRotationPitch), 0.0F, 0.0F, 1.0F);
        Tessellator var10 = Tessellator.instance;
        float var12 = 0.0F;
        float var13 = 3F;
        float var14 = 0F / 32.0F;
        float var15 = 35F / 32.0F;
        float var20 = 0.03F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(var20, var20, var20);
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);

        double dist = par1Entity.getDistanceToEntity(target)*33.2D;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
				var9 / 1.0F, var7 / 1.0F);

        for (int var23 = 0; var23 < 4; ++var23)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, var20);
            var10.startDrawingQuads();
            var10.setColorRGBA_F(1F, 1F, 1F, 1F);
            var10.addVertexWithUV(0.0D, -2.0D, 0.0D, (double)var12, (double)var14);
            var10.addVertexWithUV(dist, -2.0D, 0.0D, (double)var13, (double)var14);
            var10.addVertexWithUV(dist, 2.0D, 0.0D, (double)var13, (double)var15);
            var10.addVertexWithUV(0.0D, 2.0D, 0.0D, (double)var12, (double)var15);
            var10.draw();
        }

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        par1Entity.rotationYawHead = rotYaw;
        par1Entity.rotationPitch = rotPtc;
    }

}
