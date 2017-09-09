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
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class ItemRegistry
{
    public static final ItemTurret TURRET_PLACER = new ItemTurret();
    public static final ItemAmmo TURRET_AMMO = new ItemAmmo();
    public static final ItemTurretControlUnit TURRET_CONTROL_UNIT = new ItemTurretControlUnit();
    public static final ItemRepairKit REPAIR_KIT = new ItemRepairKit();
    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_AUTO = new ItemAssemblyUpgrade.Automation();
    public static final ItemAssemblyUpgrade ASSEMBLY_UPG_SPEED = new ItemAssemblyUpgrade.Speed();
    public static final ItemAssemblyUpgrade.Filter ASSEMBLY_UPG_FILTER = new ItemAssemblyUpgrade.Filter();
    public static final ItemTurretUpgrade TURRET_UPGRADE = new ItemTurretUpgrade();
    public static final ItemTurretInfo TURRET_INFO = new ItemTurretInfo();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(TURRET_PLACER,
                                        TURRET_AMMO,
                                        TURRET_CONTROL_UNIT,
                                        REPAIR_KIT,
                                        ASSEMBLY_UPG_AUTO,
                                        ASSEMBLY_UPG_SPEED,
                                        ASSEMBLY_UPG_FILTER,
                                        TURRET_UPGRADE, TURRET_INFO
        );
    }
}
