package sanandreasp.mods.TurretMod3.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderMobileBase extends RenderLiving {

    public static final ResourceLocation TEX_MOBILEBASE	= new ResourceLocation("turretmod3:textures/entities/mobileBase.png");
	public RenderMobileBase(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

    @Override
	protected void rotateCorpse(EntityLivingBase par1EntityLiving, float par2, float par3, float par4) {
//		super.rotateCorpse(par1EntityLiving, par2, par3, par4);
	}

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return TEX_MOBILEBASE;
    }
}
