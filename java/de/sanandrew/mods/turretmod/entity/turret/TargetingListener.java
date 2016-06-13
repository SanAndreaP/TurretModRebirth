package de.sanandrew.mods.turretmod.entity.turret;

import net.minecraft.entity.Entity;

public interface TargetingListener
{
    boolean isTargetApplicable(EntityTurret turret, Entity target, boolean currValue);
    int getPriority();
}
