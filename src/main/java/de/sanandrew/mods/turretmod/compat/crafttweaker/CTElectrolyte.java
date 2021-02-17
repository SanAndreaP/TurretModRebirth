package de.sanandrew.mods.turretmod.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteManager;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@ZenClass("mods." + TmrConstants.ID + ".ElectrolyteGenerator")
@ZenRegister
@ModOnly(TmrConstants.ID)
@SuppressWarnings("unused")
public class CTElectrolyte
{
    public static List<IAction> ACTIONS = new LinkedList<>();

    private static int ctId = 0;

    @ZenMethod
    public static void removeAllFuels() {
        ACTIONS.add(new IAction()
        {
            @Override
            public void apply() {
                ElectrolyteManager.INSTANCE.clearFuels();
            }

            @Override
            public String describe() {
                return "Removing all Electrolyte Generator recipes";
            }
        });
    }

    @ZenMethod
    public static void removeFuelByName(String id) {
        final ResourceLocation idRL = CTHandler.getRL(id);
        ACTIONS.add(new IAction()
        {
            @Override
            public void apply() {
                if( ElectrolyteManager.INSTANCE.getFuel(idRL) == null ) {
                    CraftTweakerAPI.logError("Failed to remove Electrolyte Generator recipe with name " + idRL.toString());
                }

                ElectrolyteManager.INSTANCE.removeFuel(idRL);
            }

            @Override
            public String describe() {
                return "Removing Electrolyte Generator recipe with name " + idRL.toString();
            }
        });
    }

    @ZenMethod
    public static void removeFuelsByInput(IIngredient input) {
        final Ingredient inputInst = CraftTweakerMC.getIngredient(input);
        ACTIONS.add(new IAction()
        {
            @Override
            public void apply() {
                final ItemStack[] matchingStacks = inputInst.getMatchingStacks();
                int oldSz = ElectrolyteManager.INSTANCE.getFuels().size();
                new ArrayList<>(ElectrolyteManager.INSTANCE.getFuels()).forEach(r -> {
                    if( r.getIngredients().stream().anyMatch(i -> Arrays.stream(matchingStacks).anyMatch(i::apply)) ) {
                        ElectrolyteManager.INSTANCE.removeFuel(r.getId());
                    }
                });

                if( oldSz == ElectrolyteManager.INSTANCE.getFuels().size() ) {
                    CraftTweakerAPI.logError("Failed to remove Electrolyte Generator recipe with input " + input.toString());
                }
            }

            @Override
            public String describe() {
                return "Removing Electrolyte Generator recipe with input " + input.toString();
            }
        });
    }

    @ZenMethod
    public static void addFuel(IIngredient input, IItemStack trash, IItemStack treasure, float efficiency, int processingTime) {
        registerFuelH(buildCtId(), input, trash, null, treasure, null, efficiency, processingTime);
    }

    @ZenMethod
    public static void addFuel(IIngredient input, IItemStack trash, float trashChance,
                               IItemStack treasure, float treasureChance, float efficiency, int processingTime)
    {
        registerFuelH(buildCtId(), input, trash, trashChance, treasure, treasureChance, efficiency, processingTime);
    }

    @ZenMethod
    public static void addFuel(String id, IIngredient input, IItemStack trash, IItemStack treasure,
                               float efficiency, int processingTime)
    {
        registerFuelH(CTHandler.getRL(id), input, trash, null, treasure, null, efficiency, processingTime);
    }

    @ZenMethod
    public static void addFuel(String id, IIngredient input, IItemStack trash, float trashChance,
                               IItemStack treasure, float treasureChance, float efficiency, int processingTime)
    {
        registerFuelH(CTHandler.getRL(id), input, trash, trashChance, treasure, treasureChance, efficiency, processingTime);
    }

    private static ResourceLocation buildCtId() {
        return new ResourceLocation(CraftTweaker.MODID, String.format("ct_elec_%d", ++ctId));
    }

    private static void registerFuelH(ResourceLocation id, IIngredient input, IItemStack trash, Float trashChance,
                                     IItemStack treasure, Float treasureChance, float efficiency, int processingTime)
    {
        final Ingredient inputInst = CraftTweakerMC.getIngredient(input);
        final ItemStack  trashInst = CraftTweakerMC.getItemStack(trash);
        final ItemStack  treasureInst = CraftTweakerMC.getItemStack(treasure);

        ACTIONS.add(new IAction()
        {
            @Override
            public void apply() {
                ElectrolyteRecipe recipe;
                if( trashChance == null || treasureChance == null ) {
                    recipe = new ElectrolyteRecipe(id, inputInst, trashInst, treasureInst, efficiency, processingTime);
                } else {
                    recipe = new ElectrolyteRecipe(id, inputInst, trashInst, treasureInst, efficiency, processingTime, trashChance, treasureChance);
                }
                ElectrolyteManager.INSTANCE.registerFuel(recipe);
            }

            @Override
            public String describe() {
                return "Adding Electrolyte Generator recipe <" + id.toString() + ">";
            }
        });
    }
}
