package sanandreasp.mods.TurretMod3.client.gui.laptop;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import sanandreasp.mods.turretmod3.item.ItemTurret;
import sanandreasp.mods.turretmod3.packet.PacketRecvLaptopUpgrades;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class GuiLaptopUpgrades extends GuiLaptop_Base {

	public GuiLaptopUpgrades(Container lapContainer, TileEntityLaptop par2TileEntityLaptop) {
		super(lapContainer, par2TileEntityLaptop);
		this.site = 3;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.tabUpgrades.enabled = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		RenderHelper.disableStandardItemLighting();
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.laptop.upgItems"), 57, 45, 0x808080);
        RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		for (int k = 0; k < 8; k++) {
			int tsID = (k < 4) ? k*2 : 7-(k%4)*2;
			ItemStack turretSlot = this.laptop.getStackInSlot(tsID);
			ItemStack upgSlot = this.laptop.getStackInSlot(k+8);

			if (ItemTurret.isUpgradeValid(turretSlot, upgSlot, ItemTurret.getUpgItems(turretSlot))) {
				for (int i = 0; i < this.greenHighlight[k].length; i++) {
					drawRect(
							this.guiLeft + this.greenHighlight[k][i][0],
							this.guiTop + this.greenHighlight[k][i][1],
							this.guiLeft + this.greenHighlight[k][i][2],
							this.guiTop + this.greenHighlight[k][i][3],
							0x80008000
					);
				}
			} else if (turretSlot != null && upgSlot != null) {
				for (int i = 0; i < this.greenHighlight[k].length; i++) {
					drawRect(
							this.guiLeft + this.greenHighlight[k][i][0],
							this.guiTop + this.greenHighlight[k][i][1],
							this.guiLeft + this.greenHighlight[k][i][2],
							this.guiTop + this.greenHighlight[k][i][3],
							0x80800000
					);
				}
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.laptop.titUpgrades"), this.guiLeft + 6, this.guiTop + 6, 0x808080);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		super.actionPerformed(par1GuiButton);
		if (par1GuiButton.id == this.programTurret.id) {
			this.checkUpgradesAndApply();
        	this.inventorySlots.detectAndSendChanges();
		}
	}

	private void checkUpgradesAndApply() {
		for (int k = 0; k < 8; k++) {
			int tsID = (k < 4) ? k*2 : 7-(k%4)*2;
			ItemStack turretSlot = this.laptop.getStackInSlot(tsID);
			ItemStack upgSlot = this.laptop.getStackInSlot(k+8);

			if (turretSlot != null && upgSlot != null && ItemTurret.isUpgradeValid(turretSlot, upgSlot, ItemTurret.getUpgItems(turretSlot))) {
				TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvLaptopUpgrades(this.laptop, tsID, k+8));
			}
		}
	}

	private int[][][] greenHighlight = new int[][][] {
			{
				{57, 56, 73, 72},
				{64, 72, 66, 86},
				{25, 84, 64, 86},
				{25, 86, 27, 144},
				{23, 144, 27, 146},
				{7, 137, 23, 153},
			},
			{
				{75, 56, 91, 72},
				{82, 72, 84, 89},
				{28, 87, 82, 89},
				{28, 89, 30, 162},
				{23, 162, 30, 164},
				{7, 155, 23, 171},
			},
			{
				{93, 56, 109, 72},
				{100, 72, 102, 92},
				{31, 90, 100, 92},
				{31, 92, 33, 180},
				{23, 180, 33, 182},
				{7, 173, 23, 189},
			},
			{
				{111, 56, 127, 72},
				{118, 72, 120, 95},
				{34, 93, 118, 95},
				{34, 95, 36, 198},
				{23, 198, 36, 200},
				{7, 191, 23, 207},
			},
			{
				{129, 56, 145, 72},
				{136, 72, 138, 93},
				{136, 93, 220, 95},
				{220, 93, 222, 198},
				{220, 198, 233, 200},
				{233, 191, 249, 207},
			},
			{
				{147, 56, 163, 72},
				{154, 72, 156, 90},
				{154, 90, 223, 92},
				{223, 90, 225, 180},
				{223, 180, 233, 182},
				{233, 173, 249, 189},
			},
			{
				{165, 56, 181, 72},
				{172, 72, 174, 87},
				{172, 87, 226, 89},
				{226, 87, 228, 162},
				{226, 162, 233, 164},
				{233, 155, 249, 171},
			},
			{
				{183, 56, 199, 72},
				{190, 72, 192, 84},
				{190, 84, 229, 86},
				{229, 84, 231, 144},
				{229, 144, 233, 146},
				{233, 137, 249, 153},
			},
	};

}
