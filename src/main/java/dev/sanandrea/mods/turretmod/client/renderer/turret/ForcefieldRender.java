/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.turret;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.IForcefield;
import dev.sanandrea.mods.turretmod.client.renderer.TmrRenderTypes;
import dev.sanandrea.mods.turretmod.init.TmrConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class ForcefieldRender
        implements ISelectiveResourceReloadListener
{
    public static final ForcefieldRender INSTANCE = new ForcefieldRender();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<ForcefieldCube> fadeOutFields = new ArrayList<>();
    private final Map<Integer, Queue<IForcefield>> fieldProviders = new ConcurrentHashMap<>();
    private ShieldTexture[] textures;

    private final WeakHashMap<Entity, List<ForcefieldCube>> entityFields = new WeakHashMap<>();

    public void render(Minecraft mc, MatrixStack mStack, float partTicks, ActiveRenderInfo camera) {
        boolean fabulous = Minecraft.useShaderTransparency(); // fabulous is stupid... I have to render the shields as entity render layers, or it won't show up <_<
        if( fabulous ) {
            this.entityFields.clear();
        }

        if( camera == null || mc.level == null ) {
            return;
        }


        List<ForcefieldCube> cubes = new ArrayList<>();
        Iterator<Map.Entry<Integer, Queue<IForcefield>>> it = this.fieldProviders.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<Integer, Queue<IForcefield>> entry = it.next();
            Entity entity = mc.level.getEntity(entry.getKey());

            if( entity == null || entry.getValue().isEmpty() ) {
                it.remove();
                continue;
            }

            Vector3d camPos = entity.getPosition(partTicks).subtract(camera.getPosition());
            this.handleProviders(entry.getValue().iterator(), camPos, !entity.isAlive(), cubes);

            if( fabulous ) {
                this.entityFields.put(entity, new ArrayList<>(cubes));
                cubes.clear();
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

        this.renderCubes(cubes, mc, mStack);
    }

    private void handleProviders(Iterator<IForcefield> itFF, Vector3d camPos, boolean isEntityDead, List<ForcefieldCube> cubes) {
        while( itFF.hasNext() ) {
            IForcefield ffProvider = itFF.next();

            ColorObj color = new ColorObj(ffProvider.getShieldColor());

            ForcefieldCube cube = new ForcefieldCube(camPos, ffProvider.getShieldBoundingBox(), color, ffProvider.cullShieldFaces(), ffProvider.renderFull());

            if( isEntityDead || !ffProvider.isShieldActive() ) {
                if( ffProvider.hasSmoothFadeOut() ) {
                    this.fadeOutFields.add(cube);
                }
                itFF.remove();
            } else {
                if( TmrConfig.TURRETS.renderFancyShields() ) {
                    for( ForcefieldCube intfCube : cubes ) {
                        cube.interfere(intfCube, false);
                        intfCube.interfere(cube, true);
                    }
                }

                cubes.add(cube);
            }
        }
    }

    public void renderEntityField(Entity e, Minecraft mc, MatrixStack mStack, IRenderTypeBuffer buffer) {
        List<ForcefieldCube> cubes = this.entityFields.get(e);
        if( cubes == null || cubes.isEmpty() ) {
            return;
        }

        if( this.textures == null ) {
            this.textures = getTextures(mc.getResourceManager());
        }

        for( ShieldTexture tx : this.textures ) {
            for( ForcefieldCube cube : cubes ) {
                IVertexBuilder    vBuilder = buffer.getBuffer(TmrRenderTypes.tmrShield(tx.getTexture(), cube.cullFaces, tx.moveMultiplierX, tx.moveMultiplierY));
                mStack.pushPose();
                mStack.translate(0.0D, -0.5D, 0.0D);
                cube.draw(vBuilder, mStack.last().pose());
                mStack.popPose();
            }
        }
    }

    public void renderCubes(List<ForcefieldCube> cubes, Minecraft mc, MatrixStack mStack) {
        if( this.textures == null ) {
            this.textures = getTextures(mc.getResourceManager());
        }

        for( ShieldTexture tx : this.textures ) {
            for( ForcefieldCube cube : cubes ) {
                IRenderTypeBuffer.Impl buffer     = mc.renderBuffers().bufferSource();
                IVertexBuilder    builder = buffer.getBuffer(TmrRenderTypes.tmrShield(tx.getTexture(), cube.cullFaces, tx.moveMultiplierX, tx.moveMultiplierY));

                mStack.pushPose();
                cube.translate(mStack);
                cube.draw(builder, mStack.last().pose());
                buffer.endBatch();
                mStack.popPose();
            }
        }
    }

    public static ShieldTexture[] getTextures(IResourceManager resourceManager) {
        try( IResource res = resourceManager.getResource(Resources.PROPERTY_FORCEFIELD);
             InputStream str = res.getInputStream() )
        {
            String json = IOUtils.toString(str, StandardCharsets.UTF_8);
            return GSON.fromJson(json, ShieldTexture[].class);
        } catch( IOException | JsonSyntaxException ex ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot load forcefield textures", ex);
            return new ShieldTexture[0];
        }
    }

    public void addForcefieldRenderer(Entity entity, IForcefield provider) {
        Queue<IForcefield> fields = this.fieldProviders.computeIfAbsent(entity.getId(), key -> new ConcurrentLinkedQueue<>());

        if( fields.stream().noneMatch(prov -> prov.getClass().equals(provider.getClass())) ) {
            fields.add(provider);
        }
    }

    public void removeForcefieldRenderer(Entity entity, Class<? extends IForcefield> providerCls) {
        Queue<IForcefield> fields = this.fieldProviders.computeIfAbsent(entity.getId(), key-> new ConcurrentLinkedQueue<>());

        fields.removeIf(prov -> prov.getClass().equals(providerCls));
    }

    public boolean hasForcefield(Entity entity, Class<? extends IForcefield> providerCls) {
        return this.fieldProviders.containsKey(entity.getId()) && this.fieldProviders.get(entity.getId()).stream().anyMatch(prov -> prov.getClass().equals(providerCls));
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if( resourcePredicate.test(VanillaResourceType.TEXTURES) ) {
            this.textures = null;
        }
    }

    @SuppressWarnings({"java:S1104", "java:S1121"})
    public static final class ShieldTexture
    {
        public float moveMultiplierX;
        public float moveMultiplierY;
        String texture;

        private ResourceLocation textureRL;

        public ResourceLocation getTexture() {
            return this.textureRL == null ? (this.textureRL = new ResourceLocation(this.texture)) : this.textureRL;
        }
    }
}
