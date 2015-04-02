package sanandreasp.mods.TurretMod3.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class GuiTurretButton extends GuiButton {

	public GuiTurretButton(int par1, int par2, int par3, String par4Str) {
		super(par1, par2, par3, par4Str);
		this.width = 158;
		this.height = 12;
	}

	@Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
        	GL11.glPushMatrix();
        	par1Minecraft.getTextureManager().bindTexture(TM3ModRegistry.TEX_GUIBUTTONS);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 78 + var5 * 12, this.width, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            int var6 = 14737632;

            if (!this.enabled)
            {
                var6 = -6250336;
            }
            else if (this.field_146123_n)
            {
                var6 = 16777120;
            }
            GL11.glPopMatrix();
//            var4.readFontData();
            this.drawCenteredString(par1Minecraft.fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
        }
    }
}
