package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;

public interface IRepairKitRegistry
        extends IRegistry<IRepairKit>
{
    void registerItems(DeferredRegister<Item> register, String modId);
}
