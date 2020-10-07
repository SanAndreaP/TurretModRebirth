/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.electrolytegen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ElectrolyteRecipeLoader
{
    public static void initialize(IElectrolyteManager registry) {
        TmrConstants.LOG.log(Level.INFO, "Initializing Electrolyte Generator recipes...");
        long prevTime = System.nanoTime();
        Loader.instance().getActiveModList().forEach(mod -> loadJsonRecipes(mod, registry));
        long timeDelta = (System.nanoTime() - prevTime) / 1_000_000;
        TmrConstants.LOG.log(Level.INFO, String.format("Initializing Electrolyte Generator recipes done in %d ms. Found %d recipes.", timeDelta, registry.getFuels().size()));
    }

    private static void loadJsonRecipes(ModContainer mod, IElectrolyteManager registry) {
        String modId = mod.getModId();
        MiscUtils.findFiles(mod, "assets/" + modId + '/' + TmrConstants.ID + "/recipes/electrolytegen/", null, (root, file) -> processJson(modId, root, file, registry));
    }

    private static boolean processJson(final String modId, Path root, Path file, IElectrolyteManager registry) {
        if( !file.toString().endsWith(".json") ) {
            return true;
        }

        try( BufferedReader reader = Files.newBufferedReader(file) ) {
            JsonObject json = JsonUtils.fromJson(reader, JsonObject.class);
            ResourceLocation id = TmrUtils.getPathedRL(modId, root, file);
            //new ResourceLocation(modId, "recipes/electrolytegen/" + file.getFileName().toString());

            if( json == null ) {
                throw new JsonSyntaxException("Cannot read valid JSON");
            } else if( json.isJsonNull()) {
                registry.removeFuel(id);
                return true;
            }

            JsonContext context = new JsonContext(modId);
            Ingredient electrolyte = CraftingHelper.getIngredient(json.get("electrolyte"), context);
            ItemStack trash = ItemStack.EMPTY;
            ItemStack treasure = ItemStack.EMPTY;

            JsonElement elem = json.get("trash");
            if( elem != null && !elem.isJsonNull() ) {
                trash = JsonUtils.getItemStack(elem);
            }
            elem = json.get("treasure");
            if( elem != null && !elem.isJsonNull() ) {
                treasure = JsonUtils.getItemStack(elem);
            }

            registry.registerFuel(new ElectrolyteRecipe(id, electrolyte, trash, treasure, JsonUtils.getFloatVal(json.get("efficiency")),
                                                        JsonUtils.getIntVal(json.get("ticksProcessing")),
                                                        JsonUtils.getFloatVal(json.get("trashChance"), 0.2F), JsonUtils.getFloatVal(json.get("treasureChance"), 0.02F)));
        } catch( JsonParseException e ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Parsing error loading electrolyte generator recipe from %s", file), e);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Couldn't read recipe from %s", file), e);
        }

        return true;
    }
}
