package de.sanandrew.mods.turretmod.recipe;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class RecipeRegistry
{
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TmrConstants.ID);

    public static final ElectrolyteRecipe.Serializer ELECTROLYTE_RECIPE_SER = new ElectrolyteRecipe.Serializer();
    public static final AssemblyRecipe.Serializer    ASSEMBLY_RECIPE_SER = new AssemblyRecipe.Serializer();

    private RecipeRegistry() { /* no-op */ }

    public static void register(IEventBus bus)
    {
//        CraftingHelper.register(new ResourceLocation(TmrConstants.ID, "turret_assembly_ingredient"), AssemblyIngredient.Serializer.INSTANCE);

        RECIPE_SERIALIZERS.register("electrolyte_generator", () -> ELECTROLYTE_RECIPE_SER);
        RECIPE_SERIALIZERS.register("turret_assembly", () -> ASSEMBLY_RECIPE_SER);

        RECIPE_SERIALIZERS.register("turret_assembly_special_crossbow_turret", () -> TurretVariantRecipe.Crossbow.SERIALIZER);
//        event.getRegistry().register(AssemblyRecipe.Serializer.INSTANCE.setRegistryName(new ResourceLocation(TmrConstants.ID, "turret_assembly")));

        RECIPE_SERIALIZERS.register(bus);
    }
}
