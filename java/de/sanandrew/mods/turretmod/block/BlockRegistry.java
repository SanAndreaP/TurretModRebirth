/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import cpw.mods.fml.common.registry.GameRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;

public class BlockRegistry
{
    public static BlockTurretAssembly turretAssembly;

    public static void initialize() {
        turretAssembly = new BlockTurretAssembly();

        registerBlocks(turretAssembly);

        GameRegistry.registerTileEntity(TileEntityTurretAssembly.class, TurretModRebirth.ID + ":te_turret_assembly");
    }

    private static void registerBlocks(Block... blocks) {
        for(Block block : blocks) {
            String unlocName = block.getUnlocalizedName();
            unlocName = unlocName.substring(unlocName.indexOf(':') + 1);
            GameRegistry.registerBlock(block, unlocName);
        }
    }
}
