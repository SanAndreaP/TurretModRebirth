package de.sanandrew.mods.turretmod.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface ILeveledInventory
        extends IInventory
{
    World getLevel();
}
