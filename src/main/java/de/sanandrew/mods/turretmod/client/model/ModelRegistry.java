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
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TmrConstants.ID)
public final class ModelRegistry
{
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        setStandardModel(ItemRegistry.TURRET_CONTROL_UNIT);
        setStandardModel(ItemRegistry.TURRET_INFO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_FILTER);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_AUTO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_SPEED);
        setStandardModel(BlockRegistry.ELECTROLYTE_GENERATOR);
        setStandardModel(BlockRegistry.TURRET_ASSEMBLY);

        ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "turrets/" + regName.getResourcePath()));
        });
        ItemRegistry.TURRET_AMMO.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "ammo/" + regName.getResourcePath()));
        });
        ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "upgrades/" + regName.getResourcePath()));
        });
        ItemRegistry.TURRET_REPAIRKITS.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "repairkits/" + regName.getResourcePath()));
        });

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectrolyteGenerator.class, new RenderElectrolyteGenerator());
    }

    private static void setStandardModel(Item item) {
        ResourceLocation regName = item.getRegistryName();
        if( regName != null ) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(regName, "inventory"));
        }
    }

    private static void setStandardModel(Item item, ResourceLocation modelLocation) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modelLocation, "inventory"));
    }

    private static void setStandardModel(Block item) {
        Item itm = Item.getItemFromBlock(item);
        if( itm != Items.AIR ) {
            setStandardModel(itm);
        }
    }
}
