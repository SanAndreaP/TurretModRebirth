package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A registry specialized to handling objects of the type {@link IAmmunition}
 *
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerAmmo(IAmmunitionRegistry) ITmrPlugin.registerAmmo(IAmmunitionRegistry)
 */
public interface IAmmunitionRegistry
        extends IRegistry<IAmmunition>
{

    /**
     * <p>Returns an unmodifiable view of the objects registered in this registry, compatible with the given turret.</p>
     *
     * @param turret The turret which should be filtered
     * @return an unmodifiable view of registered objects compatible with this turret.
     */
    @Nonnull
    Collection<IAmmunition> getObjects(ITurret turret);

    String getSubtype(ItemStack stack);

    ItemStack setSubtype(ItemStack stack, String type);

    @Nonnull
    ItemStack getItem(ResourceLocation id, String subtype);
}
