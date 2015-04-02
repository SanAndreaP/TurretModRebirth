package sanandreasp.mods.TurretMod3.client.gui.TCU;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

import java.lang.ref.WeakReference;
import java.util.Map;

public class GuiTCUUpgrades extends GuiTCUBase {
    private static final ResourceLocation PAGE_4 = new ResourceLocation(TM3ModRegistry.TEX_GUITCUDIR + "page_4.png");
    protected static RenderItem itemRenderer = new RenderItem();

	private Map<Integer, TurretUpgrades> upgrades = Maps.newHashMap();

	private int entryPos = 0;
	private boolean isScrolling = false;
	private float currScrollPos = 0F;

	@Override
	public void initGui() {
		super.initGui();
        this.upgrades.clear();
		for (int i = 0; i < TurretUpgrades.getUpgradeCount(); i++) {
			if (this.turret == null) break;
			WeakReference<TurretUpgrades> upg = new WeakReference<TurretUpgrades>(TurretUpgrades.getUpgradeFromID(i));
			for (Class cls : upg.get().getTurrets()) {
				if (cls.isAssignableFrom(this.turret.getClass())) {
					this.upgrades.put(this.upgrades.size(), upg.get());
					break;
				}
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

        if (this.upgrades.size() < 1) {
            this.buttonList.clear();
        	this.initGui();
        }

		this.mc.getTextureManager().bindTexture(PAGE_4);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        String s = this.turret != null ? this.turret.tInfo.getTurretName() : "";
        this.fontRendererObj.drawString("\247a"+s, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(s))/2, this.guiTop + 207, 0xFFFFFF);

        s = StatCollector.translateToLocal("gui.tcu.titUpgrades");
        this.fontRendererObj.drawString(s, this.guiLeft + 6, this.guiTop + 6, 0x808080);

        int scrollX = 163;
        int scrollY = 19 + (int)(164F * currScrollPos);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(PAGE_4);
        drawTexturedModalRect(scrollX + this.guiLeft, scrollY + this.guiTop, 176,  this.upgrades.size() > 9 ? 0 : 6, 6, 6);

        for (int i = this.entryPos; i < 9 + entryPos && i < this.upgrades.size(); i++) {
        	if (this.upgrades.get(i) != null) {
	        	int icnX = this.guiLeft + 8;
	        	int icnY = this.guiTop + 20 + (i-entryPos)*19;
	        	RenderHelper.enableGUIStandardItemLighting();
	        	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        	ItemStack renderedItem = this.upgrades.get(i).getItem().copy();
	        	if (renderedItem.getItemDamage() == OreDictionary.WILDCARD_VALUE) renderedItem.setItemDamage(0);
	        	if (this.upgrades.get(i).getEnchantment() != null) renderedItem.addEnchantment(this.upgrades.get(i).getEnchantment(), 1);
	        	this.drawItemStack(renderedItem, icnX, icnY);
	        	RenderHelper.disableStandardItemLighting();
	        	boolean taken = TurretUpgrades.hasUpgrade(this.upgrades.get(i).getClass(), this.turret.upgrades);

	        	if (taken) {
	        		drawRect(this.guiLeft + 7, this.guiTop + 19 + (i-entryPos)*19, this.guiLeft + 159, this.guiTop + 37 + (i-entryPos)*19, 0x3000FF00);
	        	}

	        	String str = this.upgrades.get(i).getName();
	        	this.fontRendererObj.drawString(str, this.guiLeft + 26, icnY + 4, taken ? 0xAAFFAA : 0xFFFFFF);
        	}
        }

        boolean var4 = Mouse.isButtonDown(0);

        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = scrollMinX + 6;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = scrollMinY + 170;

        if (!this.isScrolling && var4 && par1 > scrollMinX && par1 < scrollMaxX && par2 > scrollMinY && par2 < scrollMaxY && this.upgrades.size() > 9) {
        	this.isScrolling = true;
        } else if (!var4) {
        	this.isScrolling = false;
        }

        if (this.isScrolling) {
        	int sY = (int) (164F / (float)(this.upgrades.size() - 9));
	        for (int y = 0; y < this.upgrades.size() - 5; y++) {
	        	if (par2 > sY * y + this.guiTop + 19 || par1 < sY * y + this.guiTop + 19) {
	        		this.entryPos = y;
	        	}
	        }
	        this.currScrollPos = ((float)(par2 - scrollMinY - 9) / 164F);
        }

        if (this.currScrollPos < 0.0F)
        	this.currScrollPos = 0.0F;
        if (this.currScrollPos > 1.0F)
        	this.currScrollPos = 1.0F;

		super.drawScreen(par1, par2, par3);
	}

    private void drawItemStack(ItemStack par1ItemStack, int par2, int par3)
    {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, par1ItemStack, par2, par3);
        itemRenderer.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.renderEngine, par1ItemStack, par2, par3);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }

	public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getEventDWheel();

        if (var1 != 0 && this.upgrades.size() > 9)
        {
            if (var1 < 0)
            {
                this.entryPos = Math.min(this.entryPos + 1, this.upgrades.size() - 9);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.upgrades.size() - 9));
            }

            if (var1 > 0)
            {
            	this.entryPos = Math.max(this.entryPos - 1, 0);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.upgrades.size() - 9));
            }
        }
    }
}
