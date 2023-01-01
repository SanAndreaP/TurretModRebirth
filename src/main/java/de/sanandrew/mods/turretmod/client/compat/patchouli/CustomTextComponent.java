package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nonnull;
import java.util.function.UnaryOperator;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({ "unused", "WeakerAccess", "java:S2065", "FieldCanBeLocal", "FieldMayBeFinal" })
public class CustomTextComponent
        implements ICustomComponent
{
    int x;
    int y;

    private String text;
    private String color;
    private String alignment  = "left";
    private float  rotation   = 0.0F;

    private transient boolean blockyFont;
    private transient Integer         colorIntern;
    private transient int     tx = 0;
    private transient ITextComponent tc;

    public CustomTextComponent() { }

    public CustomTextComponent(String text, String color) {
        this.text = text;
        this.color = color;
    }

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        try {
            this.colorIntern = Integer.parseInt(this.color, 16);
        } catch( NumberFormatException var5 ) {
            this.colorIntern = null;
        }
    }

    @Override
    public void render(@Nonnull MatrixStack ms, @Nonnull IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        ms.pushPose();
        ms.translate(this.x, this.y, 0);
        ms.mulPose(Vector3f.ZP.rotationDegrees(this.rotation));
        context.getGui().getMinecraft().font.draw(ms, this.tc, this.tx, 0, MiscUtils.get(this.colorIntern, context::getTextColor));
        ms.popPose();
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        FontRenderer fr = context.getGui().getMinecraft().font;
        this.tc = new StringTextComponent(this.text).withStyle(context.getFont());

        switch( this.alignment ) {
            case "right": this.tx = -fr.width(this.tc); break;
            case "center": this.tx = -(fr.width(this.tc) / 2); break;
            default: this.tx = 0;
        }
    }

    @Override
    public void onVariablesAvailable(@Nonnull UnaryOperator<IVariable> lookup) {
        this.text = lookup.apply(IVariable.wrap(this.text)).asString();
        this.color = lookup.apply(IVariable.wrap(this.text)).asString("0xFF000000");
    }
}
