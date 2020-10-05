package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.IRegistryObject;
import vazkii.patchouli.common.util.ItemStackUtil;

public class PatchouliHelper
{
    public static ItemStackUtil.StackWrapper getWrapper(IRegistry<?> r, IRegistryObject o) {
        return new ItemStackUtil.StackWrapper(r.getItem(o.getId()));
    }
}
