package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.page.PageEntity;

import java.lang.ref.WeakReference;

public class ComponentTurretEntity
        implements ICustomComponent
{
    private int x;
    private int y;
    @VariableHolder
    @SerializedName("turret")
    public String turretId;

    transient private ITurret                     turret;
    transient private WeakReference<EntityTurret> turretCache;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        this.turret = TurretRegistry.INSTANCE.getObject(new ResourceLocation(this.turretId));
        this.turretCache = null;
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        Gui.drawRect(this.x, this.y, this.x + 46, this.y + 60, 0xFFFF0000);
        Gui.drawRect(this.x, this.y, this.x + 23, this.y + 60, 0xFFFFFF00);
        drawTurret(Minecraft.getMinecraft(), this.x, this.y, partTicks, false);
    }


    @SuppressWarnings("SameParameterValue")
    private void drawTurret(Minecraft mc, int x, int y, float partTicks, boolean bouncy) {
        if( this.turretCache == null || this.turretCache.get() == null || this.turretCache.isEnqueued() ) {
            try {
                this.turretCache = new WeakReference<>(new EntityTurret(mc.world, this.turret));
            } catch( Exception e ) {
                return;
            }
        }

        EntityTurret turret = this.turretCache.get();
        if( turret == null ) {
            return;
        }

        turret.inGui = true;

        float rotation = (ClientTicker.total - ClientTicker.delta) + ClientTicker.delta * partTicks;
//        float rotation = 45F;

        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 23.0F, y + 52.0F, 500.0F);
        GlStateManager.scale(25.0F, 25.0F, 25.0F);

        GlStateManager.rotate(180.0F + (float) (bouncy ? Math.sin(rotation * 0.25F) * 10.0F : 0.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(22.5F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(135.0F + rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        if( bouncy ) {
            GlStateManager.scale(1.0F, 0.9F + Math.sin(rotation * 0.5F) * 0.1F, 1.0F);
        }
        Minecraft.getMinecraft().getRenderManager().setRenderShadow(false);
        Minecraft.getMinecraft().getRenderManager().renderEntity(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        Minecraft.getMinecraft().getRenderManager().setRenderShadow(true);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
