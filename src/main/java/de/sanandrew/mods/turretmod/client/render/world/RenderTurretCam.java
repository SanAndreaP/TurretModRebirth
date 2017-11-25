/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class RenderTurretCam
{
    private static final long MAX_UPDATE_TIME_NS = 1_000_000_000;
    private static final WeakHashMap<ITurretInst, CamEntry> TURRETS = new WeakHashMap<>();
    private static long renderEndNanoTime;
    private static final XorShiftRandom RNG = new XorShiftRandom();

    public static void bindTurretCamTx(ITurretInst turret, int quality) {
        CamEntry entry = TURRETS.get(turret);
        if( entry == null ) {
            entry =  new CamEntry(quality);
            TURRETS.put(turret, entry);
        } else {
            entry.active = true;
        }

        if( entry.active ) {
            GlStateManager.bindTexture(entry.textureId);
        }
    }

    public static void drawTurretCam(ITurretInst turretInst, int quality, int x, int y, int width, int height) {
        if( turretInst.isActive() ) {
            RenderTurretCam.bindTurretCamTx(turretInst, quality);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.0F / 256.0F * width, 1.0F / 256.0F * height, 1.0F);
            GuiUtils.drawTexturedModalRect(-256, -256, 0.0F, 0, 0, 256, 256);
            GlStateManager.popMatrix();
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.GUI_TCU_CAM_NA.getResource());
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(1.0F / 256.0F * width, 4.0F / 256.0F * height, 1.0F);
            GuiUtils.drawTexturedModalRect(0, 0, 0.0F, 0, 64 * RNG.randomInt(3), 256, 64);
            GlStateManager.popMatrix();
        }
    }

    public static void render(Minecraft mc, final float renderTickTime) {
        GuiScreen prevDisplayedGui = null;

        if( mc.world == null ) {
            return;
        }

        if( TURRETS.size() > 0 ) {

            if (!mc.inGameHasFocus) {
                prevDisplayedGui = mc.currentScreen;
                mc.currentScreen = null;
                mc.inGameHasFocus = true;
            }

            cleanupRenderers(false);

            TURRETS.forEach((turret, camEntry) -> {
                if (turret != null) {
                    long updTime = System.nanoTime();
                    if (updTime - camEntry.lastUpdTime > MAX_UPDATE_TIME_NS) {
                        EntityLiving turretL = turret.getEntity();
                        GameSettings settings = mc.gameSettings;
                        Entity entityBkp = mc.getRenderViewEntity();
                        int thirdPersonBkp = settings.thirdPersonView;
                        boolean hideGuiBkp = settings.hideGUI;
                        int mipmapBkp = settings.mipmapLevels;
                        float fovBkp = settings.fovSetting;
                        int widthBkp = mc.displayWidth;
                        int heightBkp = mc.displayHeight;
                        float turretPrevYawBkp = turretL.prevRotationYaw;
                        float turretYawBkp = turretL.rotationYaw;
                        float turretPitchBkp = turretL.rotationPitch;
                        float turretPrevPitchBkp = turretL.prevRotationPitch;
                        double turretPosYBkp = turretL.posY;
                        double turretPrevPosYBkp = turretL.prevPosY;
                        double turretLTPosYBkp = turretL.lastTickPosY;

                        mc.setRenderViewEntity(turretL);
                        settings.fovSetting = 100.0F;
                        settings.thirdPersonView = 0;
                        settings.hideGUI = true;
                        settings.mipmapLevels = 0;
                        mc.displayWidth = camEntry.quality;
                        mc.displayHeight = camEntry.quality;

                        RenderEventHandler.renderPlayer = true;
                        RenderEventHandler.renderEntity = mc.player;

                        turretL.prevRotationYaw = turretL.prevRotationYawHead;
                        turretL.rotationYaw = turretL.rotationYawHead;
                        if( turret.isUpsideDown() ) {
                            turretL.posY -= 1.0F;
                            turretL.prevPosY -= 1.0F;
                            turretL.lastTickPosY -= 1.0F;
                            turretL.rotationPitch += 180.0F;
                            turretL.prevRotationPitch += 180.0F;
                            turretL.rotationYaw = -turretL.rotationYawHead;
                            turretL.prevRotationYaw = -turretL.prevRotationYawHead;
                        }

                        int fps = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
                        fps = Math.max(fps, 60);
                        long timeDelta = updTime - renderEndNanoTime;
                        long timeDeltaFps = Math.max((1000000000 / fps / 4) - timeDelta, 0L);

                        mc.entityRenderer.renderWorld(renderTickTime, System.nanoTime() + timeDeltaFps);
                        GlStateManager.bindTexture(camEntry.textureId);
                        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, camEntry.quality, camEntry.quality, 0);

                        renderEndNanoTime = System.nanoTime();

                        turretL.posY = turretPosYBkp;
                        turretL.prevPosY = turretPrevPosYBkp;
                        turretL.lastTickPosY = turretLTPosYBkp;
                        turretL.rotationYaw = turretYawBkp;
                        turretL.prevRotationYaw = turretPrevYawBkp;
                        turretL.rotationPitch = turretPitchBkp;
                        turretL.prevRotationPitch = turretPrevPitchBkp;

                        RenderEventHandler.renderEntity = null;
                        RenderEventHandler.renderPlayer = false;

                        //noinspection ConstantConditions
                        mc.setRenderViewEntity(entityBkp);
                        settings.fovSetting = fovBkp;
                        settings.thirdPersonView = thirdPersonBkp;
                        settings.hideGUI = hideGuiBkp;
                        settings.mipmapLevels = mipmapBkp;
                        mc.displayWidth = widthBkp;
                        mc.displayHeight = heightBkp;

                        camEntry.lastUpdTime = updTime;
                    }
                }

                camEntry.active = false;
            });

            if (prevDisplayedGui != null) {
                mc.currentScreen = prevDisplayedGui;
                mc.inGameHasFocus = false;
            }
        }
    }

    public static void cleanupRenderers(final boolean clearAll) {
        TURRETS.forEach((turret, camEntry) -> {
            if( clearAll ) {
                camEntry.active = false;
            }
            if( !camEntry.active ) {
                GL11.glDeleteTextures(camEntry.textureId);
            }
        });
        TURRETS.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null || !entry.getValue().active);
    }

    private static class CamEntry
    {
        final int quality;
        final int textureId;
        boolean active;
        long lastUpdTime;

        protected CamEntry(int quality) {
            this.textureId = GL11.glGenTextures();
            this.active = true;
            this.lastUpdTime = 0;
            this.quality = quality;
            GlStateManager.bindTexture(this.textureId);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, quality, quality, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(3 * quality * quality));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
    }
}
