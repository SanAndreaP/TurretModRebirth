/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.model;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.block.BlockRegistry;
import dev.sanandrea.mods.turretmod.client.renderer.block.TurretAssemblyRenderer;
import dev.sanandrea.mods.turretmod.client.renderer.cartridge.AmmoCartridgeItemOverrides;
import dev.sanandrea.mods.turretmod.client.renderer.cartridge.AmmoCartridgeModel;
import dev.sanandrea.mods.turretmod.client.renderer.projectile.TurretProjectileRenderer;
import dev.sanandrea.mods.turretmod.client.renderer.block.ElectrolyteGeneratorRenderer;
import dev.sanandrea.mods.turretmod.client.renderer.turret.TurretRenderer;
import dev.sanandrea.mods.turretmod.entity.EntityRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModelRegistry
{
    private ModelRegistry() { /* no-op */ }

    public static void registerModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TURRET, TurretRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PROJECTILE, TurretProjectileRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ELECTROLYTE_GENERATOR_ENTITY, ElectrolyteGeneratorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(BlockRegistry.TURRET_ASSEMBLY_ENTITY, TurretAssemblyRenderer::new);

        RenderTypeLookup.setRenderLayer(BlockRegistry.TURRET_CRATE, RenderType.cutout());
    }

    @SubscribeEvent
    public static void onModelLoad(ModelRegistryEvent event) {
        AmmunitionRegistry.INSTANCE.getAll().forEach(a -> ModelLoader.addSpecialModel(new ModelResourceLocation(getModelRL(a.getId()), "inventory")));
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        AmmunitionRegistry.INSTANCE.getAll().forEach(a -> {
            IBakedModel boltModel = event.getModelLoader().getBakedModel(new ModelResourceLocation(getModelRL(a.getId()), "inventory"),
                                                                         ModelRotation.X0_Y0, event.getModelLoader().getSpriteMap()::getSprite);

            AmmoCartridgeItemOverrides.AMMO_MODELS.put(a, boltModel);
        });

        ModelResourceLocation itemModelRL   = AmmoCartridgeModel.MODEL_RESOURCE_LOCATION;
        IBakedModel           existingModel = event.getModelRegistry().get(itemModelRL);

        if( existingModel == null ) {
            TmrConstants.LOG.warn("Did not find the expected vanilla baked model for AmmoCartridgeModel in registry");
        } else if( existingModel instanceof AmmoCartridgeModel ) {
            TmrConstants.LOG.warn("Tried to replace AmmoCartridgeModel twice");
        } else {
            AmmoCartridgeModel customModel = new AmmoCartridgeModel(existingModel);
            event.getModelRegistry().put(itemModelRL, customModel);
        }
    }

    private static ResourceLocation getModelRL(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), AmmoCartridgeModel.MODEL_RESOURCE_LOCATION.getPath() + '_' + id.getPath());
    }
}
