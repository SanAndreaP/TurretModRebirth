package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

public interface ITmrUtils
{
    ITargetProcessor getNewTargetProcInstance(EntityTurret turret);

    IUpgradeProcessor getNewUpgradeProcInstance(EntityTurret turret);

    boolean isTCUItem(ItemStack stack);

    void onTurretDeath(EntityTurret turret);

    void updateTurretState(EntityTurret turret);

    ItemStack getPickedTurretResult(RayTraceResult target, EntityTurret turret);

    void openGui(EntityPlayer player, EnumGui id, int x, int y, int z);

    boolean canPlayerEditAll();

    boolean canOpEditAll();
}
