package sanandreasp.mods.TurretMod3.client.registry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.packet.PacketRecvTurretShootKey;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgExperience;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgInfAmmo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class TickHandlerClientRnd {
    private FontRenderer nbFont;
	@SubscribeEvent
	public void tickEnd(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if(nbFont == null)
                nbFont = new FontRenderer(mc.gameSettings, TM3ModRegistry.DEFAULT_FONT, mc.renderEngine, false);
			if (mc.thePlayer != null
					&& mc.thePlayer.ridingEntity instanceof EntityTurret_Base
					&& mc.currentScreen == null
					&& !mc.gameSettings.showDebugInfo) {
				ScaledResolution scaledRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

				EntityTurret_Base turret = (EntityTurret_Base) mc.thePlayer.ridingEntity;
				TurretInfo tInf = TurretInfo.getTurretInfo(turret.getClass());

				mc.getTextureManager().bindTexture(TM3ModRegistry.TEX_TURRETCAM);

		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				float perc = turret.getSrvHealth() / turret.getMaxHealth();
				this.drawTexturedModalRect(5, 7, 0, 0, 183, 5, -1);
				this.drawTexturedModalRect(5, 7, 0, 5, MathHelper.ceiling_float_int(183F * perc), 5, 1);

				boolean inf = TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades);
				perc = (float)turret.getAmmo() / (float)turret.getMaxAmmo();
				perc = inf ? 1F : perc;
				this.drawTexturedModalRect(5, 19, 0, 10, 183, 5, -1);
				this.drawTexturedModalRect(5, 19, 0, 15, MathHelper.ceiling_float_int(183F * perc), 5, 1);

				perc = (float)(turret.getMaxShootTicks() - turret.getShootTicks()) / (float)turret.getMaxShootTicks();
				this.drawTexturedModalRect(5, 31, 0, 30, 183, 5, -1);
				this.drawTexturedModalRect(5, 31, 0, 35, MathHelper.ceiling_float_int(183F * perc), 5, 1);

				boolean xpRender = TurretUpgrades.hasUpgrade(TUpgExperience.class, turret.upgrades);
				if (xpRender) {
					perc = (float)turret.getExperience() / (float)turret.getExpCap();
					this.drawTexturedModalRect(5, 43, 0, 20, 183, 5, -1);
					this.drawTexturedModalRect(5, 43, 0, 25, MathHelper.ceiling_float_int(183F * perc), 5, 1);
				}

				String s = turret.getSrvHealth() + " / " + turret.getMaxHealth();
				drawStringWithFrame(nbFont, s, 190, 6, 0xDD0000, 0x000000);

				s = inf ? "INF" : turret.getAmmo() + " / " + turret.getMaxAmmo();
				drawStringWithFrame(nbFont, s, 190, 18, 0x3399FF, 0x000000);

				s = (turret.getMaxShootTicks() - turret.getShootTicks()) + " / " + turret.getMaxShootTicks();
				drawStringWithFrame(nbFont, s, 190, 30, 0xFFFFFF, 0x000000);

				if (xpRender) {
					s = turret.getExperience() + " / " + turret.getExpCap();
					drawStringWithFrame(nbFont, s, 190, 42, 0x00FF33, 0x000000);
				}

				s = tInf.getTurretName();
				drawStringWithFrame(mc.fontRenderer, s, scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(s) - 5, 5, 0xFFFFFF, 0x000000);
				s = tInf.getAmmoTypeNameFromIndex(turret.getAmmoType());
				drawStringWithFrame(mc.fontRenderer, s, scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(s) - 5, 17, 0xFFFFFF, 0x000000);

		        if (!mc.playerController.enableEverythingIsScrewedUpMode())
		        {
		        	int icon = 0;
		        	if (TM3ModRegistry.proxy.getPlayerTM3Data(mc.thePlayer).hasKey("tcCrosshair"))
		        		icon = TM3ModRegistry.proxy.getPlayerTM3Data(mc.thePlayer).getByte("tcCrosshair");
		        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		            mc.getTextureManager().bindTexture(Gui.icons);
		            GL11.glEnable(GL11.GL_BLEND);
		            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
		            this.drawTexturedModalRect(scaledRes.getScaledWidth() / 2 - 7, scaledRes.getScaledHeight() / 2 - 7, 0, 0, 16, 16, 1);
		            mc.getTextureManager().bindTexture(TM3ModRegistry.TEX_TURRETCAM);
		            this.drawTexturedModalRect(scaledRes.getScaledWidth() / 2 - 7, scaledRes.getScaledHeight() / 2 - 7, icon * 16, 40, 16, 16, 1);
		            GL11.glDisable(GL11.GL_BLEND);
		        }
			}
		}
	}

	private void drawStringWithFrame(FontRenderer par0FontRenderer, String par1String, int par2X, int par3Y, int par4FgColor, int par5BgColor) {
		par0FontRenderer.drawString(par1String, par2X-1, par3Y, par5BgColor);
		par0FontRenderer.drawString(par1String, par2X+1, par3Y, par5BgColor);
		par0FontRenderer.drawString(par1String, par2X, par3Y-1, par5BgColor);
		par0FontRenderer.drawString(par1String, par2X, par3Y+1, par5BgColor);
		par0FontRenderer.drawString(par1String, par2X, par3Y, par4FgColor);
	}

    private void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6, int zLevel)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
        var9.draw();
    }

    public static KeyBinding turretShootKey = new KeyBinding("keys.turretShoot", Keyboard.KEY_F, "key.categories.gameplay");

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {
        if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == turretShootKey.getKeyCode()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player != null && player.ridingEntity instanceof EntityTurret_Base && Minecraft.getMinecraft().currentScreen == null) {
                TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvTurretShootKey());
            }
        }
    }
}
