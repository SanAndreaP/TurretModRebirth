package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface ITurretAmmoRegistry
{
    List<ITurretAmmo> getRegisteredTypes();

    List<UUID> getGroups();

    ITurretAmmo[] getTypes(UUID groupId);

    ITurretAmmo getType(UUID typeId);

    @Nonnull
    ITurretAmmo getType(@Nonnull ItemStack stack);

    List<ITurretAmmo> getTypesForTurret(ITurret turret);

    @SuppressWarnings("unused")
    boolean registerAmmoType(ITurretAmmo<?> type);

    boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack);
}
