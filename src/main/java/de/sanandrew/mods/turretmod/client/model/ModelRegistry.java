/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.renderer.cartridge.AmmoCartridgeItemOverrides;
import de.sanandrew.mods.turretmod.client.renderer.cartridge.AmmoCartridgeModel;
import de.sanandrew.mods.turretmod.client.renderer.projectile.TurretProjectileRenderer;
import de.sanandrew.mods.turretmod.client.renderer.tileentity.ElectrolyteGeneratorRenderer;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretRenderer;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModelRegistry
{
    public static void registerModels(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TURRET, TurretRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PROJECTILE, TurretProjectileRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ELECTROLYTE_GENERATOR_ENTITY, ElectrolyteGeneratorRenderer::new);
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

        ModelResourceLocation itemModelResourceLocation = AmmoCartridgeModel.MODEL_RESOURCE_LOCATION;
        IBakedModel existingModel = event.getModelRegistry().get(itemModelResourceLocation);
        if (existingModel == null) {
            TmrConstants.LOG.warn("Did not find the expected vanilla baked model for AmmoCartridgeModel in registry");
        } else if (existingModel instanceof AmmoCartridgeModel) {
            TmrConstants.LOG.warn("Tried to replace AmmoCartridgeModel twice");
        } else {
            AmmoCartridgeModel customModel = new AmmoCartridgeModel(existingModel);
            event.getModelRegistry().put(itemModelResourceLocation, customModel);
        }
    }

    private static ResourceLocation getModelRL(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), AmmoCartridgeModel.MODEL_RESOURCE_LOCATION.getPath() + '_'  + id.getPath());
    }
}
