/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>A registry object defining an ammunition item.</p>
 */
public interface IAmmunition
        extends IRegistryObject
{
    /**
     * <p>Returns the ammunition group.</p>
     * <p><i>Example:</i> Each Cryo-Cell ammunition object returns the same group instance (with the ID <tt>sapturretmod:cryocell</tt>).
     *
     * @return The group representing this ammunition object.
     */
    @Nonnull
    IAmmunitionGroup getGroup();

    /**
     * <p>Returns the damage dealt, represented as a range, by a projectile spawned by this ammunition object. This is primarily used by the Turret Lexicon.</p>
     * <p>If the projectile deals the same amount of damage, this returns {@link Range#is(Comparable) Range.is(damage)}.</p>
     * <p>If the projectile deals a variable amount of damage, this returns {@link Range#between(Comparable, Comparable) Range.between(minDamage, maxDamage)}.</p>
     *
     * @return The amount of damage, as a range, a projectile can deal.
     */
    @Nonnull
    Range<Float> getDamageInfo();

    /**
     * @return The amount of rounds provided by one item.
     */
    int getAmmoCapacity();

    /**
     * <p>Returns the projectile delegate to be shot.</p>
     * <p>If this returns null, no projectile is fired. Use the {@link de.sanandrew.mods.turretmod.api.event.TargetingEvent.ProcessorTick TargetingEvent.ProcessorTick} event
     *    in order for custom turret effects (like shooting multiple projectiles at once).</p>
     *
     * @param turretInst The turret firing the projectile.
     * @return The projectile delegate or <tt>null</tt>, if no projectile should be fired.
     */
    @Nullable
    IProjectile getProjectile(ITurretInst turretInst);

    default String getDisplayName(ItemStack stack) {
        return null;
    }

    default String getItemTranslationKey() {
        return null;
    }

    /**
     * <p>Allows this ammunition object to add tooltip information, if needed.</p>
     * <p><i>Notice: when overriding this method, please call <tt>IAmmunition.super.addInformation(...)</tt> at the end of your implementation.</i></p>
     *
     * @param stack The <tt>ItemStack</tt> representing this ammunition object.
     * @param world The world the ItemStack exists in, if any.
     * @param tooltip The list of lines to be populated by this method, preferrably an <tt>ArrayList</tt>.
     * @param flag The flag of the tooltip to be drawn, determines if advanced information is to be shown or not.
     */
    default void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if( flag.isAdvanced() ) {
            tooltip.add(TextFormatting.DARK_GRAY + "aid: " + this.getId().toString());
            tooltip.add(TextFormatting.DARK_GRAY + "gid: " + this.getGroup().getId().toString());
        }
    }
}
