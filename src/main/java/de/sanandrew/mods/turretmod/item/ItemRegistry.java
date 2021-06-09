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
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmoItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry
{
    public static final Map<ResourceLocation, TurretItem> TURRET_PLACERS      = new LinkedHashMap<>();
    public static final Map<ResourceLocation, AmmoItem>   TURRET_AMMO         = new LinkedHashMap<>();
//    public static final Map<ResourceLocation, ItemUpgrade> TURRET_UPGRADES = new LinkedHashMap<>();
//    public static final Map<ResourceLocation, ItemRepairKit> TURRET_REPAIRKITS = new LinkedHashMap<>();
    public static final TurretControlUnit                 TURRET_CONTROL_UNIT = new TurretControlUnit();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_AUTO = new ItemAssemblyUpgrade.Automation();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_SPEED = new ItemAssemblyUpgrade.Speed();
//    public static final ItemAssemblyUpgrade.Filter ASSEMBLY_UPG_FILTER = new ItemAssemblyUpgrade.Filter();
//    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_REDSTONE = new ItemAssemblyUpgrade.Redstone();
    public static final TurretLexicon     TURRET_LEXICON      = new TurretLexicon();
//    public static final ItemAmmoCartridge AMMO_CARTRIDGE = new ItemAmmoCartridge();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        TurretRegistry.INSTANCE.registerItems(event, TmrConstants.ID);
        AmmunitionRegistry.INSTANCE.registerItems(event, TmrConstants.ID);
//        event.getRegistry().registerAll(TURRET_UPGRADES.values().toArray(new Item[0]));
//        event.getRegistry().registerAll(TURRET_REPAIRKITS.values().toArray(new Item[0]));
        event.getRegistry().registerAll(TURRET_CONTROL_UNIT.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret_control_unit")),
//                                        ASSEMBLY_UPG_AUTO,
//                                        ASSEMBLY_UPG_SPEED,
//                                        ASSEMBLY_UPG_FILTER,
//                                        ASSEMBLY_UPG_REDSTONE,
                                        TURRET_LEXICON.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret_lexicon"))//,
//                                        AMMO_CARTRIDGE
        );
    }
}
