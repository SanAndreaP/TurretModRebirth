package de.sanandrew.mods.turretmod.client.compat.patchouli;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.IRegistryObject;
import net.minecraftforge.fml.common.Loader;

public class PatchouliHelper
{
    public static void preInit() {
        if( Loader.isModLoaded("patchouli") ) {
            PatchouliMouseEventHandler.register();
            PageCustomCrafting.registerPage();
        }
    }

    public static vazkii.patchouli.common.util.ItemStackUtil.StackWrapper getWrapper(IRegistry<?> r, IRegistryObject o) {
        return new vazkii.patchouli.common.util.ItemStackUtil.StackWrapper(r.getItem(o.getId()));
    }
}
