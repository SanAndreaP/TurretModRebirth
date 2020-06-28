package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ComponentCustomText
        implements ICustomComponent
{
    private int x;
    private int y;

    @VariableHolder
    public String text;
    @VariableHolder
    @SerializedName("color")
    public String colorStr;
    @SerializedName("line_height")
    public int    lineHeight = 9;
    public String alignment  = "left";
    public float  rotation   = 0.0F;

    transient boolean blockyFont;
    transient Integer          color;
    transient int              tx = 0;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;

        try {
            this.color = Integer.parseInt(this.colorStr, 16);
        } catch( NumberFormatException var5 ) {
            this.color = null;
        }
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        FontRenderer font = context.getFont();
        boolean wasUnicode = font.getUnicodeFlag();
        font.setUnicodeFlag(!this.blockyFont);

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x, this.y, 0);
        GlStateManager.rotate(this.rotation, 0.0F, 0.0F, 1.0F);
        font.drawString(this.text, this.tx, 0, MiscUtils.defIfNull(this.color, context::getTextColor), false);
        GlStateManager.popMatrix();

        font.setUnicodeFlag(wasUnicode);
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        GuiScreen gui = context.getGui();
        if( gui instanceof GuiBook ) {
            GuiBook guiBook = (GuiBook) gui;
            FontRenderer font = context.getFont();
            boolean wasUnicode = font.getUnicodeFlag();

            this.blockyFont = guiBook.book.useBlockyFont;
            if( !this.blockyFont ) {
                font.setUnicodeFlag(true);
            }
            if( alignment.equals("right") ) {
                this.tx = -context.getFont().getStringWidth(this.text);
            } else if( this.alignment.equals("center") ) {
                this.tx = -(context.getFont().getStringWidth(this.text) / 2);
            } else {
                this.tx = 0;
            }
            font.setUnicodeFlag(wasUnicode);
        }
    }
}
