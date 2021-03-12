package de.sanandrew.mods.turretmod.api.electrolytegen;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.World;

public interface IElectrolyteInventory
        extends IInventory
{
    World getWorld();
}
