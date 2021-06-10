/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.renderer.projectile.TurretProjectileRenderer;
import de.sanandrew.mods.turretmod.client.renderer.tileentity.ElectrolyteGeneratorRenderer;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretRenderer;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.TurretProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class ModelRegistry
{
    public static void registerModels(FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            ItemModelsProperties.register();
//        });

//        setStandardModel(ItemRegistry.TURRET_CONTROL_UNIT);
//        setStandardModel(ItemRegistry.TURRET_INFO);
//        setStandardModel(ItemRegistry.ASSEMBLY_UPG_FILTER);
//        setStandardModel(ItemRegistry.ASSEMBLY_UPG_AUTO);
//        setStandardModel(ItemRegistry.ASSEMBLY_UPG_SPEED);
//        setStandardModel(ItemRegistry.ASSEMBLY_UPG_REDSTONE);
//        setStandardModel(BlockRegistry.ELECTROLYTE_GENERATOR);
//        setStandardModel(BlockRegistry.TURRET_ASSEMBLY);
//        setStandardModel(BlockRegistry.TURRET_CRATE);

//        ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> {
//            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
//            setStandardModel(item, new ResourceLocation(regName.getNamespace(), "turrets/" + regName.getPath()));
//        });
//        ItemRegistry.TURRET_AMMO.forEach((rl, item) -> {
//            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
//            setStandardModel(item, new ResourceLocation(regName.getNamespace(), "ammo/" + regName.getPath()));
//        });
//        ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> {
//            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
//            setStandardModel(item, new ResourceLocation(regName.getNamespace(), "upgrades/" + regName.getPath()));
//        });
//        ItemRegistry.TURRET_REPAIRKITS.forEach((rl, item) -> {
//            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
//            setStandardModel(item, new ResourceLocation(regName.getNamespace(), "repair_kits/" + regName.getPath()));
//        });

//        setCustomMeshModel(ItemRegistry.AMMO_CARTRIDGE, new MeshDefAmmoCartridge());
//
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectrolyteGenerator.class, new RenderElectrolyteGenerator());

        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.TURRET, TurretRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PROJECTILE, TurretProjectileRenderer::new);

        ClientRegistry.bindTileEntityRenderer(BlockRegistry.ELECTROLYTE_GENERATOR_ENTITY, ElectrolyteGeneratorRenderer::new);
    }

//    private static void setStandardModel(Item item) {
//        ResourceLocation regName = item.getRegistryName();
//        if( regName != null ) {
//            Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(item, new ModelResourceLocation(regName, "inventory"));
////            ModelLoaderRegistry.registerLoader(item, 0, new ModelResourceLocation(regName, "inventory"));
//        }
//    }
//
//    private static void setStandardModel(Item item, ResourceLocation modelLocation) {
////        ModelLoader.instance().
////        Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(item, new ModelResourceLocation(modelLocation, "inventory"));
//    }

//    private static void setStandardModel(Block item) {
//        Item itm = Item.getItemFromBlock(item);
//        if( itm != Items.AIR ) {
//            setStandardModel(itm);
//        }
//    }

//    private static void setCustomMeshModel(@SuppressWarnings("SameParameterValue") Item item, IListedItemMeshDefinition mesher) {
//        ModelLoader.setCustomMeshDefinition(item, mesher);
//        ModelBakery.registerItemVariants(item, mesher.getDefinedResources());
//    }
}
