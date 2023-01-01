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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.function.UnaryOperator;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({ "unused", "java:S2065", "FieldCanBeLocal", "FieldMayBeFinal" })
public class TurretEntityComponent
        implements ICustomComponent
{
    private transient int    x;
    private transient int     y;
    private transient boolean bouncy;
    private transient ITurret turretIntern;
    private transient float scaleIntern;
    private transient float offsetIntern;

    private transient WeakReference<TurretEntity> turretCache;

    private String turret;
    private String scale;
    private String offset;
    private float   defRotation = 45.0F;
    private boolean rotate      = true;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        Calendar c = Calendar.getInstance();
        this.bouncy = c.get(Calendar.DAY_OF_MONTH) == 1 && c.get(Calendar.MONTH) == Calendar.APRIL;

        this.turretCache = null;
    }

    @Override
    public void render(@Nonnull MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        Minecraft mc = context.getGui().getMinecraft();
        mc.getTextureManager().bind(Resources.TEXTURE_GUI_LEXICON_ELEMENTS);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        AbstractGui.blit(ms, this.x, this.y, 0, 0, 46, 60, 256, 256);
        drawTurret(mc, ms, this.x, this.y, context.getTicksInBook(), pticks);
    }


    @SuppressWarnings({ "SameParameterValue", "deprecation" })
    private void drawTurret(Minecraft mc, MatrixStack ms, int x, int y, int bookTick, float partTicks) {
        if( this.turretCache == null || this.turretCache.get() == null || this.turretCache.isEnqueued() ) {
            try {
                this.turretCache = new WeakReference<>(new TurretEntity(mc.level, this.turretIntern));
            } catch( Exception e ) {
                return;
            }
        }

        TurretEntity luke = this.turretCache.get();
        if( luke == null ) {
            return;
        }

        luke.inGui = true;

        float rotation = this.rotate || this.bouncy ? bookTick + partTicks : this.defRotation;
        ms.pushPose();
        ms.translate(x + 23.0F, y + 47.0F + this.offsetIntern, 500.0F);
        ms.scale(this.scaleIntern * 20.0F, this.scaleIntern * 20.0F, this.scaleIntern * 20.0F);

        ms.mulPose(Vector3f.ZP.rotationDegrees(180.0F + (float) (this.bouncy ? Math.sin(rotation * 0.25F) * 10.0F : 0.0F)));
        ms.mulPose(Vector3f.XP.rotationDegrees(22.5F));
        ms.mulPose(Vector3f.YP.rotationDegrees(135.0F + (this.rotate ? rotation : this.defRotation)));
        ms.mulPose(Vector3f.YP.rotationDegrees(-135.0F));

        if( this.bouncy ) {
            ms.scale(1.0F, 0.9F + MathHelper.sin(rotation * 0.5F) * 0.1F, 1.0F);
        }

        EntityRendererManager erm = Minecraft.getInstance().getEntityRenderDispatcher();

        erm.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> erm.render(luke, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, ms, buffer, 0xF000F0));
        buffer.endBatch();
        erm.setRenderShadow(true);

        ms.popPose();
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        this.turretIntern = TurretRegistry.INSTANCE.get(new ResourceLocation(lookup.apply(IVariable.wrap(this.turret)).asString()));
        this.scaleIntern = lookup.apply(IVariable.wrap(this.scale)).asNumber(1.0F).floatValue();
        this.offsetIntern = lookup.apply(IVariable.wrap(this.offset)).asNumber(0.0F).floatValue();
    }
}
