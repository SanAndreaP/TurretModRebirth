package sanandreasp.mods.TurretMod3.client.gui.TCU;

import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.packet.PacketRecvTargetListSrv;
import sanandreasp.mods.turretmod3.registry.TurretTargetRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiTCUTargets extends GuiTCUBase {
	protected Map<Integer, String> targetList;

	private int entryPos = 0;
	private boolean isScrolling = false;
	private float currScrollPos = 0F;

	private boolean isMousePressed = false;

	@Override
	public void initGui() {
		super.initGui();
        this.targetList = TurretTargetRegistry.getTargetList();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();

		this.mc.getTextureManager().bindTexture(PAGE_1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        int scrollX = 163;
        int scrollY = 19 + (int)(178F * currScrollPos);
        drawTexturedModalRect(scrollX + this.guiLeft, scrollY + this.guiTop, 176, 0, 6, 6);

        String tn = this.turret != null ? this.turret.tInfo.getTurretName() : "";
        this.fontRendererObj.drawString("\247a"+tn, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(tn))/2, this.guiTop + 207, 0xFFFFFF);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.tcu.titTargets"), this.guiLeft + 6, this.guiTop + 6, 0x808080);

		for (int i = this.entryPos; i < 14 + this.entryPos; i++) {
	        int x = this.guiLeft + 8, y = this.guiTop + 21 + 13*(i-this.entryPos);
			if (i + entryPos < this.targetList.size() || this.targetList.size() > 14) {
				boolean title = false;
				String s = targetList.get(i);
				boolean checked = this.turret != null && this.turret.targets != null && this.turret.targets.containsKey(s) &&this.turret.targets.get(s);
				boolean hovering = par1 < x + 11 && par1 >= x && par2 < y + 11 && par2 >= y;
				if (s.startsWith("\n")) {
					title = true;
					s = "\247e\247o" + StatCollector.translateToLocal(s.replaceAll("\n", "")) + "\247r";
					drawRect(x, y - 1, x + this.xSize - 27, y, 0xFFFFFF66);
					drawRect(x, y + 11, x + this.xSize - 27, y + 12, 0xFFFFFF66);
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

		if (this.turret == null || (this.turret != null && this.turret.targets == null))
			return;

        boolean var4 = Mouse.isButtonDown(0);

        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = scrollMinX + 6;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = scrollMinY + 184;

        if (!this.isScrolling && var4 && par1 > scrollMinX && par1 < scrollMaxX && par2 > scrollMinY && par2 < scrollMaxY) {
        	this.isScrolling = true;
        } else if (!var4) {
        	this.isScrolling = false;
        }

        for (int i = 0; i < 14 && Mouse.isButtonDown(0) && !this.isMousePressed; i++) {
        	int x = this.guiLeft + 8, y = this.guiTop + 21 + 13*i;
        	if (par1 < x + 11 && par1 >= x && par2 < y + 11 && par2 >= y) {
        		int tgID = i + this.entryPos;
        		if (targetList.get(tgID) != null) {
	        		boolean title = targetList.get(tgID).startsWith("\n");
	        		if (!title) {
			    		Boolean tgSel = this.turret.targets.get(targetList.get(tgID));
			    		if (tgSel != null)
			    			this.turret.targets.put(targetList.get(tgID), !tgSel);
			    		else
			    			this.turret.targets.put(targetList.get(tgID), true);
	        		} else {
	        			String nm = "";
			    		boolean tgSel = !this.getGroupMajority(tgID);
	        			for (int j = tgID + 1; !nm.startsWith("\n") && j < this.targetList.size(); j++) {
	    		    		nm = targetList.get(j);
	    		    		this.turret.targets.put(nm, tgSel);
	    		    		nm = j+1 < this.targetList.size() ? targetList.get(j+1) : "";
	        			}
	        		}
        		}
				break;
        	}
        }

        this.isMousePressed = var4;

        if (this.isScrolling) {
        	float sY = (178F / (float)(this.targetList.size() - 14));
	        for (int y = 0; y < this.targetList.size() - 13; y++) {
	        	if (par2 > sY * y + this.guiTop + 18) {
	        		this.entryPos = y;
	        	}
	        }
	        this.currScrollPos = ((float)(par2 - scrollMinY - 2) / 178F);
        }

        if (this.currScrollPos < 0.0F)
        	this.currScrollPos = 0.0F;
        if (this.currScrollPos > 1.0F)
        	this.currScrollPos = 1.0F;

		super.drawScreen(par1, par2, par3);
	}

	private boolean getGroupMajority(int start) {
		List<Boolean> b = new ArrayList<Boolean>();
		for (int i = start+1; i < this.targetList.size(); i++) {
			String name = this.targetList.get(i);
			if (name.length() < 1 || name.startsWith("\n")) break;
			Boolean b1 = this.turret.targets.get(name);
			if (b1 != null)
				b.add(b1);
			else
				b.add(false);
		}
		int trues = 0;
        for (Boolean aB : b) {
            if (aB) trues++;
        }
		return trues > b.size() / 2;
	}

	public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getEventDWheel();

        if (var1 != 0)
        {
            if (var1 < 0)
            {
                this.entryPos = Math.min(this.entryPos + 1, this.targetList.size() - 14);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.targetList.size() - 14));
            }

            if (var1 > 0)
            {
            	this.entryPos = Math.max(this.entryPos - 1, 0);
    	        this.currScrollPos = (float)this.entryPos / ((float)(this.targetList.size() - 14));
            }
        }
    }

    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
        	PacketRecvTargetListSrv.sendServer(turret);
            this.mc.thePlayer.closeScreen();
        }
    }
}
