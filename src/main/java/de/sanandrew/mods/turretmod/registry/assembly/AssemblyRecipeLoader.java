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
import com.google.gson.stream.JsonReader;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AssemblyRecipeLoader
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

        MiscUtils.findFiles(mod, "assets/" + modId + '/' + TmrConstants.ID + "/recipes/assembly/", null, (root, file) -> processJson(modId, file, registry));
    }

    private static boolean processJson(final String modId, Path file, final IAssemblyManager registry) {
        if( !file.toString().endsWith(".json") ) {
            return true;
        }

        try( BufferedReader reader = Files.newBufferedReader(file) ) {
            JsonObject json = fromJson(reader, JsonObject.class);
            ResourceLocation id = new ResourceLocation(modId, "recipes/assembly/" + file.getFileName().toString());

            if( json == null ) {
                throw new JsonSyntaxException("Cannot read valid JSON");
            } else if( json.isJsonNull() ) {
                registry.removeRecipe(id);
                return true;
            }

            String type = JsonUtils.getStringVal(json.get("type"));
            if( type.equals(TmrConstants.ID + ":assembly.tabs") ) {
                for( JsonElement tab : json.getAsJsonArray("tabs") ) {
                    if( tab.isJsonObject() ) {
                        JsonObject tabObj = tab.getAsJsonObject();
                        registry.setGroupIcon(JsonUtils.getStringVal(tabObj.get("name")), JsonUtils.getItemStack(tabObj.get("icon")));
                    } else {
                        throw new JsonSyntaxException("A group definition needs to be an object");
                    }
                }
            } else if( type.equals(TmrConstants.ID + ":assembly.recipe") ) {
                NonNullList<Ingredient> ingredients = NonNullList.create();
                JsonContext context = new JsonContext(modId);
                for( JsonElement jobj : json.getAsJsonArray("ingredients") ) {
                    if( jobj.isJsonObject() ) {
                        ingredients.add(AssemblyIngredient.fromJson(jobj, context));
                    } else {
                        throw new JsonSyntaxException("An ingredient needs to be an object");
                    }
                }

                String cstRecipeClassPath = JsonUtils.getStringVal(json.get("recipeClass"), null);
                if( cstRecipeClassPath != null ) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends AssemblyRecipe> cls = (Class<? extends AssemblyRecipe>) Class.forName(cstRecipeClassPath);

                        registry.registerRecipe(cls.getConstructor(ResourceLocation.class,
                                                                   String.class,
                                                                   NonNullList.class,
                                                                   int.class,
                                                                   int.class,
                                                                   ItemStack.class
                        ).newInstance(id,
                                      JsonUtils.getStringVal(json.get("group")),
                                      ingredients,
                                      JsonUtils.getIntVal(json.get("fluxPerTick")),
                                      JsonUtils.getIntVal(json.get("ticksProcessing")),
                                      JsonUtils.getItemStack(json.get("result"))));
                        return true;
                    } catch( ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException
                             | IllegalAccessException | InvocationTargetException e )
                    {
                        TmrConstants.LOG.log(Level.ERROR, "Cannot load recipe class, defaulting to AssemblyRecipe.", e);
                    }
                }

                registry.registerRecipe(new AssemblyRecipe(id,
                                                           JsonUtils.getStringVal(json.get("group")),
                                                           ingredients,
                                                           JsonUtils.getIntVal(json.get("fluxPerTick")),
                                                           JsonUtils.getIntVal(json.get("ticksProcessing")),
                                                           JsonUtils.getItemStack(json.get("result"))));
            }
        } catch( JsonParseException e ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Malformed recipe JSON from %s", file), e);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.WARN, String.format("Couldn't read recipe from %s", file), e);
        }

        return true;
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);
        try {
            return JsonUtils.GSON.getAdapter(clazz).read(jsonReader);
        } catch( IOException e ) {
            throw new JsonParseException(e);
        }
    }
}
