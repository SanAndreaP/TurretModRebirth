/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TurretAssemblyRecipes
{
    public static void initialize(final IAssemblyManager registry) {
        TmrConstants.LOG.log(Level.INFO, "Initializing Turret Assembly recipes...");
        long prevTime = System.nanoTime();
        Loader.instance().getActiveModList().forEach(mod -> loadJsonRecipes(mod, registry));
        long timeDelta = (System.nanoTime() - prevTime) / 1_000_000;
        TmrConstants.LOG.log(Level.INFO, String.format("Initializing Turret Assembly recipes done in %d ms. Found %d recipes.", timeDelta, registry.getRecipes().size()));
    }

    private static void loadJsonRecipes(ModContainer mod, final IAssemblyManager registry) {
        final String modId = mod.getModId();

        MiscUtils.findFiles(mod, "assets/" + modId + '/' + modId + "/recipes/assembly/", null, (root, file) -> processJson(modId, file, registry));
    }

    private static boolean processJson(final String modId, Path file, final IAssemblyManager registry) {
        if( !file.toString().endsWith(".json") ) {
            return true;
        }

        try( BufferedReader reader = Files.newBufferedReader(file) ) {
            JsonObject json = JsonUtils.fromJson(reader, JsonObject.class);

            if( json == null ) {
                throw new JsonSyntaxException("Cannot read valid JSON");
            }

            String type = JsonUtils.getStringVal(json.get("type"));
            if( type.equals(modId + ":assembly.tabs") ) {
                for( JsonElement tab : json.getAsJsonArray("tabs") ) {
                    if( tab.isJsonObject() ) {
                        JsonObject tabObj = tab.getAsJsonObject();
                        registry.setGroupIcon(JsonUtils.getStringVal(tabObj.get("name")), JsonUtils.getItemStack(tabObj.get("icon")));
                    } else {
                        throw new JsonSyntaxException("A group definition needs to be an object");
                    }
                }
            } else if( type.equals(modId + ":assembly.recipe") ) {
                NonNullList<Ingredient> ingredients = NonNullList.create();
                JsonContext context = new JsonContext(modId);
                for( JsonElement jobj : json.getAsJsonArray("ingredients") ) {
                    if( jobj.isJsonObject() ) {
                        ingredients.add(AssemblyIngredient.fromJson(jobj, context));
                    } else {
                        throw new JsonSyntaxException("An ingredient needs to be an object");
                    }
                }

                registry.registerRecipe(new AssemblyRecipe(new ResourceLocation(modId, file.getFileName().toString()),
                                                           JsonUtils.getStringVal(json.get("group")),
                                                           ingredients,
                                                           JsonUtils.getIntVal(json.get("fluxPerTick")),
                                                           JsonUtils.getIntVal(json.get("ticksProcessing")),
                                                           JsonUtils.getItemStack(json.get("result"))));
            }
        } catch( JsonParseException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Malformed recipe JSON from %s", file), e);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe from %s", file), e);
        }

        return true;
    }
}
