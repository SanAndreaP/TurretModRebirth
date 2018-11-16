/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class Ammunitions
{
    public static final IAmmunition ARROW = new Arrow();
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
        registry.registerAmmoType(ARROW);
        registry.registerAmmoType(SGSHELL);
        registry.registerAmmoType(CRYOCELL_MK1);
        registry.registerAmmoType(CRYOCELL_MK2);
        registry.registerAmmoType(CRYOCELL_MK3);
        registry.registerAmmoType(BULLET);
        registry.registerAmmoType(MGSHELL);
        registry.registerAmmoType(ELECTROLYTECELL);
        registry.registerAmmoType(FLUXCELL);
        registry.registerAmmoType(FUELTANK);
    }

    enum Groups
            implements IAmmunitionGroup
    {
        ARROW (Ammunitions.ARROW.getId(), "arrow", Turrets.CROSSBOW),
        BULLET (Ammunitions.BULLET.getId(), "bullet", Turrets.REVOLVER),
        CRYO_CELL (Ammunitions.CRYOCELL_MK1.getId(), "cryocell", Turrets.CRYOLATOR),
        ELEC_CELL (Ammunitions.ELECTROLYTECELL.getId(), "eleccell", Turrets.FORCEFIELD),
        FUEL_TANK (Ammunitions.FUELTANK.getId(), "fueltank", Turrets.FLAMETHROWER),
        FLUX_CELL (Ammunitions.FLUXCELL.getId(), "fluxcell", Turrets.LASER),
        MG_SHELL (Ammunitions.MGSHELL.getId(), "minigun_shell", Turrets.MINIGUN),
        SG_SHELL (Ammunitions.SGSHELL.getId(), "shotgun_shell", Turrets.SHOTGUN),

        UNKNOWN (new ResourceLocation("null"), "", null);

        private final ResourceLocation id;
        private final String name;
        private final ITurret turret;

        private ItemStack icon;

        Groups(ResourceLocation id, String name, ITurret turret) {
            this.id = id;
            this.name = name;
            this.turret = turret;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public ITurret getTurret() {
            return this.turret;
        }

        @Override
        public ItemStack getIcon() {
            if( this.icon == null ) {
                this.icon = AmmunitionRegistry.INSTANCE.getAmmoItem(this.id);
            }
            return this.icon;
        }
    }
}
