package de.sanandrew.mods.turretmod.api.ammo;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class Ammunition
        implements IAmmunition
{
    private final ResourceLocation id;

    protected int capacity;

    public Ammunition(ResourceLocation id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }
}
