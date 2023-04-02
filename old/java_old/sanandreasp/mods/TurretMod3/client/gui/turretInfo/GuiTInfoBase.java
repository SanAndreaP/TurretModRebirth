package sanandreasp.mods.TurretMod3.client.gui.turretInfo;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import sanandreasp.mods.turretmod3.client.gui.GuiItemTab;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;

public class GuiTInfoBase extends GuiScreen {

    protected FontRenderer customFR;

	protected int guiLeft;
	protected int guiTop;
	protected int xSize;
	protected int ySize;

    protected GuiButton[] leftTabs;
    protected GuiButton tabTurretDesc;
    protected GuiButton tabTurretValues;
    protected GuiButton tabTurretItems;
    protected GuiButton tabTurretUpgrades;

    protected GuiButton tabNavUp;
    protected GuiButton tabNavDn;

	protected int tabInd = 0;

	protected int site;

	protected Class<? extends EntityTurret_Base> turretCls;
	protected TurretInfo turretInf;

	@Override
	public void initGui() {
		this.buttonList.clear();

		this.xSize = 176;
		this.ySize = 222;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

		turretCls = TurretInfo.getTurretClass(site);
        turretInf = TurretInfo.getTurretInfo(turretCls);

        tabTurretDesc = new GuiItemTab(buttonList.size(), this.guiLeft + this.xSize-3, this.guiTop + 10, new ItemStack(Items.book), StatCollector.translateToLocal("gui.tinfo.btndesc"), true);
        buttonList.add(tabTurretDesc);
        tabTurretValues = new GuiItemTab(buttonList.size(), this.guiLeft + this.xSize-3, this.guiTop + 36, new ItemStack(Items.sign), StatCollector.translateToLocal("gui.tinfo.btnbval"), true);
        buttonList.add(tabTurretValues);
        tabTurretItems = new GuiItemTab(buttonList.size(), this.guiLeft + this.xSize-3, this.guiTop + 62, new ItemStack(Items.arrow), StatCollector.translateToLocal("gui.tinfo.btnahit"), true);
        buttonList.add(tabTurretItems);
        tabTurretUpgrades = new GuiItemTab(buttonList.size(), this.guiLeft + this.xSize-3, this.guiTop + 88, new ItemStack(Items.enchanted_book), StatCollector.translateToLocal("gui.tinfo.btnupgd"), true);
        buttonList.add(tabTurretUpgrades);

        this.tabNavUp = new GuiTabNav(buttonList.size(), this.guiLeft-23, this.guiTop + 2, true);
        buttonList.add(this.tabNavUp);
        this.tabNavDn = new GuiTabNav(buttonList.size(), this.guiLeft-23, this.guiTop + 197, false);
        buttonList.add(this.tabNavDn);

        this.leftTabs = new GuiButton[this.turretInf.getTurretCount()];
        for (int i = 0; i < TurretInfo.getTurretCount(); i++) {
        	TurretInfo tinf = TurretInfo.getTurretInfo(TurretInfo.getTurretClass(i));
        	this.leftTabs[i] = new GuiItemTab(buttonList.size(), this.guiLeft - 23, this.guiTop + 15 + i*26, tinf.getTurretItem(), tinf.getTurretName(), false);
        	buttonList.add(this.leftTabs[i]);
        	if (!this.leftTabs[i].enabled && this.tabInd+6 < i) {
        		this.tabInd = i - 6;
        	}
        	if (!this.leftTabs[i].enabled && this.tabInd > i) {
        		this.tabInd = i;
        	}
        }

        this.customFR = new FontRenderer(this.mc.gameSettings, TM3ModRegistry.DEFAULT_FONT, this.mc.renderEngine, true);
        if (mc.gameSettings.language != null)
        {
            mc.fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
        }
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
        for (int i = 0; i < this.leftTabs.length ;i++) {
        	GuiButton btn = this.leftTabs[i];
        	if (i < this.tabInd || i > this.tabInd + 6) {
        		btn.visible = btn.enabled = false;
        	} else {
        		btn.yPosition = this.guiTop + 15 + (i-tabInd)*26;
        		btn.visible = true;
        		btn.enabled = i != this.site;
        	}
        }

        this.tabNavUp.enabled = this.tabInd > 0;
        this.tabNavDn.enabled = this.tabInd + 7 < this.leftTabs.length;

		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		int id = par1GuiButton.id;
		if (id == this.tabNavUp.id && this.tabInd > 0)
			this.tabInd--;
		else if (id == this.tabNavDn.id && this.tabInd+6 < this.leftTabs.length)
			this.tabInd++;
		else if (id == this.tabTurretDesc.id) {
			GuiTInfoBase gs = new GuiTInfoPG1(this.site);
			gs.tabInd = this.tabInd;
			this.mc.displayGuiScreen(gs);
		} else if (id == this.tabTurretValues.id) {
			GuiTInfoBase gs = new GuiTInfoPG2(this.site);
			gs.tabInd = this.tabInd;
			this.mc.displayGuiScreen(gs);
		} else if (id == this.tabTurretItems.id) {
			GuiTInfoBase gs = new GuiTInfoPG3(this.site);
			gs.tabInd = this.tabInd;
			this.mc.displayGuiScreen(gs);
		} else if (id == this.tabTurretUpgrades.id) {
			GuiTInfoBase gs = new GuiTInfoPG4(this.site);
			gs.tabInd = this.tabInd;
			this.mc.displayGuiScreen(gs);
		} else {
			for (int i = this.tabInd; i < this.tabInd + 7 && i < this.leftTabs.length; i++) {
				if (id == this.leftTabs[i].id) {
					this.site = i;
					this.initGui();
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
