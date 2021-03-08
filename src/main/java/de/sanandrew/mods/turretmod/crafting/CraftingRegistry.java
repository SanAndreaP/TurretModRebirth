package de.sanandrew.mods.turretmod.crafting;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CraftingRegistry
{
    @SubscribeEvent
    public static void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        CraftingHelper.register(new ResourceLocation(TmrConstants.ID, "turret_assembly_ingredient"), AssemblyIngredient.Serializer.INSTANCE);

        event.getRegistry().register(AssemblyRecipe.Serializer.INSTANCE.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret_assembly")));
    }
}
