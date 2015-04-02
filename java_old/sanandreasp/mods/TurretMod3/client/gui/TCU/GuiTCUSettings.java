package sanandreasp.mods.TurretMod3.client.gui.TCU;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.client.gui.GuiTurretButton;
import sanandreasp.mods.turretmod3.packet.PacketRecvTurretSettings;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgControl;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class GuiTCUSettings extends GuiTCUBase
{
	private GuiButton dismantleTurret;
	private GuiButton toggleUniqueTarget;
	private GuiButton getExperience;
	private GuiButton dismountFromBase;
	private GuiButton rideTurret;
	private GuiButton switchOnOff;
	private GuiTextField frequency;

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 222;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

		this.dismantleTurret = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 30, StatCollector.translateToLocal("gui.tcu.stgDismantle"));
		this.buttonList.add(this.dismantleTurret);
		this.toggleUniqueTarget = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 46, StatCollector.translateToLocal("gui.tcu.stgUniqueTarget"));
		this.buttonList.add(this.toggleUniqueTarget);
		this.getExperience = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 62, StatCollector.translateToLocal("gui.tcu.stgGetExp"));
		this.buttonList.add(this.getExperience);
		this.dismountFromBase = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 78, StatCollector.translateToLocal("gui.tcu.stgDismountBase"));
		this.buttonList.add(this.dismountFromBase);
		this.rideTurret = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 94, StatCollector.translateToLocal("gui.tcu.rideTurret"));
		this.buttonList.add(this.rideTurret);
		this.frequency = new GuiTextField(this.fontRendererObj, this.guiLeft + 9, this.guiTop + 119, 158, 12);
		this.frequency.setText(Integer.toString(this.turret.getFrequency()));
		this.switchOnOff = new GuiTurretButton(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 136, StatCollector.translateToLocal("gui.tcu.turretOnOff").split("\\|")[0]);
		this.buttonList.add(this.switchOnOff);

		super.initGui();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();

		this.mc.getTextureManager().bindTexture(PAGE_2);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        String s = this.turret != null ? this.turret.tInfo.getTurretName() : "";
        this.fontRendererObj.drawString("\247a"+s, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(s))/2, this.guiTop + 207, 0xFFFFFF);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.tcu.titSettings"), this.guiLeft + 6, this.guiTop + 6, 0x808080);

        if (this.turret != null) {
        	this.dismantleTurret.enabled = true;
        	this.toggleUniqueTarget.enabled = true;
        	this.switchOnOff.enabled = true;
        	this.getExperience.enabled = this.turret.getExperience() > 0;
        	this.dismountFromBase.enabled = this.turret.isRiding();
        	this.rideTurret.enabled = TurretUpgrades.hasUpgrade(TUpgControl.class, this.turret.upgrades) && this.turret.ridingEntity == null;

            this.toggleUniqueTarget.displayString = StatCollector.translateToLocalFormatted("gui.tcu.stgUniqueTarget."+this.turret.useUniqueTargets(), StatCollector.translateToLocal("gui.tcu.stgUniqueTarget"));

            this.switchOnOff.displayString = StatCollector.translateToLocal("gui.tcu.turret."+!this.turret.isActive());
        } else {
        	this.dismantleTurret.enabled = false;
        	this.toggleUniqueTarget.enabled = false;
        	this.getExperience.enabled = false;
        	this.dismountFromBase.enabled = false;
        	this.rideTurret.enabled = false;
        	this.switchOnOff.enabled = false;
        }

		super.drawScreen(par1, par2, par3);

		this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.tcu.frequency"), this.guiLeft + 9, this.guiTop + 110, 0x606060);

		this.frequency.drawTextBox();
	}

    @Override
	public void updateScreen()
    {
    	super.updateScreen();
    	this.frequency.updateCursorCounter();
    }

    @Override
	protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.frequency.mouseClicked(par1, par2, par3);
    }

    @Override
	protected void keyTyped(char par1, int par2)
    {
    	this.frequency.textboxKeyTyped(par1, par2);

    	if ((par2 == 28 || par2 == 1) && this.frequency.isFocused()) {
    		this.frequency.setFocused(false);
    		if (!this.frequency.getText().isEmpty()) {
    			this.writeFrequency();
    		}
    	}
    	else if ((par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) && !this.frequency.isFocused()) {
    		this.mc.thePlayer.closeScreen();
    		this.writeFrequency();
    	}
    }

    private void writeFrequency() {
        TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvTurretSettings(this.turret.getEntityId(), (byte)0x5, Integer.parseInt(this.frequency.getText())));
    }

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		byte b = 0x7;
		if (par1GuiButton.id == this.dismantleTurret.id) b = 0x0;
		else if (par1GuiButton.id == this.toggleUniqueTarget.id) b = 0x1;
		else if (par1GuiButton.id == this.getExperience.id) b = 0x2;
		else if (par1GuiButton.id == this.dismountFromBase.id) b = 0x3;
		else if (par1GuiButton.id == this.rideTurret.id) b = 0x4;
		else if (par1GuiButton.id == this.switchOnOff.id) b = 0x6;

        TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvTurretSettings(this.turret.getEntityId(), b));

		if (b == 0x0 || b == 0x4 || (b == 0x6 && this.turret.isActive())) {
			this.mc.thePlayer.closeScreen();
			return;
		}

		super.actionPerformed(par1GuiButton);
	}
}
