package sanandreasp.mods.TurretMod3.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.inventory.ContainerDismantleStorage;

public class GuiDismantledStorage extends GuiContainer {
    private String invName;
    private String playerInvName;
    private Entity instance;

	public GuiDismantledStorage(ContainerDismantleStorage par1Container, Entity e) {
		super(par1Container);
        this.allowUserInput = false;
        this.invName = par1Container.getInvName();
        this.playerInvName = par1Container.getPInvName();
        short var3 = 222;
        int var4 = var3 - 108;
        this.ySize = var4 + 3 * 18;
        this.instance = e;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		if (this.instance == null || this.instance.isDead ) {
			mc.thePlayer.closeScreen();
			return;
		}
		super.drawScreen(par1, par2, par3);
	}

	@Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
		this.mc.getTextureManager().bindTexture(field_147001_a);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(var5, var6 + 3 * 18 + 17, 0, 126, this.xSize, 96);
    }

	@Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal(invName), 8, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal(playerInvName), 8, this.ySize - 96 + 2, 4210752);
    }
}
