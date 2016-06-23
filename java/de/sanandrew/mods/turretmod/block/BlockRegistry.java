/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;

public class BlockRegistry
{
    public static BlockTurretAssembly assemblyTable;
    public static BlockElectrolyteGenerator potatoGenerator;

    public static void initialize() {
        assemblyTable = new BlockTurretAssembly();
        potatoGenerator = new BlockElectrolyteGenerator();

        registerBlocks(assemblyTable, potatoGenerator);

        GameRegistry.registerTileEntity(TileEntityTurretAssembly.class, TurretModRebirth.ID + ":te_turret_assembly");
        GameRegistry.registerTileEntity(TileEntityElectrolyteGenerator.class, TurretModRebirth.ID + ":te_potato_generator");
    }

    private static void registerBlocks(Block... blocks) {
        for(Block block : blocks) {
            String unlocName = block.getUnlocalizedName();
            unlocName = unlocName.substring(unlocName.indexOf(':') + 1);
            block.setRegistryName(unlocName);
            GameRegistry.register(block);
            ItemBlock blockItm = new ItemBlock(block);
            blockItm.setRegistryName(unlocName);
            GameRegistry.register(blockItm);
        }
    }
}
