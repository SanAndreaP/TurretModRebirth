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
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

@SuppressWarnings("ConstantNamingConvention")
@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(TmrConstants.ID)
public class BlockRegistry
{
    public static final BlockTurretAssembly turret_assembly = nilBlock();
    public static final BlockElectrolyteGenerator electrolyte_generator = nilBlock();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(new BlockTurretAssembly().setRegistryName(TmrConstants.ID, "turret_assembly"),
                                        new BlockElectrolyteGenerator().setRegistryName(TmrConstants.ID, "electrolyte_generator"));

        GameRegistry.registerTileEntity(TileEntityTurretAssembly.class, TmrConstants.ID + ":te_turret_assembly");
        GameRegistry.registerTileEntity(TileEntityElectrolyteGenerator.class, TmrConstants.ID + ":te_potato_generator");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Block[] blocks = {
                turret_assembly, electrolyte_generator
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

    /**
     * prevents IDE from thinking the block fields are null
     */
    private static <T> T nilBlock() {
        return null;
    }
}
