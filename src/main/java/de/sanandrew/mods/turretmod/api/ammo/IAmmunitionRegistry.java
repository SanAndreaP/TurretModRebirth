package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

@SuppressWarnings("UnusedReturnValue")
public interface IAmmunitionRegistry
        extends IRegistry<IAmmunition>
{
    @Nonnull
    Collection<IAmmunition> getTypes(ITurret turret);
}
