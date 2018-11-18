package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public interface IAmmunitionRegistry
{
    List<IAmmunition> getTypes();

    List<IAmmunition> getTypes(IAmmunitionGroup group);

    IAmmunition getType(ResourceLocation typeId);

    @Nonnull
    IAmmunition getType(@Nonnull ItemStack stack);

    List<IAmmunition> getTypesForTurret(ITurret turret);

    List<IAmmunitionGroup> getGroupsForTurret(ITurret turret);

    boolean register(IAmmunition type);

    @Nonnull
    ItemStack getAmmoItem(ResourceLocation id);

    @Nonnull
    ItemStack getAmmoItem(IAmmunition type);

    List<IAmmunitionGroup> getGroups();

    boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack);
}
