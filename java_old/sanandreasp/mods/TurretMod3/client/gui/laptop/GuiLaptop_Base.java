package sanandreasp.mods.TurretMod3.client.gui.laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.client.gui.GuiItemTab;
import sanandreasp.mods.turretmod3.packet.PacketRecvLaptopGUICng;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

import java.util.ArrayList;
import java.util.List;

public class GuiLaptop_Base extends GuiContainer {
    private static final ResourceLocation[] PAGES = {new ResourceLocation(TM3ModRegistry.TEX_GUILAP + "page_1.png"), new ResourceLocation(TM3ModRegistry.TEX_GUILAP + "page_2.png"), new ResourceLocation(TM3ModRegistry.TEX_GUILAP + "page_3.png")};
	protected TileEntityLaptop laptop;
	protected int site = 1;
    protected GuiButton programTurret;

    protected GuiButton tabGeneral;
    protected GuiButton tabTargets;
    protected GuiButton tabUpgrades;
    protected GuiButton tabMisc;

    private GuiButton selectedButton = null;

    protected List<GuiButton> buttonList2 = new ArrayList<GuiButton>();

	public GuiLaptop_Base(Container lapContainer, TileEntityLaptop par2TileEntityLaptop) {
		super(lapContainer);
		this.laptop = par2TileEntityLaptop;
		this.xSize = 256;
		this.ySize = 219;
		this.allowUserInput = true;
	}

	@Override
	public void initGui() {
		super.initGui();

		programTurret = new GuiItemTab(buttonList2.size(), this.guiLeft - 23, this.guiTop + this.ySize - 32, new ItemStack(TM3ModRegistry.tcu), StatCollector.translateToLocal("gui.laptop.btnprog"), false);
		buttonList2.add(programTurret);
        tabGeneral = new GuiItemTab(buttonList2.size(), this.guiLeft - 23, this.guiTop + 10, new ItemStack(Items.redstone), StatCollector.translateToLocal("gui.laptop.btngenrl"), false);
        buttonList2.add(tabGeneral);
        tabTargets = new GuiItemTab(buttonList2.size(), this.guiLeft - 23, this.guiTop + 36, new ItemStack(Items.diamond_sword), StatCollector.translateToLocal("gui.tcu.btntarg"), false);
        buttonList2.add(tabTargets);
        tabUpgrades = new GuiItemTab(buttonList2.size(), this.guiLeft - 23, this.guiTop + 62, new ItemStack(Items.saddle), StatCollector.translateToLocal("gui.tinfo.btnupgd"), false);
        buttonList2.add(tabUpgrades);
        tabMisc = new GuiItemTab(buttonList2.size(), this.guiLeft - 23, this.guiTop + 88, new ItemStack(Items.sign), StatCollector.translateToLocal("gui.tcu.misc"), false);
        buttonList2.add(tabMisc);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		RenderHelper.disableStandardItemLighting();
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 48, this.ySize - 92, 0x808080);
        RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(PAGES[site-1]);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		RenderHelper.disableStandardItemLighting();
        for (int k = 0; k < this.buttonList2.size(); ++k)
        {
            GuiButton guibutton = this.buttonList2.get(k);
            guibutton.drawButton(this.mc, par1, par2);
        }
	}

	@Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
		super.mouseClicked(par1, par2, par3);
        if (par3 == 0)
        {
            for (int l = 0; l < this.buttonList2.size(); ++l)
            {
                GuiButton guibutton = this.buttonList2.get(l);

                if (guibutton.mousePressed(this.mc, par1, par2))
                {
                    this.selectedButton = guibutton;
                    guibutton.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
            }
        }
    }

	@Override
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
		super.mouseMovedOrUp(par1, par2, par3);
        if (this.selectedButton != null && par3 == 0)
        {
            this.selectedButton.mouseReleased(par1, par2);
            this.selectedButton = null;
        }
    }

	@Override
	public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3) {
		buttonList2.clear();
		super.setWorldAndResolution(par1Minecraft, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == tabGeneral.id) {
			PacketRecvLaptopGUICng.sendServer(3, this.laptop);
		} else if (par1GuiButton.id == tabTargets.id) {
			PacketRecvLaptopGUICng.sendServer(4, this.laptop);
		} else if (par1GuiButton.id == tabUpgrades.id) {
			PacketRecvLaptopGUICng.sendServer(5, this.laptop);
		} else if (par1GuiButton.id == tabMisc.id) {
			PacketRecvLaptopGUICng.sendServer(6, this.laptop);
		}
	}

}
