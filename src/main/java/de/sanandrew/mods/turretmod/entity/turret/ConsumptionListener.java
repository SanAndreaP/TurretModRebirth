package de.sanandrew.mods.turretmod.entity.turret;

public interface ConsumptionListener
{
    boolean consume(boolean shouldConsumePrev, EntityTurret turret);
}
