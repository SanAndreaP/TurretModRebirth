package de.sanandrew.mods.turretmod.api.turret;

public interface IVariantHolder
{
    default IVariant getVariant(ITurretEntity turret, Object id) {
        return getVariant(id);
    }

    default boolean hasVariants() {
        return true;
    }

    IVariant[] getVariants();

    IVariant getVariant(Object id);

    IVariant getVariant(String id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
