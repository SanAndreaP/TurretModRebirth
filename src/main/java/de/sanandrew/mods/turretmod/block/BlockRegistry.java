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
import de.sanandrew.mods.turretmod.item.TmrItemGroups;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class BlockRegistry
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TmrConstants.ID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TmrConstants.ID);

//    public static final BlockTurretAssembly TURRET_ASSEMBLY = new BlockTurretAssembly();
    public static final ElectrolyteGeneratorBlock ELECTROLYTE_GENERATOR = new ElectrolyteGeneratorBlock();
    public static final TurretCrateBlock          TURRET_CRATE          = new TurretCrateBlock();

    public static final TileEntityType<ElectrolyteGeneratorEntity> ELECTROLYTE_GENERATOR_ENTITY = newTileType(ElectrolyteGeneratorEntity::new, ELECTROLYTE_GENERATOR);
    public static final TileEntityType<TurretCrateEntity>          TURRET_CRATE_ENTITY          = newTileType(TurretCrateEntity::new, TURRET_CRATE);

    private BlockRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        BLOCKS.register("electrolyte_generator", () -> ELECTROLYTE_GENERATOR);
        BLOCKS.register("turret_crate", () -> TURRET_CRATE);

        TILE_ENTITIES.register("electrolyte_generator", () -> ELECTROLYTE_GENERATOR_ENTITY);
        TILE_ENTITIES.register("turret_crate", () -> TURRET_CRATE_ENTITY);

        BLOCKS.register(bus);
        TILE_ENTITIES.register(bus);
    }

    private static <T extends TileEntity> TileEntityType<T> newTileType(Supplier<T> tileFactory, Block block) {
        return new TileEntityType<>(tileFactory, Collections.singleton(block), null);
    }
}
