package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.TurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.function.UnaryOperator;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
//TODO: fix commented code and add variables...
public class TurretEntityComponent
        implements ICustomComponent
{
    private int    x;
    private int    y;

    private  String turretId;
    private float   defRotation = 45.0F;
    private boolean rotate      = true;
    private float   scale       = 1.0F;
    private float   offset      = 0.0F;

    private ITurret                     turret;
    private WeakReference<TurretEntity> turretCache;
    private boolean                     bouncy;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        Calendar c = Calendar.getInstance();
        this.bouncy = c.get(Calendar.DAY_OF_MONTH) == 1 && c.get(Calendar.MONTH) == Calendar.APRIL;

        this.turret = TurretRegistry.INSTANCE.get(new ResourceLocation(this.turretId));
        this.turretCache = null;
    }

    @Override
    public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        Minecraft mc = context.getGui().getMinecraft();
        mc.getTextureManager().bind(Resources.TEXTURE_GUI_TURRETINFO);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        AbstractGui.blit(ms, this.x, this.y, 0, 0, 46, 60, 256, 256);
        drawTurret(mc, ms, this.x, this.y, context.getTicksInBook(), pticks);
    }


    @SuppressWarnings("SameParameterValue")
    private void drawTurret(Minecraft mc, MatrixStack ms, int x, int y, int bookTick, float partTicks) {
        if( this.turretCache == null || this.turretCache.get() == null || this.turretCache.isEnqueued() ) {
            try {
                this.turretCache = new WeakReference<>(new TurretEntity(mc.level, this.turret));
            } catch( Exception e ) {
                return;
            }
        }

        TurretEntity turret = this.turretCache.get();
        if( turret == null ) {
            return;
        }

        turret.inGui = true;

        float rotation = this.defRotation;//this.rotate || this.bouncy ? (ClientTicker.total - ClientTicker.delta) + ClientTicker.delta * partTicks : this.defRotation;

        InventoryScreen.renderEntityInInventory(x+23, (int)(y+47+this.offset), (int)(this.scale * 20), x, y, turret);
////        GlStateManager.enableColorMaterial();
////        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        ms.pushPose();
//        ms.translate(x + 23.0F, y + 47.0F + this.offset, 500.0F);
//        ms.scale(this.scale * 20.0F, this.scale * 20.0F, this.scale * 20.0F);
//
//        ms.mulPose(Vector3f.ZP.rotationDegrees(180.0F + (float) (this.bouncy ? Math.sin(rotation * 0.25F) * 10.0F : 0.0F)));
//        ms.mulPose(Vector3f.XP.rotationDegrees(22.5F));
//        ms.mulPose(Vector3f.YP.rotationDegrees(135.0F + (this.rotate ? rotation : this.defRotation)));
////        RenderHelper.enableStandardItemLighting();
//        ms.mulPose(Vector3f.YP.rotationDegrees(-135.0F));
//
//
////        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
////        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
////        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;
//
//        if( this.bouncy ) {
//            ms.scale(1.0F, 0.9F + MathHelper.sin(rotation * 0.5F) * 0.1F, 1.0F);
//        }
//        mc.getEntityRenderDispatcher().setRenderShadow(false);
//        mc.getEntityRenderDispatcher().render(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, ms, RenderType.entitySolid(turret.getDelegate().getBaseTexture(turret)), true);
//        mc.getEntityRenderDispatcher().setRenderShadow(true);
//
//        ms.popPose();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//        GlStateManager.disableTexture2D();
//        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        this.turretId = lookup.apply(IVariable.wrap(this.turretId)).asString()
        ;
    }
}
