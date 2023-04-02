/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class BlockRegistry
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TmrConstants.ID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TmrConstants.ID);

    public static final ElectrolyteGeneratorBlock ELECTROLYTE_GENERATOR = new ElectrolyteGeneratorBlock();
    public static final TurretAssemblyBlock       TURRET_ASSEMBLY       = new TurretAssemblyBlock();
    public static final TurretCrateBlock          TURRET_CRATE          = new TurretCrateBlock();

    public static final TileEntityType<ElectrolyteGeneratorEntity> ELECTROLYTE_GENERATOR_ENTITY = newTileType(ElectrolyteGeneratorEntity::new, ELECTROLYTE_GENERATOR);
    public static final TileEntityType<TurretAssemblyEntity>       TURRET_ASSEMBLY_ENTITY       = newTileType(TurretAssemblyEntity::new, TURRET_ASSEMBLY);
    public static final TileEntityType<TurretCrateEntity>          TURRET_CRATE_ENTITY          = newTileType(TurretCrateEntity::new, TURRET_CRATE);

    private BlockRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        BLOCKS.register("electrolyte_generator", () -> ELECTROLYTE_GENERATOR);
        BLOCKS.register("turret_assembly", () -> TURRET_ASSEMBLY);
        BLOCKS.register("turret_crate", () -> TURRET_CRATE);

        TILE_ENTITIES.register("electrolyte_generator", () -> ELECTROLYTE_GENERATOR_ENTITY);
        TILE_ENTITIES.register("turret_assembly", () -> TURRET_ASSEMBLY_ENTITY);
        TILE_ENTITIES.register("turret_crate", () -> TURRET_CRATE_ENTITY);

        BLOCKS.register(bus);
        TILE_ENTITIES.register(bus);
    }

    @SuppressWarnings("java:S4449")
    private static <T extends TileEntity> TileEntityType<T> newTileType(Supplier<T> tileFactory, Block block) {
        return new TileEntityType<>(tileFactory, Collections.singleton(block), null);
    }
}
