package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.ResourceLocation;

public interface IVariant
{
    <T> T getId();

    ResourceLocation getTexture();

    String getTranslatedName();
}
