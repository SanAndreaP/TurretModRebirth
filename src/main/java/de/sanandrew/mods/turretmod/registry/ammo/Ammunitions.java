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

public final class Ammunitions
{
    public static final IAmmunition ARROW = new Arrow();
    public static final IAmmunition HARPOON = new Harpoon();
    public static final IAmmunition SGSHELL = new ShotgunShell();
    public static final IAmmunition CRYOCELL_MK1 = new CryoCell.Mk1();
    public static final IAmmunition CRYOCELL_MK2 = new CryoCell.Mk2();
    public static final IAmmunition CRYOCELL_MK3 = new CryoCell.Mk3();
    public static final IAmmunition BULLET = new Bullet();
    public static final IAmmunition MGSHELL = new MinigunShell();
    public static final IAmmunition ELECTROLYTECELL = new ElectrolyteCell();
    public static final IAmmunition FLUXCELL = new FluxCell();
    public static final IAmmunition FUELTANK = new FuelTank();

    public static void initialize(IAmmunitionRegistry registry) {
        IRegistry.registerAll(registry,
                              ARROW, HARPOON, SGSHELL, CRYOCELL_MK1, CRYOCELL_MK2, CRYOCELL_MK3, BULLET, MGSHELL, ELECTROLYTECELL,
                              FLUXCELL, FUELTANK);
    }

    enum Groups
            implements IAmmunitionGroup
    {
        ARROW("arrow", Turrets.CROSSBOW, Ammunitions.ARROW.getId()),
        HARPOON("harpoon", Turrets.HARPOON, Ammunitions.HARPOON.getId()),
        BULLET("bullet", Turrets.REVOLVER, Ammunitions.BULLET.getId()),
        CRYO_CELL("cryocell", Turrets.CRYOLATOR, Ammunitions.CRYOCELL_MK1.getId()),
        ELEC_CELL("eleccell", Turrets.FORCEFIELD, Ammunitions.ELECTROLYTECELL.getId()),
        FUEL_TANK("fueltank", Turrets.FLAMETHROWER, Ammunitions.FUELTANK.getId()),
        FLUX_CELL("fluxcell", Turrets.LASER, Ammunitions.FLUXCELL.getId()),
        MG_SHELL("shell.minigun", Turrets.MINIGUN, Ammunitions.MGSHELL.getId()),
        SG_SHELL("shell.shotgun", Turrets.SHOTGUN, Ammunitions.SGSHELL.getId()),

        UNKNOWN("null", TurretRegistry.INSTANCE.getDefaultObject(), null);

        private final ResourceLocation id;
        private final ResourceLocation typeIcon;
        private final ITurret turret;

        private ItemStack icon;

        Groups(@Nonnull String name, @Nonnull ITurret turret, ResourceLocation typeIcon) {
            this.id = new ResourceLocation(TmrConstants.ID, "ammo." + name);
            this.turret = turret;
            this.typeIcon = typeIcon;
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
        @Nonnull
        public ItemStack getIcon() {
            if( this.typeIcon == null ) {
                return ItemStack.EMPTY;
            }

            if( this.icon == null ) {
                this.icon = AmmunitionRegistry.INSTANCE.getItem(this.typeIcon);
            }
            return this.icon;
        }
    }
}
