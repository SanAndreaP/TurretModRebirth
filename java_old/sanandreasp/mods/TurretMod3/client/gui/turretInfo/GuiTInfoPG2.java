package sanandreasp.mods.TurretMod3.client.gui.turretInfo;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class GuiTInfoPG2 extends GuiTInfoBase {
    private final static ResourceLocation PAGE_2 = new ResourceLocation(TM3ModRegistry.TEX_GUIINFO + "page_2.png");
	public GuiTInfoPG2(int pg) {
		this.allowUserInput = true;
		this.site = pg;
	}

	@Override
	public void initGui() {
		super.initGui();
        tabTurretValues.enabled = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

		this.mc.getTextureManager().bindTexture(PAGE_2);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        String str = StatCollector.translateToLocal("gui.tinfo.titpg2");
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 6, 0x808080);

        str = this.turretInf.getTurretName();
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 21, 0x00FF00);

        int icoX = this.guiLeft + 7;
        int icoY = this.guiTop + 131;

        String title = "", value = "", desc = "";

        if (par1 >= icoX && par1 < icoX + 16 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX, icoY, icoX + 16, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameMaxAmmo");
        	value = ((Integer)this.turretInf.getMaxAmmo()).toString();
        	desc = StatCollector.translateToLocal("gui.tinfo.descMaxAmmo");
        } else if (par1 >= icoX + 18 && par1 < icoX + 34 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 18, icoY, icoX + 34, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameMaxHealth");
        	value = this.turretInf.getMaxHealth() + " HP";
        	desc = StatCollector.translateToLocal("gui.tinfo.descMaxHealth");
        } else if (par1 >= icoX + 36 && par1 < icoX + 52 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 36, icoY, icoX + 52, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameUpperRangeY");
        	value =  StatCollector.translateToLocalFormatted("gui.tinfo.blocks", Math.floor(this.turretInf.getYRangeHigh()));
        	desc = StatCollector.translateToLocal("gui.tinfo.descUpperRangeY");
        } else if (par1 >= icoX + 54 && par1 < icoX + 70 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 54, icoY, icoX + 70, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameLowerRangeY");
        	value = StatCollector.translateToLocalFormatted("gui.tinfo.blocks", Math.floor(this.turretInf.getYRangeLow()));
        	desc = StatCollector.translateToLocal("gui.tinfo.descLowerRangeY");
        } else if (par1 >= icoX + 72 && par1 < icoX + 88 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 72, icoY, icoX + 88, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameRangeX");
        	value = StatCollector.translateToLocalFormatted("gui.tinfo.blocks", Math.floor(this.turretInf.getXRange()));
        	desc = StatCollector.translateToLocal("gui.tinfo.descRangeX");
        } else if (par1 >= icoX + 90 && par1 < icoX + 106 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 90, icoY, icoX + 106, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameDamage");
        	value = this.turretInf.getDamage() + " HP";
        	desc = StatCollector.translateToLocal("gui.tinfo.descDamage");
        } else if (par1 >= icoX + 108 && par1 < icoX + 124 && par2 >= icoY && par2 < icoY + 16) {
        	drawRect(icoX + 108, icoY, icoX + 124, icoY + 16, 0x80FFFFFF);
        	title = StatCollector.translateToLocal("gui.tinfo.nameExp");
        	value = this.turretInf.getMaxXP() + " XP";
        	desc = StatCollector.translateToLocal("gui.tinfo.descExp");
        } else {
        	this.customFR.drawSplitString(StatCollector.translateToLocal("gui.tinfo.hoverPG2"), this.guiLeft + 11, this.guiTop + 153, 157, 0x808080);
        }

    	this.fontRendererObj.drawString(title, this.guiLeft + 10, this.guiTop + 153, 0x006000);
    	this.fontRendererObj.drawString(value, this.guiLeft + 15, this.guiTop + 162, 0x000000);
    	this.customFR.drawSplitString(desc, this.guiLeft + 11, this.guiTop + 171, 157, 0x606060);

		this.mc.getTextureManager().bindTexture(PAGE_2);
        GL11.glColor4f(1.0F, 0.3F, 0.3F, 1.0F);
        drawTexturedModalRect(this.guiLeft + 136, this.guiTop + 51, 176, 32, 16, 16);
        drawTexturedModalRect(this.guiLeft + 43, this.guiTop + 131, 176, 32, 16, 16);
        GL11.glColor4f(0.3F, 1.0F, 0.3F, 1.0F);
        drawTexturedModalRect(this.guiLeft + 136, this.guiTop + 86, 176, 32, 16, 16);
        drawTexturedModalRect(this.guiLeft + 61, this.guiTop + 131, 176, 32, 16, 16);
        GL11.glColor4f(1.0F, 1.0F, 0.3F, 1.0F);
        drawTexturedModalRect(this.guiLeft + 72, this.guiTop + 105, 176, 32, 16, 16);
        drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 131, 176, 32, 16, 16);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 131, 176, 0, 16, 16);
        drawTexturedModalRect(this.guiLeft + 25, this.guiTop + 131, 176, 16, 16, 16);
        drawTexturedModalRect(this.guiLeft + 97, this.guiTop + 131, 176, 48, 16, 16);
        drawTexturedModalRect(this.guiLeft + 115, this.guiTop + 131, 176, 64, 16, 16);

		super.drawScreen(par1, par2, par3);
	}
}
