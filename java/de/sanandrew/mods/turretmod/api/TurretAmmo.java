package de.sanandrew.mods.turretmod.api;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds info about turret ammunition. Users of this interface can add custom ammo for turrets.<br>
 * To add new custom ammo, you need to implement this in a class and use the {@link TurretAmmo#AMMO_TYPES} list to add a new instance
 * of your custom ammo implementation.
 */
public interface TurretAmmo
{
    /**
     * The list of available ammo types.<br>
     * If you want to add your own ammo, insert a new {@link TurretAmmo} instance here via {@code TurretAmmo.AMMO_TYPES.add(TurretAmmo)}
     */
    List<TurretAmmo> AMMO_TYPES = new ArrayList<>();

    /**
     * Returns the ammo name. Can be non-unique, but it's preferred.
     * @return the name of the ammunition
     */
    String getName();

    /**
     * Returns the amount of ammo a single item can yield. A value of 2 means, with a stack of 10 items, it'll stock the turret with 20 units of ammo.
     * @return the amount of ammo units an item yields
     */
    int getAmount();

    /**
     * Returns the type of the ammo. This indicates whether or not this ammo is different from other implementations.<br>
     * Behavior:<ul>
     * <li>If this ammo implementation returns the same item as other implementations,
     * the turret will accept this ammo even if the turret was filled up with another one, given it returns the same item.</li>
     * <li>If the turret was filled with ammo returning a different item, it'll remove all ammo previously stored in the turret (and drop it in the
     * inventory/world) and fills the turret up with this type.</li>
     * <li>The turret will treat every ammo implementation returning the same item like one implementation: The first one to be registered, which returns 1 in
     * {@link TurretAmmo#getAmount()}</li>
     * </ul>
     * @return the ammo type as ItemStack
     */
    ItemStack getTypeItem();

    /**
     * Returns the item used by the registry to determine the ammo implementation. This must be unique for every implementation!
     * @return the ammo item
     */
    ItemStack getAmmoItem();

    /**
     * Checks whether the turret can hold this type of ammo.
     * @param turret The turret
     * @return true, if it's applicable, false otherwise
     */
    boolean isApplicablToTurret(Turret turret);

    /**
     * Creates a new projectile instance to be fired by the turret.
     * @param world The world the projectile spawns in
     * @param turret The turret shooting the projectile
     * @return A new instance of a projectile entity
     */
    TurretProjectile<? extends EntityArrow> getProjectile(World world, Turret turret);
}
