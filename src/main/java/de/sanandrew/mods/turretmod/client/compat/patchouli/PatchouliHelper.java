package de.sanandrew.mods.turretmod.client.compat.patchouli;

import net.minecraftforge.fml.ModList;

public class PatchouliHelper
{
    public static void preInit() {
        if( ModList.get().isLoaded("patchouli") ) {
//            PatchouliMouseEventHandler.register();
//            PageCustomCrafting.registerPage();
        }
    }

//    public static vazkii.patchouli.common.util.ItemStackUtil.StackWrapper getWrapper(IRegistry<?> r, IRegistryObject o) {
//        return new vazkii.patchouli.common.util.ItemStackUtil.StackWrapper(r.getItem(o.getId()));
//    }
}
