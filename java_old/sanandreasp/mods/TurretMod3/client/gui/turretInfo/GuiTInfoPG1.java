package sanandreasp.mods.TurretMod3.client.gui.turretInfo;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.ArrayList;
import java.util.List;

public class GuiTInfoPG1 extends GuiTInfoBase {
	private static final ResourceLocation PAGE_1 = new ResourceLocation(TM3ModRegistry.TEX_GUIINFO + "page_1.png");
    protected static RenderItem itemRenderer = new RenderItem();
    float renderYaw = 0.0F;

    public GuiTInfoPG1(int pg) {
		this.allowUserInput = true;
		this.site = pg;
	}

	@Override
	public void initGui() {
		super.initGui();
        tabTurretDesc.enabled = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

		this.mc.getTextureManager().bindTexture(PAGE_1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        String str = StatCollector.translateToLocal("gui.tinfo.titpg1");
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 6, 0x808080);

        str = this.turretInf.getTurretName();
        this.fontRendererObj.drawString(str, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(str))/2, this.guiTop + 21, 0x00FF00);

        this.customFR.drawSplitString(this.turretInf.getTurretDesc(), this.guiLeft + 12, this.guiTop + 125, 152, 0x000000);

        drawTurret();

        String itmNm = drawCrafting(par1, par2);

		super.drawScreen(par1, par2, par3);

        if (itmNm.length() > 0) {
			this.drawTooltip(itmNm, par1, par2);
        }
	}

	private void drawTurret() {
		EntityTurret_Base turret;
		try {
			turret = turretCls.getConstructor(World.class).newInstance(this.mc.theWorld);
		} catch (Exception e) {
			return;
		}

		turret.setInGui();

		GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)this.guiLeft + 35, (float)this.guiTop + 110F, 50.0F);
        GL11.glScalef((float)(-30), (float)30, (float)30);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        renderYaw += 0.2F;
        turret.renderYawOffset = renderYaw;
        turret.rotationYaw = renderYaw;
        turret.rotationYawHead = renderYaw;
        turret.getDataWatcher().updateObject(21, turret.getMaxHealth() / 2);
        turret.getDataWatcher().updateObject(20, (short) (turret.getMaxAmmo() / 2));
        GL11.glTranslatef(0.0F, turret.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private String drawCrafting(int mX, int mY) {
		Object crf[] = this.turretInf.getCrafting();
		ItemStack crfIS[] = new ItemStack[9];
		int ind = 0;
		for (int i = 0; i < 3; i++) {
			char[] shape = ((String) crf[i]).toCharArray();
			for (char src : shape) {
				if (src == ' ') {
					crfIS[ind++] = null;
					continue;
				}
				for (int j = 3; j < crf.length; j+=2) {
					if (crf[j].equals(src)) {
						crfIS[ind++] = (ItemStack)crf[j+1];
						break;
					}
				}
			}
		}

		RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        String itmName = "";

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (crfIS[i*3 + j] == null) continue;
				ItemStack item = crfIS[i*3 + j].copy();
				if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) item.setItemDamage(0);
				drawItemStack(item, this.guiLeft + 67 + j*18, this.guiTop + 52 + i*18);
				if (mX >= this.guiLeft + 67 + j*18 && mX < this.guiLeft + 67 + j*18 + 16 && mY >= this.guiTop + 52 + i*18 && mY < this.guiTop + 52 + i*18 + 16) {
			        RenderHelper.disableStandardItemLighting();
			        GL11.glDisable(GL11.GL_DEPTH_TEST);
					drawRect(this.guiLeft + 67 + j*18, this.guiTop + 52 + i*18, this.guiLeft + 67 + j*18 + 16, this.guiTop + 52 + i*18 + 16, 0x80FFFFFF);
			        GL11.glEnable(GL11.GL_DEPTH_TEST);
					RenderHelper.enableGUIStandardItemLighting();
					itmName = item.getDisplayName();
				}
			}
		}

		drawItemStack(this.turretInf.getTurretItem(), this.guiLeft + 67 + 79, this.guiTop + 52 + 18);

        RenderHelper.disableStandardItemLighting();

        return itmName;
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

    protected void drawTooltip(String name, int par2, int par3)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        List<String> var4 = new ArrayList<String>();

        var4.add(name);

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

            this.fontRendererObj.drawStringWithShadow(var4.get(0), var6, var7, 0xFFFFFF);

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
