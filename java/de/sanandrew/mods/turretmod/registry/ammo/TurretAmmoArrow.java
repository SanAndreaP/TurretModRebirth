/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.EntityTurret;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;

import java.util.UUID;

public abstract class TurretAmmoArrow
        implements TurretAmmo
{
    private final String name;
    private final UUID uuid;
    private final int capacity;

    public TurretAmmoArrow(boolean quiver) {
        this.name = quiver ? "turret_ammo.arrow_lrg" : "turret_ammo.arrow_sng";
        this.uuid = UUID.fromString(quiver ? "e6d51120-b52a-42ea-bf78-bebbc7d41c09" : "7b497e61-4e8d-4e49-ac71-414751e399e8");
        this.capacity = quiver ? 16 : 1;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getItemDesc() {
        return null;
    }

    @Override
    public String getInfoDesc() {
        return null;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Override
    public Class<? extends IProjectile> getEntity() {
        return null;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return null;
    }

    @Override
    public float getInfoDamage() {
        return 3.0F;
    }

    public static class Single
            extends TurretAmmoArrow
    {
        public Single() {
            super(false);
        }
    }

    public static class Quiver
            extends TurretAmmoArrow
    {
        public Quiver() {
            super(true);
        }
    }
}
