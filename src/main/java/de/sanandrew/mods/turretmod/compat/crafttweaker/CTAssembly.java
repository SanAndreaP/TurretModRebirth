package de.sanandrew.mods.turretmod.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@ZenClass("mods." + TmrConstants.ID + ".TurretAssembly")
@ZenRegister
@ModOnly(TmrConstants.ID)
@SuppressWarnings("unused")
public class CTAssembly
{
    public static List<IAction> ACTIONS_PRE = new LinkedList<>();
    public static List<IAction> ACTIONS_POST = new LinkedList<>();

    private static int ctId = 0;

    @ZenMethod
    public static void removeAllRecipes() {
        removeAllRecipes(null);
    }

    @ZenMethod
    public static void removeAllRecipes(String group) {
        ACTIONS_PRE.add(new IAction()
        {
            @Override
            public void apply() {
                if( group != null ) {
                    AssemblyManager.INSTANCE.clearRecipesByGroup(group);
                } else {
                    AssemblyManager.INSTANCE.clearRecipes();
                }
            }

            @Override
            public String describe() {
                return "Removing all Turret Assembly Table recipes"
                       + (group != null ? " within group " + group : "");
            }
        });
    }

    @ZenMethod
    public static void removeRecipeByName(String id) {
        final ResourceLocation idRL = CTHandler.getRL(id);
        ACTIONS_PRE.add(new IAction()
        {
            @Override
            public void apply() {
                if( AssemblyManager.INSTANCE.getRecipe(idRL) == null ) {
                    CraftTweakerAPI.logError("Failed to remove Turret Assembly Table recipe with name " + idRL.toString());
                }

                AssemblyManager.INSTANCE.removeRecipe(idRL);
            }

            @Override
            public String describe() {
                return "Removing Turret Assembly Table recipe with name " + idRL.toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipesByOutput(IItemStack output) {
        removeRecipesByOutput(output, null);
    }

    @ZenMethod
    public static void removeRecipesByOutput(IItemStack output, String group) {
        final ItemStack outputInst = CraftTweakerMC.getItemStack(output);
        final Function<String, List<IAssemblyRecipe>> grabRecipes = (gn) -> {
            if( gn != null ) {
                return AssemblyManager.INSTANCE.getRecipes(gn);
            } else {
                return AssemblyManager.INSTANCE.getRecipes();
            }
        };
        ACTIONS_PRE.add(new IAction()
        {
            @Override
            public void apply() {
                int               oldSz          = grabRecipes.apply(group).size();
                new ArrayList<>(grabRecipes.apply(group)).forEach(r -> {
                    if( ItemStackUtils.areEqual(outputInst, r.getRecipeOutput(), outputInst.hasTagCompound()) ) {
                        AssemblyManager.INSTANCE.removeRecipe(r.getId());
                    }
                });

                if( oldSz == grabRecipes.apply(group).size() ) {
                    CraftTweakerAPI.logError("Failed to remove Turret Assembly Table recipe with input " + output.toString()
                                             + (group != null ? " within group " + group : ""));
                }
            }

            @Override
            public String describe() {
                return "Removing Turret Assembly Table recipe with input " + output.toString()
                       + (group != null ? " within group " + group : "");
            }
        });
    }

    @ZenMethod
    public static void addRecipe(String id, String group, IIngredient[] inputs, IItemStack output, int fluxPerTick, int processTime) {
        registerRecipeH(CTHandler.getRL(id), group, inputs, output, fluxPerTick, processTime);
    }

    @ZenMethod
    public static void addRecipe(String group, IIngredient[] inputs, IItemStack output, int fluxPerTick, int processTime) {
        registerRecipeH(buildCtId(), group, inputs, output, fluxPerTick, processTime);
    }

    @ZenMethod
    public static void setGroupIcon(String group, IItemStack item) {
        final ItemStack itemInst = CraftTweakerMC.getItemStack(item);

        ACTIONS_POST.add(new IAction()
        {
            @Override
            public void apply() {
                AssemblyManager.INSTANCE.setGroupIcon(group, itemInst);
            }

            @Override
            public String describe() {
                return "Set Turret Assembly Table group icon to " + itemInst.toString() + " for group " + group;
            }
        });
    }

    @ZenMethod
    public static void setGroupOrder(String group, int ordinal) {
        ACTIONS_POST.add(new IAction()
        {
            @Override
            public void apply() {
                AssemblyManager.INSTANCE.setGroupOrder(group, ordinal);
            }

            @Override
            public String describe() {
                return "Setting Turret Assembly Table group order to " + ordinal + " for group " + group;
            }
        });
    }

    private static ResourceLocation buildCtId() {
        return new ResourceLocation(CraftTweaker.MODID, String.format("ct_saaembly_%d", ++ctId));
    }

    private static void registerRecipeH(ResourceLocation id, String group, IIngredient[] inputs, IItemStack output, int fluxPerTick, int processTime) {
        final NonNullList<Ingredient> inputsInst = NonNullList.create();
        final ItemStack outputInst = CraftTweakerMC.getItemStack(output);
        Arrays.stream(inputs).forEach(i -> inputsInst.add(new AssemblyIngredient(i.getAmount(), Arrays.stream(i.getItemArray()).map(CraftTweakerMC::getItemStack).toArray(ItemStack[]::new))));

        ACTIONS_PRE.add(new IAction()
        {
            @Override
            public void apply() {
                try {
                    AssemblyManager.INSTANCE.registerRecipe(new AssemblyRecipe(id, group, inputsInst, fluxPerTick, processTime, outputInst), true);
                } catch( RuntimeException ex ) {
                    CraftTweakerAPI.logError(ex.getMessage());
                }
            }

            @Override
            public String describe() {
                return "Adding Turret Assembly Table recipe <" + id.toString() + ">";
            }
        });
    }
}
