package sanandreasp.mods.TurretMod3.client.gui.turretInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class GuiTabNav extends GuiButton {
    protected boolean isUp;

	public GuiTabNav(int par1, int par2, int par3, boolean par6) {
		super(par1, par2, par3, "");
		this.width = 23;
		this.height = 13;
		this.isUp = par6;
	}

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            par1Minecraft.renderEngine.bindTexture(TM3ModRegistry.TEX_GUIBUTTONS);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 52+23*(isUp?0:1), var5*13, this.width, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }
}
