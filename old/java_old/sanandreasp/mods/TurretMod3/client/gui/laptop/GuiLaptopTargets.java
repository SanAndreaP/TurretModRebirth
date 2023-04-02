package sanandreasp.mods.TurretMod3.client.gui.laptop;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.packet.PacketRecvLaptopTargets;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sanandreasp.mods.turretmod3.registry.TurretTargetRegistry.trTargets;

public class GuiLaptopTargets extends GuiLaptop_Base {
	private static final ResourceLocation PAGE_1 = new ResourceLocation(TM3ModRegistry.TEX_GUITCUDIR + "page_1.png");
	protected Map<Integer, String> targetList;
	protected Map<String, Boolean> checkedTargets = Maps.newHashMap();

	private int entryPos = 0;
	private boolean isScrolling = false;
	private float currScrollPos = 0F;

	private boolean isMousePressed = false;

	public GuiLaptopTargets(Container lapContainer, TileEntityLaptop par2TileEntityLaptop) {
		super(lapContainer, par2TileEntityLaptop);
		this.site = 2;
	}

	@Override
	public void initGui() {
		super.initGui();
        this.targetList = trTargets.getTargetList();
		List<String> entities = new ArrayList<String>(EntityList.stringToClassMapping.keySet());
		List<String> stdTargets = trTargets.getTargetStrings();
		for (String entityName : entities) {
			checkedTargets.put(entityName, stdTargets.contains(entityName));
		}
		this.tabTargets.enabled = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);

		this.mc.getTextureManager().bindTexture(PAGE_1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int scrollX = 203;
        int scrollY = 18 + (int)(91F * currScrollPos);
        drawTexturedModalRect(scrollX + this.guiLeft, scrollY + this.guiTop, 176, 0, 6, 6);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.laptop.titTargets"), this.guiLeft + 6, this.guiTop + 6, 0x808080);

		for (int i = this.entryPos; i < 7 + this.entryPos; i++) {
	        int x = this.guiLeft + 48, y = this.guiTop + 21 + 13*(i-this.entryPos);
			if (i + entryPos < this.targetList.size() || this.targetList.size() > 7) {
				boolean title = false;
				String s = targetList.get(i);
				boolean checked = this.checkedTargets.containsKey(s) && this.checkedTargets.get(s);
				boolean hovering = par1 < x + 11 && par1 >= x && par2 < y + 11 && par2 >= y;
				if (s.startsWith("\n")) {
					title = true;
					s = "\247e\247o" + StatCollector.translateToLocal(s.replaceAll("\n", "")) + "\247r";
					drawRect(x, y-1, x + this.xSize - 104, y, 0xFFFFFF66);
					drawRect(x, y + 11, x + this.xSize - 104, y + 12, 0xFFFFFF66);
				}
			    if (!title) {
			        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					this.mc.getTextureManager().bindTexture(PAGE_1);
			    	if (!checked) {
			    		if (hovering)
			    			drawTexturedModalRect(x, y, 176, 14, 11, 11);
			    		else
			    			drawTexturedModalRect(x, y, 176, 47, 11, 11);
			    	} else  {
			    		if (hovering)
			    			drawTexturedModalRect(x, y, 176, 25, 11, 11);
			    		else
			    			drawTexturedModalRect(x, y, 176, 36, 11, 11);
			    	}
			    } else {
			        GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
					this.mc.getTextureManager().bindTexture(PAGE_1);
		    		if (hovering)
		    			drawTexturedModalRect(x, y, 176, 14, 11, 11);
		    		else
		    			drawTexturedModalRect(x, y, 176, 47, 11, 11);
			    }
				String name = StatCollector.translateToLocal("entity."+s+".name");
				name = name.length() > 0 && !title && !name.contains("entity.") ? name : s;
				this.fontRendererObj.drawString(name.contains(".") ? name.substring(name.lastIndexOf('.')+1) : name, x + (title ? 25 : 15), y + 2, 0xFFFFFF);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.mc.getTextureManager().bindTexture(PAGE_1);
		        drawTexturedModalRect(x, y, 148, 192, 11, 11);
			}
		}

        boolean var4 = Mouse.isButtonDown(0);

        int scrollMinX = this.guiLeft + 203;
        int scrollMaxX = scrollMinX + 6;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = scrollMinY + 97;

        if (!this.isScrolling && var4 && par1 > scrollMinX && par1 < scrollMaxX && par2 > scrollMinY && par2 < scrollMaxY) {
        	this.isScrolling = true;
        } else if (!var4) {
        	this.isScrolling = false;
        }

        for (int i = 0; i < 7 && Mouse.isButtonDown(0) && !this.isMousePressed; i++) {
        	int x = this.guiLeft + 48, y = this.guiTop + 21 + 13*i;
        	if (par1 < x + 11 && par1 >= x && par2 < y + 11 && par2 >= y) {
        		int tgID = i + this.entryPos;
        		boolean title = targetList.get(tgID).startsWith("\n");
        		if (!title) {
		    		boolean tgSel = this.checkedTargets.get(targetList.get(tgID));
		    		this.checkedTargets.put(targetList.get(tgID), !tgSel);
        		} else {
        			String nm = "";
		    		boolean tgSel = !this.getGroupMajority(tgID);
        			for (int j = tgID + 1; !nm.startsWith("\n") && j < this.targetList.size(); j++) {
    		    		nm = targetList.get(j);
    		    		this.checkedTargets.put(nm, tgSel);
    		    		nm = j+1 < this.targetList.size() ? targetList.get(j+1) : "";
        			}
        		}
				break;
        	}
        }

        this.isMousePressed = var4;

        if (this.isScrolling) {
        	float sY = (91F / (float)(this.targetList.size() - 7));
	        for (int y = 0; y < this.targetList.size() - 6; y++) {
	        	if (par2 > sY * y + this.guiTop + 18) {
	        		this.entryPos = y;
	        	}
	        }
	        this.currScrollPos = ((float)(par2 - scrollMinY) / 91F);
        }

        if (this.currScrollPos < 0.0F)
        	this.currScrollPos = 0.0F;
        if (this.currScrollPos > 1.0F)
        	this.currScrollPos = 1.0F;
	}

	private boolean getGroupMajority(int start) {
		List<Boolean> b = new ArrayList<Boolean>();
		for (int i = start+1; i < this.targetList.size(); i++) {
			String name = this.targetList.get(i);
			if (name.length() < 1 || name.startsWith("\n")) break;
			boolean b1 = this.checkedTargets.get(name);
			b.add(b1);
		}
		int trues = 0;
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i)) trues++;
		}
		return trues > b.size() / 2;
	}

	@Override
	public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getEventDWheel();

        if (var1 != 0)
        {
            if (var1 < 0)
            {
                this.entryPos = Math.min(this.entryPos + 1, this.targetList.size() - 7);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.targetList.size() - 7));
            }

            if (var1 > 0)
            {
            	this.entryPos = Math.max(this.entryPos - 1, 0);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.targetList.size() - 7));
            }
        }
    }

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		super.actionPerformed(par1GuiButton);
		if (par1GuiButton.id == this.programTurret.id) {
        	PacketRecvLaptopTargets.sendServer(this.checkedTargets, this.laptop);
        	this.inventorySlots.detectAndSendChanges();
		}
	}

//	@Override
//    protected void keyTyped(char par1, int par2)
//    {
//        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode)
//        {
//            this.mc.thePlayer.closeScreen();
//        }
//    }

}
