package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.inventory.IInventory;

public interface IVariantHolder
{
    default IVariant getVariant(ITurretEntity turret, Object id) {
        return getVariant(id);
    }

    default boolean hasVariants() {
        return true;
    }

    IVariant[] getVariants();

    IVariant getVariant(IInventory inv);

    IVariant getVariant(Object id);

    IVariant getVariant(String id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
