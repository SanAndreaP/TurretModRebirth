/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@SuppressWarnings("ConstantNamingConvention")
@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(TurretModRebirth.ID)
public class BlockRegistry
{
    public static final BlockTurretAssembly turret_assembly = nilBlock();
    public static final BlockElectrolyteGenerator electrolyte_generator = nilBlock();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(new BlockTurretAssembly().setRegistryName(TurretModRebirth.ID, "turret_assembly"),
                                        new BlockElectrolyteGenerator().setRegistryName(TurretModRebirth.ID, "electrolyte_generator"));

        GameRegistry.registerTileEntity(TileEntityTurretAssembly.class, TurretModRebirth.ID + ":te_turret_assembly");
        GameRegistry.registerTileEntity(TileEntityElectrolyteGenerator.class, TurretModRebirth.ID + ":te_potato_generator");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Block[] blocks = {
                turret_assembly, electrolyte_generator
        };
        for( Block block : blocks ) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    /** prevents IDE from thinking the block fields are null */
    private static <T> T nilBlock() {
        return null;
    }
}
