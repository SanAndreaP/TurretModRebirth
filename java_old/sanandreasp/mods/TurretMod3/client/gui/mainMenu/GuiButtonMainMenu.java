package sanandreasp.mods.TurretMod3.client.gui.mainMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiButtonMainMenu extends GuiButton {
	private int color = 0xFFFFFF;
	private int transparency = 0x60;

	public GuiButtonMainMenu(int par1, int par2, int par3, int par4, int par5, String par6Str, int par7Color) {
		super(par1, par2, par3, par4, par5, par6Str);
		this.color = par7Color;
	}

    public GuiButtonMainMenu(int par1, int par2, int par3, String par4Str, int par5Color)
    {
        this(par1, par2, par3, 200, 20, par4Str, par5Color);
    }

    /** sets the transparency of the button (excluding the text) to the value from the parameter.
     * Valid values are  all from 0 to 255 **/
    public GuiButtonMainMenu setBGAlpha(int alpha) {
    	transparency = Math.min(255, Math.max(0, alpha));
    	return this;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var6 = this.getHoverState(var5);

            int var7 = this.color;
            if (!this.enabled)
            {
                var7 = 0x505050;
            }
            else if (var5)
            {
                var7 = 0xFFFFFF;
            }

            int var8Color = ((transparency << 24) & 0xFF000000) + var7;
            this.drawRect(this.xPosition + 1, this.yPosition, this.xPosition + this.width, this.yPosition + 1, var8Color);
            this.drawRect(this.xPosition + this.width - 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height, var8Color);
            this.drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width - 1, this.yPosition + this.height, var8Color);
            this.drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height - 1, var8Color);
            if (var5 && this.enabled)
            	this.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, ((transparency << 24) & 0xFF000000) + this.color);
            this.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, (transparency << 24) & 0xFF000000);
            this.drawRect(this.xPosition + 1, this.yPosition + this.height, this.xPosition + this.width + 1, this.yPosition + this.height + 1, ((transparency/2) << 24) & 0xFF000000);
            this.drawRect(this.xPosition + this.width, this.yPosition + 1, this.xPosition + this.width + 1, this.yPosition + this.height, ((transparency/2) << 24) & 0xFF000000);
            this.mouseDragged(par1Minecraft, par2, par3);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            var4.drawString(this.displayString, this.xPosition + (this.width - var4.getStringWidth(this.displayString)) / 2 + 1, this.yPosition + (this.height - 8) / 2 + 1, 0x60000000);
            GL11.glDisable(GL11.GL_BLEND);
            var4.drawString(this.displayString, this.xPosition + (this.width - var4.getStringWidth(this.displayString)) / 2, this.yPosition + (this.height - 8) / 2, var7);
        }
    }
}
