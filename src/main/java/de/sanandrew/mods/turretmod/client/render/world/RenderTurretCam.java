/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class RenderTurretCam
{
    private static final int QUALITY = 64;
    private static final long MAX_UPDATE_TIME_NS = 1_000_000_000;
    private static final WeakHashMap<EntityTurret, CamEntry> TURRETS = new WeakHashMap<>();
    private static long renderEndNanoTime;

    public static void bindTurretCamTx(EntityTurret turret) {
        CamEntry entry = TURRETS.get(turret);
        if( entry == null ) {
            entry =  new CamEntry();
            TURRETS.put(turret, entry);
        } else {
            entry.active = true;
        }

        if( entry.active ) {
            GlStateManager.bindTexture(entry.textureId);
        }
    }

    public static void render(Minecraft mc, final float renderTickTime) {
        GuiScreen prevDisplayedGui = null;

        if( mc.world == null ) {
            return;
        }

        if( !mc.inGameHasFocus ) {
            prevDisplayedGui = mc.currentScreen;
            mc.currentScreen = null;
            mc.inGameHasFocus = true;
        }

        cleanupRenderers(false);

        TURRETS.forEach((turret, camEntry) -> {
            if( turret != null ) {
                long updTime = System.nanoTime();
                if( updTime - camEntry.lastUpdTime > MAX_UPDATE_TIME_NS ) {
                    GameSettings settings = mc.gameSettings;
                    Entity entityBkp = mc.getRenderViewEntity();
                    int thirdPersonBkp = settings.thirdPersonView;
                    boolean hideGuiBkp = settings.hideGUI;
                    int mipmapBkp = settings.mipmapLevels;
                    float fovBkp = settings.fovSetting;
                    int widthBkp = mc.displayWidth;
                    int heightBkp = mc.displayHeight;
                    float turretPrevYawBkp = turret.prevRotationYaw;
                    float turretYawBkp = turret.rotationYaw;
                    float turretPitchBkp = turret.rotationPitch;
                    float turretPrevPitchBkp = turret.prevRotationPitch;
                    double turretPosYBkp = turret.posY;
                    double turretPrevPosYBkp = turret.prevPosY;
                    double turretLTPosYBkp = turret.lastTickPosY;

                    mc.setRenderViewEntity(turret);
                    settings.fovSetting = 100.0F;
                    settings.thirdPersonView = 0;
                    settings.hideGUI = true;
                    settings.mipmapLevels = 0;
                    mc.displayWidth = QUALITY;
                    mc.displayHeight = QUALITY;

                    RenderEventHandler.renderPlayer = true;
                    RenderEventHandler.renderEntity = mc.player;

                    turret.prevRotationYaw = turret.prevRotationYawHead;
                    turret.rotationYaw = turret.rotationYawHead;
                    if( turret.isUpsideDown ) {
                        turret.posY -= 1.0F;
                        turret.prevPosY -= 1.0F;
                        turret.lastTickPosY -= 1.0F;
                        turret.rotationPitch += 180.0F;
                        turret.prevRotationPitch += 180.0F;
                        turret.rotationYaw = -turret.rotationYawHead;
                        turret.prevRotationYaw = -turret.prevRotationYawHead;
                    }

                    int fps = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
                    fps = Math.max(fps, 60);
                    long timeDelta = updTime - renderEndNanoTime;
                    long timeDeltaFps = Math.max((1000000000 / fps / 4) - timeDelta, 0L);

                    mc.entityRenderer.renderWorld(renderTickTime, System.nanoTime() + timeDeltaFps);
                    GlStateManager.bindTexture(camEntry.textureId);
                    GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, QUALITY, QUALITY, 0);

                    renderEndNanoTime = System.nanoTime();

                    turret.posY = turretPosYBkp;
                    turret.prevPosY = turretPrevPosYBkp;
                    turret.lastTickPosY = turretLTPosYBkp;
                    turret.rotationYaw = turretYawBkp;
                    turret.prevRotationYaw = turretPrevYawBkp;
                    turret.rotationPitch = turretPitchBkp;
                    turret.prevRotationPitch = turretPrevPitchBkp;

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

        if( prevDisplayedGui != null ) {
            mc.currentScreen = prevDisplayedGui;
            mc.inGameHasFocus = false;
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
        int textureId;
        boolean active;
        long lastUpdTime;

        protected CamEntry() {
            this.textureId = GL11.glGenTextures();
            this.active = true;
            this.lastUpdTime = 0;
            GlStateManager.bindTexture(this.textureId);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, QUALITY, QUALITY, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(3 * QUALITY * QUALITY));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
    }
}
