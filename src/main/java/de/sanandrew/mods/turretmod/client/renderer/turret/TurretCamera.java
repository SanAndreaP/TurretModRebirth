/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.renderer.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
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
import org.lwjgl.opengl.GL30;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class TurretCamera
{
    private static final long                                 MAX_UPDATE_TIME_NS = 1_000_000_000;
    private static final WeakHashMap<ITurretEntity, CamEntry> TURRETS = new WeakHashMap<>();
    private static final XorShiftRandom                       RNG     = new XorShiftRandom();

    public static void bindTurretCamTx(ITurretEntity turret) {
        CamEntry entry = TURRETS.get(turret);
        if( entry == null ) {
            entry = new CamEntry(turret.getCameraQuality());
            TURRETS.put(turret, entry);
        } else {
            entry.active = true;
        }

        if( entry.active ) {
            RenderSystem.bindTexture(entry.textureId);
        }
    }

    public static void drawTurretCam(ITurretEntity turretInst, MatrixStack mStack, int x, int y, int width, int height) {
        mStack.pushPose();

        if( turretInst.isActive() ) {
            TurretCamera.bindTurretCamTx(turretInst);
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

    @SuppressWarnings({ "deprecation", "ConstantConditions" })
    public static void render(Minecraft mc, MatrixStack mStack, final float renderTickTime) {
        Screen prevScreen = null;

        if( mc.level == null ) {
            return;
        }

        cleanupRenderers(false);

        final long updTime = System.nanoTime();
        Map<ITurretEntity, CamEntry> disp = TURRETS.entrySet().stream()
                                                   .filter(e -> MiscUtils.applyNonNull(e.getKey(), t -> t != null && t.isActive(), false)
                                                                && updTime - e.getValue().lastUpdTime > e.getValue().maxUpdTime)
                                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if( disp.size() > 0 ) {
            if( !mc.isPaused() ) {
                prevScreen = mc.screen;
                mc.pauseGame(true);
                mc.screen = null;
            }

            GameSettings settings         = mc.options;
            boolean      prevHideGui      = settings.hideGui;
            int          prevMipmapLevels = settings.mipmapLevels;
            Entity       prevCameraEntity = mc.getCameraEntity();

            disp.forEach((turret, camEntry) -> {
                LivingEntity turretL = turret.get();

                mc.setCameraEntity(turretL);
                settings.hideGui = true;
                settings.mipmapLevels = 0;

                RenderEventHandler.renderPlayer = true;
                RenderEventHandler.renderEntity = mc.player;

                Framebuffer fb = mc.getMainRenderTarget();

                RenderSystem.pushMatrix();
                renderLevel(mc, renderTickTime, Util.getNanos(), mStack);
                int fboDraw = GlStateManager.glGenFramebuffers();
                if( fboDraw > -1 ) {
                    GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboDraw);
                    GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, camEntry.textureId, 0);
                    GL30.glBlitFramebuffer(0, 0, fb.width, fb.height, 0, 0, camEntry.quality, camEntry.quality, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);
                    GlStateManager._glDeleteFramebuffers(fboDraw);
                    fb.bindWrite(false);
                } else {
                    RenderSystem.bindTexture(camEntry.textureId);
                    GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, fb.width, fb.height, 0);
                }
                RenderSystem.popMatrix();

                camEntry.lastUpdTime = updTime;
            });

            RenderEventHandler.renderEntity = null;
            RenderEventHandler.renderPlayer = false;

            settings.hideGui = prevHideGui;
            settings.mipmapLevels = prevMipmapLevels;
            mc.setCameraEntity(prevCameraEntity);

            mc.setScreen(prevScreen);
        }

        TURRETS.forEach((turret, camEntry) -> camEntry.active = false);
    }

    private static void renderLevel(Minecraft mc, float renderTickTime, long nanos, MatrixStack mStack) {
        mc.gameRenderer.lightTexture().updateLightTexture(renderTickTime);

        ActiveRenderInfo camera = mc.gameRenderer.getMainCamera();
        Matrix4f pose = new MatrixStack().last().pose();

        pose.multiply(getProjectionMatrix(mc.gameRenderer));
        mc.gameRenderer.resetProjectionMatrix(pose);
        camera.setup(Objects.requireNonNull(mc.level), Objects.requireNonNull(mc.getCameraEntity()), false, false, renderTickTime);

        EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, camera, renderTickTime);
        camera.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
        mStack.mulPose(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));
        mStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        mStack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot() + 180.0F));

        mc.levelRenderer.renderLevel(mStack, renderTickTime, nanos, false, camera, mc.gameRenderer, mc.gameRenderer.lightTexture(), pose);
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
                RenderSystem.deleteTexture(camEntry.textureId);
            }
        });
        TURRETS.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null || !entry.getValue().active);
    }

    private static class CamEntry
    {
        private final int     textureId;
        private final int     quality;
        private final long    maxUpdTime;
        private       boolean active;
        private       long    lastUpdTime;

        private CamEntry(int quality) {
            this(quality, MAX_UPDATE_TIME_NS);
        }

        private CamEntry(int quality, long maxUpdTime) {
            this.textureId = GL11.glGenTextures();
            this.maxUpdTime = maxUpdTime;
            this.quality = quality;
            this.active = true;
            this.lastUpdTime = 0;

            RenderSystem.bindTexture(this.textureId);
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, quality, quality, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                                       BufferUtils.createIntBuffer(3 * quality * quality));
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
    }
}
