package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;

import javax.annotation.Nonnull;

@SuppressWarnings("UnusedReturnValue")
public interface ITurretRegistry
        extends IRegistry<ITurret>
{
    void registerItems(RegistryEvent.Register<Item> event, String modId);

    @Nonnull
    ItemStack getItem(ITurretEntity turretInst);


}
