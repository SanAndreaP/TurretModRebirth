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
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class BlockRegistry
{
    public static final BlockTurretAssembly TURRET_ASSEMBLY = new BlockTurretAssembly();
    public static final BlockElectrolyteGenerator ELECTROLYTE_GENERATOR = new BlockElectrolyteGenerator();
    public static final BlockTurretCrate TURRET_CRATE = new BlockTurretCrate();

    public static final TileEntityType<TileEntityElectrolyteGenerator> ELECTROLYTE_GENERATOR_ENTITY =
            new TileEntityType<>(TileEntityElectrolyteGenerator::new, Collections.singleton(ELECTROLYTE_GENERATOR), null);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        TURRET_ASSEMBLY.setRegistryName(TmrConstants.ID, "turret_assembly");
        ELECTROLYTE_GENERATOR.setRegistryName(TmrConstants.ID, "electrolyte_generator");
        TURRET_CRATE.setRegistryName(TmrConstants.ID, "turret_crate");

        event.getRegistry().registerAll(TURRET_ASSEMBLY, ELECTROLYTE_GENERATOR, TURRET_CRATE);
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        ELECTROLYTE_GENERATOR_ENTITY.setRegistryName(TmrConstants.ID, "electrolyte_generator_entity");

        event.getRegistry().registerAll(ELECTROLYTE_GENERATOR_ENTITY);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(getBlockItem(TURRET_ASSEMBLY),
                                        getBlockItem(ELECTROLYTE_GENERATOR),
                                        getBlockItem(TURRET_CRATE));
    }

    private static BlockItem getBlockItem(Block block) {
        BlockItem bi = new BlockItem(block, new Item.Properties().group(TmrCreativeTabs.MISC));
        bi.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return bi;
    }
}
