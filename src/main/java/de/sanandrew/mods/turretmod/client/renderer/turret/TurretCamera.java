/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.renderer.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.model.TransformationHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.WeakHashMap;

public class TurretCamera
{
    private static final long                                 MAX_UPDATE_TIME_NS = 1_000_000_000;
    private static final WeakHashMap<ITurretEntity, CamEntry> TURRETS            = new WeakHashMap<>();
    private static final XorShiftRandom RNG = new XorShiftRandom();

    public static void bindTurretCamTx(ITurretEntity turret, int quality) {
        CamEntry entry = TURRETS.get(turret);
        if( entry == null ) {
            entry =  new CamEntry(quality);
            TURRETS.put(turret, entry);
        } else {
            entry.active = true;
        }

        if( entry.active ) {
            RenderSystem.bindTexture(entry.textureId);
        }
    }

    public static void drawTurretCam(ITurretEntity turretInst, MatrixStack mStack, int quality, int x, int y, int width, int height) {
        mStack.pushPose();

        if( turretInst.isActive() ) {
            TurretCamera.bindTurretCamTx(turretInst, quality);
            mStack.translate(x + width, y + height, 0);
            mStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(0, 0, 180), true));

            GuiUtils.drawTexture(mStack, 0, 0, 0.0F, 0, 0, width, height, 1.0F / width, 1.0F / height);

        } else {
            Minecraft.getInstance().textureManager.bind(Resources.TEXTURE_GUI_TCU_CAM_NA);

            mStack.translate(x, y, 0);
            mStack.scale(1.0F / 256.0F * width, 4.0F / 256.0F * height, 1.0F);

            GuiUtils.drawTexture(mStack, 0, 0, 0.0F, 0, 64 * RNG.randomInt(3), 256, 64);
        }

        mStack.popPose();
    }

    @SuppressWarnings("deprecation")
    public static void render(Minecraft mc, MatrixStack mStack, final float renderTickTime) {
        Screen prevDisplayedGui = null;

        if( mc.level == null ) {
            return;
        }

        final long updTime = System.nanoTime();
        if( TURRETS.entrySet().stream().anyMatch(e -> updTime - e.getValue().lastUpdTime > MAX_UPDATE_TIME_NS) ) {
            if (!mc.isPaused()) {
                prevDisplayedGui = mc.screen;
                mc.pauseGame(true);
                mc.screen = null;
            }

            cleanupRenderers(false);

            TURRETS.forEach((turret, camEntry) -> {
                if (turret != null) {
                    if( updTime - camEntry.lastUpdTime > MAX_UPDATE_TIME_NS ) {
                        LivingEntity turretL   = turret.get();
                        GameSettings settings  = mc.options;
                        Entity      entityBkp      = mc.getCameraEntity();
                        PointOfView povBkp = settings.getCameraType();
                        boolean     hideGuiBkp     = settings.hideGui;
                        int mipmapBkp = settings.mipmapLevels;
                        double fovBkp = settings.fov;

                        mc.setCameraEntity(turretL);
                        settings.fov = 60.0F;
                        settings.setCameraType(PointOfView.FIRST_PERSON);
                        settings.hideGui = true;
                        settings.mipmapLevels = 0;

                        RenderEventHandler.renderPlayer = true;
                        RenderEventHandler.renderEntity = mc.player;

                        Framebuffer fb = mc.getMainRenderTarget();
                        int widthBkp = fb.width;
                        int heightBkp = fb.height;

                        RenderSystem.pushMatrix();
                        renderLevel(mc, renderTickTime, Util.getNanos(), mStack);
                        RenderSystem.bindTexture(camEntry.textureId);
                        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, widthBkp, heightBkp, 0);
                        RenderSystem.popMatrix();

                        RenderEventHandler.renderEntity = null;
                        RenderEventHandler.renderPlayer = false;

                        //noinspection ConstantConditions
                        mc.setCameraEntity(entityBkp);
                        settings.fov = fovBkp;
                        settings.setCameraType(povBkp);
                        settings.hideGui = hideGuiBkp;
                        settings.mipmapLevels = mipmapBkp;

                        camEntry.lastUpdTime = updTime;
                    }
                }

                camEntry.active = false;
            });

            mc.setScreen(prevDisplayedGui);
        }
    }

    private static void renderLevel(Minecraft mc, float renderTickTime, long nanos, MatrixStack mStack) {
        mc.gameRenderer.lightTexture().updateLightTexture(renderTickTime);

        ActiveRenderInfo activerenderinfo = mc.gameRenderer.getMainCamera();
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.last().pose().multiply(getProjectionMatrix(mc.gameRenderer));

        Matrix4f matrix4f = matrixstack.last().pose();
        mc.gameRenderer.resetProjectionMatrix(matrix4f);
        activerenderinfo.setup(Objects.requireNonNull(mc.level), Objects.requireNonNull(mc.getCameraEntity()), false, false, renderTickTime);

        EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, activerenderinfo, renderTickTime);
        activerenderinfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
        mStack.mulPose(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));

        mStack.mulPose(Vector3f.XP.rotationDegrees(activerenderinfo.getXRot()));
        mStack.mulPose(Vector3f.YP.rotationDegrees(activerenderinfo.getYRot() + 180.0F));
        mc.levelRenderer.renderLevel(mStack, renderTickTime, nanos, false, activerenderinfo, mc.gameRenderer, mc.gameRenderer.lightTexture(), matrix4f);

        ForgeHooksClient.dispatchRenderLast(mc.levelRenderer, mStack, renderTickTime, matrix4f, nanos);
    }

    private static Matrix4f getProjectionMatrix(GameRenderer gr) {
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.last().pose().setIdentity();
        matrixstack.last().pose().multiply(Matrix4f.perspective(90.0F, 1.0F, 0.05F, gr.getRenderDistance() * 4.0F));
        return matrixstack.last().pose();
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
        private final int     textureId;
        private       boolean active;
        private       long    lastUpdTime;

        private CamEntry(int quality) {
            this.textureId = GL11.glGenTextures();
            this.active = true;
            this.lastUpdTime = 0;

            RenderSystem.bindTexture(this.textureId);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, quality, quality, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(3 * quality * quality));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
    }
}
