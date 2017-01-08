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

@SuppressWarnings("ConstantNamingConvention")
@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(TmrConstants.ID)
public class ItemRegistry
{
    public static final ItemTurret turret_placer = nilItem();
    public static final ItemAmmo turret_ammo = nilItem();
    public static final ItemTurretControlUnit turret_control_unit = nilItem();
    public static final ItemRepairKit repair_kit = nilItem();
    public static final ItemAssemblyUpgrade assembly_upg_auto = nilItem();
    public static final ItemAssemblyUpgrade assembly_upg_speed = nilItem();
    public static final ItemAssemblyUpgrade.Filter assembly_upg_filter = nilItem();
    public static final ItemTurretUpgrade turret_upgrade = nilItem();
    public static final ItemTurretInfo turret_info = nilItem();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            new ItemTurret().setRegistryName(TmrConstants.ID, "turret_placer"),
            new ItemAmmo().setRegistryName(TmrConstants.ID, "turret_ammo"),
            new ItemTurretControlUnit().setRegistryName(TmrConstants.ID, "turret_control_unit"),
            new ItemRepairKit().setRegistryName(TmrConstants.ID, "repair_kit"),
            new ItemAssemblyUpgrade.Automation().setRegistryName(TmrConstants.ID, "assembly_upg_auto"),
            new ItemAssemblyUpgrade.Speed().setRegistryName(TmrConstants.ID, "assembly_upg_speed"),
            new ItemAssemblyUpgrade.Filter().setRegistryName(TmrConstants.ID, "assembly_upg_filter"),
            new ItemTurretUpgrade().setRegistryName(TmrConstants.ID, "turret_upgrade"),
            new ItemTurretInfo().setRegistryName(TmrConstants.ID, "turret_info")
        );
    }

    /** prevents IDE from thinking the item fields are null */
    private static <T> T nilItem() {
        return null;
    }
}
