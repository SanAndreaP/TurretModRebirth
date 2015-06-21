/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.block.BlockItemTransmitter;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class TmrBlocks
{
    public static Block itemTransmitter;

    public static void initialize() {
        initializeBlocks();
        registerBlocks();
        registerTileEntities();
    }

    private static void initializeBlocks() {
        itemTransmitter = new BlockItemTransmitter();
    }

    private static void registerBlocks() {
        SAPUtils.registerBlocks(itemTransmitter);
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityItemTransmitter.class, TurretMod.MOD_ID + ":te_item_transmitter");
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockAndTeRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemTransmitter.class, new RenderItemTransmitter());
    }
}
