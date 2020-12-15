package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.ResourceLocation;

public interface IVariantHolder
{

    IVariant getVariant(ITurretInst turretInst, ResourceLocation id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
