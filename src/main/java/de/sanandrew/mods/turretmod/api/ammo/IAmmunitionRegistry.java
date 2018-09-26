package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IAmmunitionRegistry
{
    List<IAmmunition> getTypes();

    List<UUID> getGroups();

    IAmmunition[] getTypes(UUID groupId);

    IAmmunition getType(UUID typeId);

    @Nonnull
    IAmmunition getType(@Nonnull ItemStack stack);

    List<IAmmunition> getTypesForTurret(ITurret turret);

    List<IAmmunitionGroup> getGroupsForTurret(ITurret turret);

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    boolean registerAmmoType(IAmmunition type);

    @Nonnull
    ItemStack getAmmoItem(UUID id);

    @Nonnull
    ItemStack getAmmoItem(IAmmunition type);

    boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack);
}
