package de.sanandrew.mods.turretmod.compat.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class CTHandler
{
    public static void onInit() {
        if( Loader.isModLoaded("crafttweaker") ) {
            CTElectrolyte.ACTIONS.forEach(IAction::apply);

            CTAssembly.ACTIONS_PRE.forEach(IAction::apply);
            CTAssembly.ACTIONS_POST.forEach(IAction::apply);
        }
    }

    static ResourceLocation getRL(String id) {
        return id.contains(":") ? new ResourceLocation(id) : new ResourceLocation(CraftTweaker.MODID, id);
    }
}
