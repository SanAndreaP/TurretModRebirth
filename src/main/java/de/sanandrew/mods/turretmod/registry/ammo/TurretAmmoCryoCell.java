/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoCryoCell
        implements IAmmunition
{private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    TurretAmmoCryoCell(String name, UUID id, int capacity) {
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Nonnull
    @Override
    public IAmmunitionGroup getGroup() {
        return Ammunitions.Groups.CRYO_CELL;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class SingleMK1
            extends TurretAmmoCryoCell
    {
        SingleMK1() {
            super("cryocell_1", Ammunitions.CRYOCELL_MK1, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK1;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_I;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_I.getDamage();
        }
    }

    public static class MultiMK1
            extends TurretAmmoCryoCell
    {
        MultiMK1() {
            super("cryocell_pack_1", Ammunitions.CRYOCELL_PACK_MK1, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK1;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_I;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_I.getDamage();
        }
    }

    public static class SingleMK2
            extends TurretAmmoCryoCell
    {
        SingleMK2() {
            super("cryocell_2", Ammunitions.CRYOCELL_MK2, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK2;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_II;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_II.getDamage();
        }
    }

    public static class MultiMK2
            extends TurretAmmoCryoCell
    {
        MultiMK2() {
            super("cryocell_pack_2", Ammunitions.CRYOCELL_PACK_MK2, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK2;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_II;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_II.getDamage();
        }
    }

    public static class SingleMK3
            extends TurretAmmoCryoCell
    {
        SingleMK3() {
            super("cryocell_3", Ammunitions.CRYOCELL_MK3, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK3;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_III;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_III.getDamage();
        }
    }

    public static class MultiMK3
            extends TurretAmmoCryoCell
    {
        MultiMK3() {
            super("cryocell_pack_3", Ammunitions.CRYOCELL_PACK_MK3, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK3;
        }

        @Override
        public ITurretProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_III;
        }

        @Override
        public float getDamageInfo() {
            return Projectiles.CRYO_BALL_III.getDamage();
        }
    }
}
