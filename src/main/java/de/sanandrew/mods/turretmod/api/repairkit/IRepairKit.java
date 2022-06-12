package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A repair kit object that can heal a turret.
 * <p>Even if a repair kit does not have any healing capabilities ({@link IRepairKit#getBaseRestorationAmount()} returns 0), it may have side
 * effects applied to the turret via {@link IRepairKit#onApply(ITurretEntity)}.</p>
 */
public interface IRepairKit
        extends IRegistryObject
{
    /**
     * Returns the amount of health points (1HP = ½ hearts) this repair kit restores.
     *
     * @return the amount of health points (1HP = ½ hearts) this repair kit restores
     */
    float getBaseRestorationAmount();

    /**
     * Returns <tt>true</tt> if this repair kit is applicable to the given turret.
     *
     * @param turret the turret that should be checked
     * @return <tt>true</tt> if this repair kit is applicable to the given turret
     */
    default boolean isApplicable(@Nonnull ITurretEntity turret) {
        return turret.get().getHealth() + this.getBaseRestorationAmount() <= turret.get().getMaxHealth();
    }

    /**
     * Performs actions specific to this repair kit related to the given turret.
     * This should be called after the repair kit has been applied to the turret.
     *
     * @param turret the turret that got repaired
     */
    default void onApply(@Nonnull ITurretEntity turret) { }

    /**
     * Appends lines of additional information to the given list for this repair kit.
     *
     * @param stack the <tt>ItemStack</tt> representing this repair kit
     * @param world the world the <tt>ItemStack</tt> exists in, if any
     * @param tooltip the list of lines added to the tooltip
     * @param flag a flag on what info the tooltip should contain, currently only determines if advanced information is to be shown or not
     */
    default void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flag) { }
}
