/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public final class Ammunitions
{
    public static final IAmmunition ARROW           = new Arrow();
    public static final IAmmunition HARPOON         = new Harpoon();
    public static final IAmmunition SGSHELL         = new ShotgunShell();
    public static final IAmmunition CRYOCELL_MK1    = new CryoCell.Mk1();
    public static final IAmmunition CRYOCELL_MK2    = new CryoCell.Mk2();
    public static final IAmmunition CRYOCELL_MK3    = new CryoCell.Mk3();
    public static final IAmmunition BULLET          = new Bullet();
    public static final IAmmunition MGSHELL         = new MinigunShell();
    public static final IAmmunition ELECTROLYTECELL = new ElectrolyteCell();
    public static final IAmmunition FLUXCELL        = new FluxCell();
    public static final IAmmunition FUELTANK        = new FuelTank();

    public static void initialize(IAmmunitionRegistry registry) {
        IRegistry.registerAll(registry,
                              ARROW, HARPOON, SGSHELL, CRYOCELL_MK1, CRYOCELL_MK2, CRYOCELL_MK3, BULLET, MGSHELL, ELECTROLYTECELL,
                              FLUXCELL, FUELTANK);
    }

    enum Groups
            implements IAmmunitionGroup
    {
        ARROW("arrow", Turrets.CROSSBOW),
        HARPOON("harpoon", Turrets.HARPOON),
        BULLET("bullet", Turrets.REVOLVER),
        CRYO_CELL("cryocell", Turrets.CRYOLATOR),
        ELEC_CELL("eleccell", Turrets.FORCEFIELD),
        FUEL_TANK("fueltank", Turrets.FLAMETHROWER),
        FLUX_CELL("fluxcell", Turrets.LASER),
        MG_SHELL("shell_minigun", Turrets.MINIGUN),
        SG_SHELL("shell_shotgun", Turrets.SHOTGUN),

        UNKNOWN("null", TurretRegistry.INSTANCE.getDefaultObject());

        private final ResourceLocation id;
        private final ResourceLocation patchouliId;
        private final ITurret          turret;

        Groups(@Nonnull String name, @Nonnull ITurret turret) {
            this.id = new ResourceLocation(TmrConstants.ID, "ammo." + name);
            this.turret = turret;
            this.patchouliId = new ResourceLocation(TmrConstants.ID, "ammo_" + name);
        }

        @Override
        @Nonnull
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        @Nonnull
        public ITurret getTurret() {
            return this.turret;
        }

        @Override
        public ResourceLocation getBookEntryId() {
            return this.patchouliId;
        }

        @Override
        @Nonnull
        public ItemStack getIcon() {
            return ItemStack.EMPTY;
        }
    }
}
