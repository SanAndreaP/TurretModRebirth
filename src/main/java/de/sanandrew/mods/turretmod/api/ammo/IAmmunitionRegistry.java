package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface IAmmunitionRegistry
{
    List<IAmmunition> getRegisteredTypes();

    List<UUID> getGroups();

    IAmmunition[] getTypes(UUID groupId);

    IAmmunition getType(UUID typeId);

    IAmmunition getType(ItemStack stack);

    List<IAmmunition> getTypesForTurret(ITurret turret);

    @SuppressWarnings("unused")
    boolean registerAmmoType(IAmmunition<?> type);

    ItemStack getAmmoItem(IAmmunition type);

    boolean areAmmoItemsEqual(ItemStack firstStack, ItemStack secondStack);
}
