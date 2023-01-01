package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

@SuppressWarnings("UnusedReturnValue")
public interface ITurretRegistry
        extends IRegistry<ITurret>
{
    void registerItems(DeferredRegister<Item> register, String modId);

    @Nonnull
    default ItemStack getItem(ITurretEntity turret) {
        return this.getItem(turret, 1);
    }

    @Nonnull
    ItemStack getItem(ITurretEntity turret, int count);
}
