package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITmrPlugin
{
    default void preInit(ITmrUtils utils) { }

    default void registerAssemblyRecipes(ITurretAssemblyRegistry registry) { }

    default void registerTurrets(ITurretRegistry registry) { }

    default void registerRepairKits(IRepairKitRegistry registry) { }

    default void registerAmmo(ITurretAmmoRegistry registry) { }

    default void registerUpgrades(IUpgradeRegistry registry) { }

    default void postInit() { }

    @SideOnly(Side.CLIENT)
    default void registerTurretInfoCategories(ITurretInfoCategoryRegistry registry) { }

    @SideOnly(Side.CLIENT)
    default void registerTurretRenderer(ITurretRenderRegistry<?> registry) { }

    @SideOnly(Side.CLIENT)
    default void registerTcuLabelElements(ILabelRegistry registry) { }
}
