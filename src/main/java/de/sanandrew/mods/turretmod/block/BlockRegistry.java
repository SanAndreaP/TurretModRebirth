/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class BlockRegistry
{
    public static final BlockTurretAssembly TURRET_ASSEMBLY = new BlockTurretAssembly();
    public static final BlockElectrolyteGenerator ELECTROLYTE_GENERATOR = new BlockElectrolyteGenerator();
    public static final BlockTurretCrate TURRET_CRATE = new BlockTurretCrate();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(TURRET_ASSEMBLY, ELECTROLYTE_GENERATOR, TURRET_CRATE);

        GameRegistry.registerTileEntity(TileEntityTurretAssembly.class, new ResourceLocation(TmrConstants.ID, "te_turret_assembly"));
        GameRegistry.registerTileEntity(TileEntityElectrolyteGenerator.class, new ResourceLocation(TmrConstants.ID, "te_potato_generator"));
        GameRegistry.registerTileEntity(TileEntityTurretCrate.class, new ResourceLocation(TmrConstants.ID, "te_turret_crate"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Block[] blocks = {
                TURRET_ASSEMBLY, ELECTROLYTE_GENERATOR, TURRET_CRATE
        };
        for( Block block : blocks ) {
            ResourceLocation regName = block.getRegistryName();
            if( regName != null ) {
                event.getRegistry().register(new ItemBlock(block).setRegistryName(regName));
            } else {
                TmrConstants.LOG.log(Level.ERROR, String.format("Cannot register Item for Block %s as it does not have a registry name!", block));
            }
        }
    }
}
