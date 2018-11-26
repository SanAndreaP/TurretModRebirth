/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class RenderForcefieldHandler
        implements ISelectiveResourceReloadListener
{
    public static final RenderForcefieldHandler INSTANCE = new RenderForcefieldHandler();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<ForcefieldCube> fadeOutFields = new ArrayList<>();
    private final Map<Integer, Queue<IForcefieldProvider>> fieldProviders = new ConcurrentHashMap<>();
    private ShieldTexture[] textures;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.getRenderViewEntity();
        if( renderEntity == null ) {
            return;
        }

        final float partialTicks = event.getPartialTicks();
        double renderX = getPartialPos(renderEntity.lastTickPosX, renderEntity.posX, partialTicks);
        double renderY = getPartialPos(renderEntity.lastTickPosY, renderEntity.posY, partialTicks);
        double renderZ = getPartialPos(renderEntity.lastTickPosZ, renderEntity.posZ, partialTicks);

        List<ForcefieldCube> cubes = new ArrayList<>();

        int worldTicks = (int) (mc.world.getTotalWorldTime() % Integer.MAX_VALUE);

        Iterator<Map.Entry<Integer, Queue<IForcefieldProvider>>> it = this.fieldProviders.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<Integer, Queue<IForcefieldProvider>> entry = it.next();
            Entity entity = mc.world.getEntityByID(entry.getKey());

            if( entity == null || entry.getValue().size() < 1 ) {
                it.remove();
                continue;
            }

            Iterator<IForcefieldProvider> itFF = entry.getValue().iterator();
            while( itFF.hasNext() ) {
                IForcefieldProvider ffProvider = itFF.next();

                ColorObj color = new ColorObj(ffProvider.getShieldColor());

                double entityX = getPartialPos(entity.lastTickPosX, entity.posX, partialTicks);
                double entityY = getPartialPos(entity.lastTickPosY, entity.posY, partialTicks);
                double entityZ = getPartialPos(entity.lastTickPosZ, entity.posZ, partialTicks);

                ForcefieldCube cube = new ForcefieldCube(new Vec3d(entityX - renderX, entityY - renderY, entityZ - renderZ), ffProvider.getShieldBoundingBox(), color);
                cube.fullRendered = ffProvider.renderFull();

                if( entity.isDead || !entity.isEntityAlive() || !ffProvider.isShieldActive() || !mc.world.loadedEntityList.contains(entity) ) {
                    if( ffProvider.hasSmoothFadeOut() ) {
                        this.fadeOutFields.add(cube);
                    }
                    itFF.remove();
                } else {
                    if( TmrConfig.Client.calcForcefieldIntf ) {
                        for( ForcefieldCube intfCube : cubes ) {
                            cube.interfere(intfCube, false);
                            intfCube.interfere(cube, true);
                        }
                    }

                    cubes.add(cube);
                }
            }
        }

        Iterator<ForcefieldCube> fadeOutIt = this.fadeOutFields.iterator();
        while( fadeOutIt.hasNext() ) {
            ForcefieldCube shield = fadeOutIt.next();
            if( shield.boxColor.alpha() <= 0 ) {
                fadeOutIt.remove();
            } else {
                cubes.add(shield);

                shield.boxColor.setAlpha(shield.boxColor.alpha() - 3);
            }
        }

        if( this.textures == null ) {
            try( IResource res = mc.getResourceManager().getResource(Resources.TURRET_FORCEFIELD_PROPERTIES.resource);
                 InputStream str = res.getInputStream() )
            {
                String json = IOUtils.toString(str, Charset.forName("UTF-8"));
                this.textures = GSON.fromJson(json, ShieldTexture[].class);
            } catch( IOException | JsonSyntaxException ex ) {
                this.textures = new ShieldTexture[0];
                TmrConstants.LOG.log(Level.ERROR, "Cannot load forcefield textures", ex);
            }
        }

        Tessellator tess = Tessellator.getInstance();
        for( ShieldTexture tx : this.textures ) {
            float transformTexAmount = worldTicks % 400 + event.getPartialTicks();
            float texTranslateX = transformTexAmount * tx.moveMultiplierX;
            float texTranslateY = transformTexAmount * tx.moveMultiplierY;

            mc.renderEngine.bindTexture(tx.getTexture());

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.translate(texTranslateX, texTranslateY, 0.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            for( ForcefieldCube cube : cubes ) {
                tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                GlStateManager.depthMask(false);
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.disableCull();
                cube.draw(tess);
                tess.draw();
                GlStateManager.enableCull();
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.depthMask(true);
            }

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    private static double getPartialPos(double prev, double curr, double partialTicks) {
        return prev + (curr - prev) * partialTicks;
    }

    @SuppressWarnings("unused")
    public void addForcefieldRenderer(Entity entity, IForcefieldProvider provider) {
        Queue<IForcefieldProvider> fields = this.fieldProviders.computeIfAbsent(entity.getEntityId(), key -> new ConcurrentLinkedQueue<>());

        if( fields.stream().noneMatch(prov -> prov.getClass().equals(provider.getClass())) ) {
            fields.add(provider);
        }
    }

    public boolean hasForcefield(Entity entity, Class<? extends IForcefieldProvider> providerCls) {
        return this.fieldProviders.containsKey(entity.getEntityId()) && this.fieldProviders.get(entity.getEntityId()).stream().anyMatch(prov -> prov.getClass().equals(providerCls));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if( resourcePredicate.test(VanillaResourceType.TEXTURES) ) {
            this.textures = null;
        }
    }

    private final class ShieldTexture
    {
        String texture;
        float moveMultiplierX;
        float moveMultiplierY;
        private ResourceLocation textureRL;

        ResourceLocation getTexture() {
            return this.textureRL == null ? (this.textureRL = new ResourceLocation(this.texture)) : this.textureRL;
        }
    }
}
