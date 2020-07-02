package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.base.ClientTicker;

import java.lang.ref.WeakReference;
import java.util.Calendar;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ComponentTurretEntity
        implements ICustomComponent
{
    private int    x;
    private int    y;
    @VariableHolder
    @SerializedName("turret")
    public  String turretId;

    public boolean rotate      = true;
    @SerializedName("default_rotation")
    public float   defRotation = 45.0F;
    public float   scale       = 1.0F;
    public float   offset      = 0.0F;

    transient private ITurret                     turret;
    transient private WeakReference<EntityTurret> turretCache;
    transient private boolean                     bouncy;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        Calendar c = Calendar.getInstance();
        this.bouncy = c.get(Calendar.DAY_OF_MONTH) == 1 && c.get(Calendar.MONTH) == Calendar.APRIL;

        this.turret = TurretRegistry.INSTANCE.getObject(new ResourceLocation(this.turretId));
        this.turretCache = null;
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.TINFO_ELEMENTS.resource);
        Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, 46, 60, 256, 256);
        drawTurret(Minecraft.getMinecraft(), this.x, this.y, partTicks);
    }


    @SuppressWarnings("SameParameterValue")
    private void drawTurret(Minecraft mc, int x, int y, float partTicks) {
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

        float rotation = this.rotate || this.bouncy ? (ClientTicker.total - ClientTicker.delta) + ClientTicker.delta * partTicks : this.defRotation;

        GlStateManager.enableColorMaterial();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 23.0F, y + 47.0F + this.offset, 500.0F);
        GlStateManager.scale(this.scale * 20.0D, this.scale * 20.0D, this.scale * 20.0D);

        GlStateManager.rotate(180.0F + (float) (this.bouncy ? Math.sin(rotation * 0.25F) * 10.0F : 0.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(22.5F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(135.0F + (this.rotate ? rotation : this.defRotation), 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        if( this.bouncy ) {
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
