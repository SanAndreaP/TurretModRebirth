/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class ItemRegistry
{
    public static final Map<ResourceLocation, ItemTurret> TURRET_PLACERS = new HashMap<>();
    public static final Map<ResourceLocation, ItemAmmo> TURRET_AMMO = new HashMap<>();
    public static final Map<ResourceLocation, ItemUpgrade> TURRET_UPGRADES = new HashMap<>();
    public static final Map<ResourceLocation, ItemRepairKit> TURRET_REPAIRKITS = new HashMap<>();
    public static final ItemTurretControlUnit TURRET_CONTROL_UNIT = new ItemTurretControlUnit();
    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_AUTO = new ItemAssemblyUpgrade.Automation();
    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_SPEED = new ItemAssemblyUpgrade.Speed();
    public static final ItemAssemblyUpgrade.Filter ASSEMBLY_UPG_FILTER = new ItemAssemblyUpgrade.Filter();
    public static final ItemTurretInfo TURRET_INFO = new ItemTurretInfo();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(TURRET_PLACERS.values().toArray(new Item[0]));
        event.getRegistry().registerAll(TURRET_AMMO.values().toArray(new Item[0]));
        event.getRegistry().registerAll(TURRET_UPGRADES.values().toArray(new Item[0]));
        event.getRegistry().registerAll(TURRET_REPAIRKITS.values().toArray(new Item[0]));
        event.getRegistry().registerAll(TURRET_CONTROL_UNIT,
                                        ASSEMBLY_UPG_AUTO,
                                        ASSEMBLY_UPG_SPEED,
                                        ASSEMBLY_UPG_FILTER,
                                        TURRET_INFO
        );
    }
}
