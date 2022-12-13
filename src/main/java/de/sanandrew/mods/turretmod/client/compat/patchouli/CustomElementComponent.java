package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.DummyGui;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public abstract class CustomElementComponent
        implements ICustomComponent
{
    private int x;
    private int y;

    private GuiElementInst elem;

    private final BiFunction<IGui, JsonObject, IGuiElement> onLoad;

    public CustomElementComponent(BiFunction<IGui, JsonObject, IGuiElement> onLoad) {
        this.onLoad = onLoad;
    }

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        this.elem.initialize(DummyGui.INSTANCE);
        this.elem.get().setup(DummyGui.INSTANCE, this.elem);
    }

    @Override
    public void render(@Nonnull MatrixStack ms, @Nonnull IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if( this.elem.isVisible() ) {
            this.elem.get().render(DummyGui.INSTANCE, ms, pticks, this.x, this.y, mouseX, mouseY, this.elem);
        }
//        if( this.scale != 0.0F ) {
////            gui.get().getMinecraft().getTextureManager().bind(this.txLocation);
////            stack.pushPose();
////            RenderSystem.enableBlend();
////            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
////            stack.translate((double)x, (double)y, 0.0);
////            stack.scale(this.scale[0], this.scale[1], 1.0F);
////            RenderSystem.color4f(this.color.fRed(), this.color.fGreen(), this.color.fBlue(), this.color.fAlpha());
////            this.drawRect(gui, stack);
////            stack.popPose();
////            context.getGui().getMinecraft().renderEngine.bindTexture(this.resource);
////            GlStateManager.pushMatrix();
////            GlStateManager.translate((float) this.x, (float) this.y, this.zIndex);
////            GlStateManager.scale(this.scale, this.scale, this.scale);
////            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
////            GlStateManager.enableBlend();
////            Gui.drawModalRectWithCustomSizedTexture(0, 0, (float) this.u, (float) this.v, this.width, this.height, (float) this.textureWidth, (float) this.textureHeight);
////            GlStateManager.popMatrix();
//        }

    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        JsonObject elemData = lookup.apply(IVariable.empty()).unwrap().getAsJsonObject();

        this.elem = new GuiElementInst(this.onLoad.apply(DummyGui.INSTANCE, elemData));
        this.elem.data = elemData;
    }
}
