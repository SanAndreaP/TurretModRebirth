package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.ResourceLocation;

public interface IVariantHolder
{
    default IVariant getVariant(ITurretInst turretInst, ResourceLocation id) {
        return getVariant(id);
    }

    IVariant getVariant(ResourceLocation id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
