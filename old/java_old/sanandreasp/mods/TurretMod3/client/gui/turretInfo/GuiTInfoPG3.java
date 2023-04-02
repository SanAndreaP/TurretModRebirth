package sanandreasp.mods.TurretMod3.client.gui.turretInfo;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.ScaledResolution;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuiTInfoPG3 extends GuiTInfoBase {
    private static final ResourceLocation PAGE_3 = new ResourceLocation(TM3ModRegistry.TEX_GUIINFO + "page_3.png");
    protected static RenderItem itemRenderer = new RenderItem();

	protected Map<Integer, ItemStack> ammoList = Maps.newHashMap();
	protected Map<Integer, ItemStack> healList = Maps.newHashMap();

	private int entryPosA = 0;
	private boolean isScrollingA = false;
	private float currScrollPosA = 0F;
	private int entryPosH = 0;
	private boolean isScrollingH = false;
	private float currScrollPosH = 0F;

	public GuiTInfoPG3(int pg) {
		this.allowUserInput = true;
		this.site = pg;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.ammoList.clear();
		this.healList.clear();

        Iterator<ItemStack> dummyA = this.turretInf.getAmmoItems().keySet().iterator();
        Iterator<ItemStack> dummyH = this.turretInf.getHealthItems().keySet().iterator();

        for (int i = 0; dummyA.hasNext(); i++) this.ammoList.put(i, dummyA.next());
        for (int i = 0; dummyH.hasNext(); i++) this.healList.put(i, dummyH.next());

        this.tabTurretItems.enabled = false;

        this.entryPosH = this.entryPosA = 0;
        this.currScrollPosA = this.currScrollPosH = 0;
        this.isScrollingA = this.isScrollingH = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

		this.mc.getTextureManager().bindTexture(PAGE_3);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        String str = StatCollector.translateToLocal("gui.tinfo.titpg3");
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 6, 0x808080);

        str = this.turretInf.getTurretName();
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 21, 0x00FF00);

        str = StatCollector.translateToLocal("gui.tinfo.ammoitems");
        this.fontRendererObj.drawString(str, this.guiLeft + 8, this.guiTop + 39, 0x808080);
        str = StatCollector.translateToLocal("gui.tinfo.healitems");
        this.fontRendererObj.drawString(str, this.guiLeft + 8, this.guiTop + 130, 0x808080);

		this.mc.getTextureManager().bindTexture(PAGE_3);
        int scrollX = 163;
        int scrollYA = 49 + (int)(69F * currScrollPosA);
        int scrollYH = 140 + (int)(69F * currScrollPosH);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(scrollX + this.guiLeft, scrollYA + this.guiTop, 176, this.ammoList.size() > 4 ? 0 : 6, 6, 6);
        drawTexturedModalRect(scrollX + this.guiLeft, scrollYH + this.guiTop, 176, this.healList.size() > 4 ? 0 : 6, 6, 6);

		int hoverX = -1;
		int hoverY = -1;
		int hoverID = -1;
		int hoverType = -1;

        for (int i = this.entryPosA; i < 4 + entryPosA && i < this.ammoList.size(); i++) {
        	if (this.ammoList.get(i) != null) {
	        	int icnX = this.guiLeft + 8;
	        	int icnY = this.guiTop + 50 + (i-entryPosA)*19;
	        	RenderHelper.enableGUIStandardItemLighting();
	        	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        	ItemStack renderedItem = this.ammoList.get(i).copy();
	        	if (renderedItem.getItemDamage() == OreDictionary.WILDCARD_VALUE) renderedItem.setItemDamage(0);
	        	this.drawItemStack(renderedItem, icnX, icnY);
	        	RenderHelper.disableStandardItemLighting();
	        	str = this.ammoList.get(i).getDisplayName();
	        	this.fontRendererObj.drawString(str, 26 + this.guiLeft, icnY + 4, 0xFFFFFF);
	        	if (par1 >= this.guiLeft + 7 && par1 < this.guiLeft + 159 && par2 >= this.guiTop + 49 + (i-entryPosA)*19 && par2 < this.guiTop + 67 + (i-entryPosA)*19) {
	        		hoverID = i;
	        		hoverX = icnX;
	        		hoverY = icnY;
	        		hoverType = 0;
	        	}
        	}
        }

        for (int i = this.entryPosH; i < 4 + entryPosH && i < this.healList.size(); i++) {
        	if (this.healList.get(i) != null) {
	        	int icnX = this.guiLeft + 8;
	        	int icnY = this.guiTop + 141 + (i-entryPosH)*19;
	        	RenderHelper.enableGUIStandardItemLighting();
	        	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        	ItemStack renderedItem = this.healList.get(i).copy();
	        	if (renderedItem.getItemDamage() == OreDictionary.WILDCARD_VALUE) renderedItem.setItemDamage(0);
	        	this.drawItemStack(renderedItem, icnX, icnY);
	        	RenderHelper.disableStandardItemLighting();
	        	str = this.healList.get(i).getDisplayName();
	        	this.fontRendererObj.drawString(str, 26 + this.guiLeft, icnY + 4, 0xFFFFFF);
	        	if (par1 >= this.guiLeft + 7 && par1 < this.guiLeft + 159 && par2 >= this.guiTop + 140 + (i-entryPosH)*19 && par2 < this.guiTop + 158 + (i-entryPosH)*19) {
	        		hoverID = i;
	        		hoverX = icnX;
	        		hoverY = icnY;
	        		hoverType = 1;
	        	}
//	        	int hp = this.turretInf.getHealthFromItem(this.healList.get(i));
//	        	str = StatCollector.translateToLocal("gui.tinfo.healthpts");
//	        	str = hp + " " + str.substring(hp > 1 ? str.lastIndexOf('|')+1 : 0, hp > 1 ? str.length() : str.lastIndexOf('|'));
//	        	this.fontRendererObj.drawString(str, 26 + this.guiLeft, icnY + 9, 0xE0E0E0);
        	}
        }

        boolean var4 = Mouse.isButtonDown(0);

        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = scrollMinX + 6;
        int scrollMinY = this.guiTop + 49;
        int scrollMaxY = scrollMinY + 75;

        if (!this.isScrollingA && var4 && par1 > scrollMinX && par1 < scrollMaxX && par2 > scrollMinY && par2 < scrollMaxY && this.ammoList.size() > 4) {
        	this.isScrollingA = true;
        } else if (!var4) {
        	this.isScrollingA = false;
        }

        if (this.isScrollingA) {
        	int sY = (int) (69F / (float)(this.ammoList.size() - 3));
	        for (int y = 0; y < this.ammoList.size() - 3; y++) {
	        	if (par2 > sY * y + this.guiTop + 49 || par1 < sY * y + this.guiTop + 49) {
	        		this.entryPosA = y;
	        	}
	        }
	        this.currScrollPosA = ((float)(par2 - scrollMinY - 3) / 69F);
        }

        scrollMinY = this.guiTop + 140;
        scrollMaxY = scrollMinY + 75;

        if (!this.isScrollingH && var4 && par1 > scrollMinX && par1 < scrollMaxX && par2 > scrollMinY && par2 < scrollMaxY && this.healList.size() > 4) {
        	this.isScrollingH = true;
        } else if (!var4) {
        	this.isScrollingH = false;
        }

        if (this.isScrollingH) {
        	int sY = (int) (69F / (float)(this.healList.size() - 3));
        	for (int y = 0; y < this.healList.size() - 3; y++) {
        		if (par2 > sY * y + this.guiTop + 140 || par1 < sY * y + this.guiTop + 140) {
        			this.entryPosH = y;
        		}
        	}
        	this.currScrollPosH = ((float)(par2 - scrollMinY - 3) / 69F);
        }

        if (this.currScrollPosA < 0.0F)
        	this.currScrollPosA = 0.0F;
        if (this.currScrollPosA > 1.0F)
        	this.currScrollPosA = 1.0F;
        if (this.currScrollPosH < 0.0F)
        	this.currScrollPosH = 0.0F;
        if (this.currScrollPosH > 1.0F)
        	this.currScrollPosH = 1.0F;

		super.drawScreen(par1, par2, par3);

        if (hoverX >= 0 && hoverY >= 0 && hoverID >= 0 && hoverType >= 0) {
        	drawRect(hoverX - 1, hoverY - 1, hoverX + 152, hoverY + 17, 0x40FFFFFF);
        	if (hoverType == 0) {
//	        	int am = this.turretInf.getAmmoFromItem(this.ammoList.get(hoverID));
	        	String type = this.turretInf.getAmmoTypeNameFromIndex(this.turretInf.getAmmoTypeFromItem(this.ammoList.get(hoverID)));
	    		this.drawTooltip(type, this.ammoList.get(hoverID).copy(), par1, par2);
        	} else if (hoverType == 1) {
//        		int hp = this.turretInf.func_110143_aJFromItem(this.healList.get(hoverID));
        		this.drawTooltip("", this.healList.get(hoverID).copy(), par1, par2);
        	}
        }
	}

    @Override
	public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getEventDWheel();

        ScaledResolution var13 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int var14 = var13.getScaledWidth();
        int var15 = var13.getScaledHeight();
        int mX = Mouse.getX() * var14 / this.mc.displayWidth;
        int mY = var15 - Mouse.getY() * var15 / this.mc.displayHeight - 1;

        if (var1 != 0)
        {
            if (var1 < 0)
            {
            	if (mX >= this.guiLeft + 6 && mX < this.guiLeft + 170 && mY >= this.guiTop + 48 && mY < this.guiTop + 125 && this.ammoList.size() > 4) {
	                this.entryPosA = Math.min(this.entryPosA + 1, this.ammoList.size() - 4);
	    	        this.currScrollPosA = (float)this.entryPosA / ((float)(this.ammoList.size() - 4));
            	} else if (mX >= this.guiLeft + 6 && mX < this.guiLeft + 170 && mY >= this.guiTop + 139 && mY < this.guiTop + 216 && this.healList.size() > 4) {
	                this.entryPosH = Math.min(this.entryPosH + 1, this.healList.size() - 4);
	    	        this.currScrollPosH = (float)this.entryPosH / ((float)(this.healList.size() - 4));
            	}
            }

            if (var1 > 0)
            {
            	if (mX >= this.guiLeft + 6 && mX < this.guiLeft + 170 && mY >= this.guiTop + 48 && mY < this.guiTop + 125 && this.ammoList.size() > 4) {
	            	this.entryPosA = Math.max(this.entryPosA - 1, 0);
	    	        this.currScrollPosA = (float)this.entryPosA / ((float)(this.ammoList.size() - 4));
            	} else if (mX >= this.guiLeft + 6 && mX < this.guiLeft + 170 && mY >= this.guiTop + 139 && mY < this.guiTop + 216 && this.healList.size() > 4) {
	                this.entryPosH = Math.max(this.entryPosH - 1, 0);
	    	        this.currScrollPosH = (float)this.entryPosH / ((float)(this.healList.size() - 4));
            	}
            }
        }
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

    protected void drawTooltip(String aType, ItemStack is, int par2, int par3)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        List<String> var4 = new ArrayList<String>();

        is.getItem().addInformation(is, this.mc.thePlayer, var4, false);

        if (var4.size() > 0) {
        	var4.add("\2477-------");
        }

        int count;
        String str;
        if (aType.length() > 0) {
        	count = this.turretInf.getAmmoFromItem(is);
	    	str = StatCollector.translateToLocal("gui.tinfo.projectiles");
        } else {
        	count = this.turretInf.getHealthFromItem(is);
        	str = StatCollector.translateToLocal("gui.tinfo.healthpts");
        }
    	str = count + " " + str.substring(count > 1 ? str.lastIndexOf('|')+1 : 0, count > 1 ? str.length() : str.lastIndexOf('|'));

        var4.add("\247b"+str);
        if (aType.length() > 0) {
        	String s = StatCollector.translateToLocal("gui.tcu.infoType");
        	var4.add("\2473" + s + ": " + aType);
        }

        if (!var4.isEmpty())
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < var4.size(); ++var6)
            {
                var7 = this.fontRendererObj.getStringWidth(var4.get(var6));

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = par2 + 12;
            var7 = par3 - 12;
            int var9 = 8;

            if (var4.size() > 1)
            {
                var9 += (var4.size() - 1) * 10;
            }

            if (this.guiTop + var7 + var9 + 6 > this.height)
            {
                var7 = this.height - var9 - this.guiTop - 6;
            }

            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (String s : var4) {
            	this.fontRendererObj.drawStringWithShadow(s, var6, var7, 0xFFFFFF);
            	var7+=10;
            }
//            if (aType.length() > 0) this.fontRendererObj.drawStringWithShadow(var4.get(1), var6, var7+=10, 0xAAAAAA);

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
