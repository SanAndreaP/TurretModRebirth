package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * A registry specialized to handling objects of the type {@link IRepairKit}
 */
public interface IRepairKitRegistry
        extends IRegistry<IRepairKit>
{
}
