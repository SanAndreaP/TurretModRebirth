/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ITurretAmmo<T extends Entity & IProjectile>
{
    /**
     * Returns the unlocalized name for this ammo type item. The name can (and should) differ, even
     * if they're the same type.<br>
     * <i>Example:</i> Arrows and Quivers (each proviing the same ammo type) have the name {@code arrow_sng} and
     * {@code arrow_lrg} respectively.
     * @return A name for this item
     */
    String getName();

    /**
     * Returns the ID for this ammo type item. It needs to be unique from all other items registered.<br>
     * <i>Example:</i> Arrows and Quivers IDs begin with {@code 7B49...} and {@code E6D5...} respectively.<br>
     * Cannot be {@code null}!
     * @return A unique ID for this item
     */
    @Nonnull
    UUID getId();

    /**
     * Returns the ID for this ammo type item. It needs to be unique from all other types registered, but must
     * be the same for different items with the same type.<br>
     * <i>Example:</i> Arrows and Quivers have the same ID beginning with {@code 7B49...}, Flux Cells and Flux Cell
     * Packs have an ID beginning with {@code 4880...}, Cryo-Cells have a different ID depending on their tier.<br>
     * Cannot be {@code null}!
     * @return A unique ID for this type
     */
    @Nonnull
    UUID getTypeId();

    /**
     * Returns the ID for this ammo group. This is used in the Turret Info Tablet to group them together in one page.<br>
     * <i>Example:</i> Arrows and Quivers have the same ID beginning with {@code 7B49...}, All Cryo-Cells and their
     * Packs have an ID beginning with {@code 0B56...}.<br>
     * Cannot be {@code null}!
     * @return A unique ID for this type
     */
    @Nonnull
    UUID getGroupId();

    /**
     * Returns the unlocalized name for this ammo to be used in the Turret Info Tablet. This should return the same
     * name within the same group (See {@link #getGroupId()}).<br>
     * <i>Example:</i> Arrows and Quivers IDs both have {@code arrow} as their name.<br>
     * @return A name for the Turret Info Tablet
     */
    String getInfoName();


    float getInfoDamage();
    UUID getRecipeId();
    int getAmmoCapacity();
    Class<T> getEntityClass();
    T getEntity(EntityTurret turret);
    Class<? extends EntityTurret> getTurret();
    ResourceLocation getModel();
    ItemStack getStoringAmmoItem();
}
