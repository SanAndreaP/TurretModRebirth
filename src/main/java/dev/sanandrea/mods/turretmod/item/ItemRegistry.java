/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.block.BlockRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.TurretRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoCartridgeItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKitItem;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKitRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("java:S2386")
public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TmrConstants.ID);

    public static final Map<ResourceLocation, TurretItem>    TURRET_PLACERS      = new LinkedHashMap<>();
    public static final Map<ResourceLocation, AmmoItem>      TURRET_AMMO         = new LinkedHashMap<>();
    public static final Map<ResourceLocation, ItemUpgrade>   TURRET_UPGRADES     = new LinkedHashMap<>();
    public static final Map<ResourceLocation, RepairKitItem> TURRET_REPAIRKITS   = new LinkedHashMap<>();
    public static final TurretControlUnit                    TURRET_CONTROL_UNIT = new TurretControlUnit();
    public static final AssemblyUpgradeItem                  ASSEMBLY_UPG_AUTO     = new AssemblyUpgradeItem.Simple();
    public static final AssemblyUpgradeItem                  ASSEMBLY_UPG_SPEED    = new AssemblyUpgradeItem.Speed();
    public static final AssemblyUpgradeItem.Filter           ASSEMBLY_UPG_FILTER   = new AssemblyUpgradeItem.Filter();
    public static final AssemblyUpgradeItem                  ASSEMBLY_UPG_REDSTONE = new AssemblyUpgradeItem.Simple();
    public static final TurretLexicon                        TURRET_LEXICON      = new TurretLexicon();
    public static final AmmoCartridgeItem                    AMMO_CARTRIDGE      = new AmmoCartridgeItem();

    private ItemRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        TurretRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        AmmunitionRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        UpgradeRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        RepairKitRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);

        ITEMS.register("electrolyte_generator", () -> new BlockItem(BlockRegistry.ELECTROLYTE_GENERATOR, new Item.Properties().tab(TmrItemGroups.MISC)));
        ITEMS.register("turret_assembly", () -> new BlockItem(BlockRegistry.TURRET_ASSEMBLY, new Item.Properties().tab(TmrItemGroups.MISC)));
        ITEMS.register("turret_crate", () -> new BlockItem(BlockRegistry.TURRET_CRATE, new Item.Properties().tab(TmrItemGroups.MISC)));

        ITEMS.register("turret_control_unit", () -> TURRET_CONTROL_UNIT);
        ITEMS.register("turret_lexicon", () -> TURRET_LEXICON);
        ITEMS.register("ammo_cartridge", () -> AMMO_CARTRIDGE);

        ITEMS.register("turret_assembly_auto_upgrade", () -> ASSEMBLY_UPG_AUTO);
        ITEMS.register("turret_assembly_speed_upgrade", () -> ASSEMBLY_UPG_SPEED);
        ITEMS.register("turret_assembly_filter_upgrade", () -> ASSEMBLY_UPG_FILTER);
        ITEMS.register("turret_assembly_redstone_upgrade", () -> ASSEMBLY_UPG_REDSTONE);

        ITEMS.register(bus);
    }
}
