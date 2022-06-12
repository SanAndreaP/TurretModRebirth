/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmoItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.repairkits.RepairKitItem;
import de.sanandrew.mods.turretmod.item.repairkits.RepairKitRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TmrConstants.ID);

    public static final Map<ResourceLocation, TurretItem> TURRET_PLACERS      = new LinkedHashMap<>();
    public static final Map<ResourceLocation, AmmoItem>   TURRET_AMMO         = new LinkedHashMap<>();
    public static final Map<ResourceLocation, ItemUpgrade>   TURRET_UPGRADES     = new LinkedHashMap<>();
    public static final Map<ResourceLocation, RepairKitItem> TURRET_REPAIRKITS   = new LinkedHashMap<>();
    public static final TurretControlUnit                    TURRET_CONTROL_UNIT = new TurretControlUnit();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_AUTO = new ItemAssemblyUpgrade.Automation();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_SPEED = new ItemAssemblyUpgrade.Speed();
//    public static final ItemAssemblyUpgrade.Filter ASSEMBLY_UPG_FILTER = new ItemAssemblyUpgrade.Filter();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_REDSTONE = new ItemAssemblyUpgrade.Redstone();
    public static final TurretLexicon     TURRET_LEXICON = new TurretLexicon();
    public static final AmmoCartridgeItem AMMO_CARTRIDGE = new AmmoCartridgeItem();

    private ItemRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        TurretRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        AmmunitionRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        UpgradeRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);
        RepairKitRegistry.INSTANCE.registerItems(ITEMS, TmrConstants.ID);

        ITEMS.register("electrolyte_generator", () -> new BlockItem(BlockRegistry.ELECTROLYTE_GENERATOR, new Item.Properties().tab(TmrItemGroups.MISC)));
        ITEMS.register("turret_crate", () -> new BlockItem(BlockRegistry.TURRET_CRATE, new Item.Properties().tab(TmrItemGroups.MISC)));

        ITEMS.register("turret_control_unit", () -> TURRET_CONTROL_UNIT);
        ITEMS.register("turret_lexicon", () -> TURRET_LEXICON);
        ITEMS.register("ammo_cartridge", () -> AMMO_CARTRIDGE);

        ITEMS.register(bus);
    }
}
