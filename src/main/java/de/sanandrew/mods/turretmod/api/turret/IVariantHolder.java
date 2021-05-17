package de.sanandrew.mods.turretmod.api.turret;

public interface IVariantHolder
{
    default IVariant getVariant(ITurretInst turretInst, Object id) {
        return getVariant(id);
    }

    default boolean hasVariants() {
        return true;
    }

    IVariant getVariant(Object id);

    IVariant getVariant(String id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
