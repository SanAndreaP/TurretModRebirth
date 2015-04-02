package sanandreasp.mods.TurretMod3.client.gui.laptop;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;
import sanandreasp.mods.turretmod3.packet.PacketRecvLaptopMisc;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class GuiLaptopMisc extends GuiLaptop_Base {
	private GuiTextField frequency;
	private GuiTextField customName;

	public GuiLaptopMisc(Container lapContainer, TileEntityLaptop par2TileEntityLaptop) {
		super(lapContainer, par2TileEntityLaptop);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.tabMisc.enabled = false;

		this.frequency = new GuiTextField(this.fontRendererObj, this.guiLeft + (this.xSize-150)/2, this.guiTop + 40, 150, 10);
		this.frequency.setText("0");
		this.customName = new GuiTextField(this.fontRendererObj, this.guiLeft + (this.xSize-150)/2, this.guiTop + 70, 150, 10);
		this.customName.setText("");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.laptop.titMisc"), this.guiLeft + 6, this.guiTop + 6, 0x808080);

		String s = StatCollector.translateToLocal("gui.tcu.frequency");
		this.fontRendererObj.drawString(s, this.guiLeft + 40, this.guiTop + 30, 0x606060);
		s = StatCollector.translateToLocal("gui.laptop.customName");
		this.fontRendererObj.drawString(s, this.guiLeft + 40, this.guiTop + 60, 0x606060);

		this.frequency.drawTextBox();
		this.customName.drawTextBox();
	}

    @Override
	public void updateScreen()
    {
    	super.updateScreen();
    	this.frequency.updateCursorCounter();
    	this.customName.updateCursorCounter();
    }

    @Override
	protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.frequency.mouseClicked(par1, par2, par3);
        this.customName.mouseClicked(par1, par2, par3);
    }

    @Override
	protected void keyTyped(char par1, int par2)
    {
    	this.frequency.textboxKeyTyped(par1, par2);
    	this.customName.textboxKeyTyped(par1, par2);

    	if ((par2 == 28 || par2 == 1) && (this.frequency.isFocused() || this.customName.isFocused())) {
    		this.frequency.setFocused(false);
    		this.customName.setFocused(false);
    	}
    	else if ((par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) && !this.frequency.isFocused() && !this.customName.isFocused()) {
    		this.mc.thePlayer.closeScreen();
    	}
    }

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		super.actionPerformed(par1GuiButton);
		if (par1GuiButton.id == this.programTurret.id) {
            try{
				TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvLaptopMisc(this.laptop, this.customName.getText(), Short.valueOf(this.frequency.getText())));
			} catch (NumberFormatException ignored) {

			}
        	this.inventorySlots.detectAndSendChanges();
		}
	}
}
