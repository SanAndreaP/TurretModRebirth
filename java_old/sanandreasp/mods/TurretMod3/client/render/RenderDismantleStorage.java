package sanandreasp.mods.TurretMod3.client.render;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.client.model.turret.ModelTurret_Base;
import sanandreasp.mods.turretmod3.client.render.turret.RenderTurret_Base;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

import java.util.Iterator;
import java.util.Map;

public class RenderDismantleStorage extends RendererLivingEntity {
    private ResourceLocation CHEST = new ResourceLocation("/item/chest.png");
	public RenderDismantleStorage(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	@Override
	protected void renderModel(EntityLivingBase par1EntityLiving, float par2, float par3, float par4, float par5, float par6, float par7) {
		EntityDismantleStorage stg = (EntityDismantleStorage)par1EntityLiving;
        if (!par1EntityLiving.isInvisible())
        {
            this.bindEntityTexture(par1EntityLiving);
    		GL11.glPushMatrix();
            GL11.glTranslatef(-0.125F, 0.93F, -0.125F);
            GL11.glScalef(0.25F, 0.25F, 0.25F);
            ((ModelChest)this.mainModel).renderAll();
            GL11.glPopMatrix();
            try {
            	EntityTurret_Base turret = (stg.tbClass.getConstructor(World.class).newInstance(stg.worldObj));
                this.bindTexture(turret.getTexture());
                if (stg.tbRender == null) {
                	Map<Class<? extends Entity>, Render> map = Maps.newHashMap();
                	RenderingRegistry.instance().loadEntityRenderers(map);
                	Iterator<Class<? extends Entity>> it = map.keySet().iterator();
                	while(it.hasNext()) {
                		Class<? extends Entity> eC = it.next();
                		if (stg.tbClass.isAssignableFrom(eC)) {
                			stg.tbRender = map.get(eC);
                		}
                	}
                } else {
                	ModelTurret_Base baseMd = (ModelTurret_Base)((RenderTurret_Base)stg.tbRender).getMainModel();
                	baseMd.renderBase();
                }
			} catch (Exception e) { }
        }
        else
        {
            this.mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, par1EntityLiving);
        };
	}

	@Override
	protected float getDeathMaxRotation(EntityLivingBase par1EntityLiving) {
		return 0F;
	}

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return CHEST;
    }
}
