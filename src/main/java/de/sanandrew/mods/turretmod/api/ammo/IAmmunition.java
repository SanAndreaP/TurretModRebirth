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
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>A registry object defining an ammunition item.</p>
 */
@SuppressWarnings("unused")
public interface IAmmunition
        extends IRegistryObject
{
    String[] NO_SUBTYPES = new String[0];

    @Nonnull
    default String[] getSubtypes() {
        return NO_SUBTYPES;
    }

    /**
     * @return The amount of rounds provided by one item.
     */
    int getCapacity();

    /**
     * @return the turret delegate that can use this ammo, cannot be <tt>null</tt>
     */
    @Nonnull
    ITurret getApplicableTurret();

    /**
     * <p>Returns the projectile delegate to be shot.</p>
     * <p>If this returns null, no projectile is fired. Use the {@link de.sanandrew.mods.turretmod.api.event.TargetingEvent.ProcessorTick TargetingEvent.ProcessorTick} event
     *    in order for custom turret effects (like shooting multiple projectiles at once).</p>
     *
     * @param turret The turret firing the projectile.
     * @return The projectile delegate or <tt>null</tt>, if no projectile should be fired.
     */
    @Nullable
    IProjectile getProjectile(ITurretEntity turret);

    /**
     * <p>Allows this ammunition object to add tooltip information, if needed.</p>
     *
     * @param stack The <tt>ItemStack</tt> representing this ammunition object.
     * @param world The world the ItemStack exists in, if any.
     * @param tooltip The list of lines to be populated by this method, preferrably an <tt>ArrayList</tt>.
     * @param flag The flag of the tooltip to be drawn, determines if advanced information is to be shown or not.
     */
    default void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) { }

    Range<Float> getDamageInfo();
}
