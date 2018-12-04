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
import de.sanandrew.mods.sanlib.lib.function.Ex2Function;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeItem;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class TurretAssemblyRecipes
{
    public static void initialize(final ITurretAssemblyRegistry registry) {
        TmrConstants.LOG.log(Level.INFO, "Initializing Turret Assembly recipes...");
        long prevTime = System.nanoTime();
        Loader.instance().getActiveModList().forEach(mod -> loadJsonRecipes(mod, registry));
        long timeDelta = (System.nanoTime() - prevTime) / 1_000_000;
        TmrConstants.LOG.log(Level.INFO, String.format("Initializing Turret Assembly recipes done in %d ms. Found %d recipes.", timeDelta, registry.getRecipeList().size()));
    }

    private static void loadJsonRecipes(ModContainer mod, final ITurretAssemblyRegistry registry) {
        final String modId = mod.getModId();

        MiscUtils.findFiles(mod, "assets/" + modId + "/recipes.sapturretmod/assembly/",
                            root -> preProcessJson(root, registry),
                            (root, file) -> processRecipeJson(modId, file, registry));
    }

    private static boolean preProcessJson(Path root, final ITurretAssemblyRegistry registry) {
        if( Files.exists(root) ) {
            try( Stream<Path> groups = Files.find(root, Integer.MAX_VALUE, (filePth, attr) -> FilenameUtils.getName(filePth.toString()).startsWith("group_")) ) {
                groups.forEach(file -> processJson(file, json -> registerJsonGroup(json, registry)));
            } catch( IOException ex ) {
                TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe group from directory %s", root), ex);
                return false;
            }
        }

        return true;
    }

    private static boolean processRecipeJson(final String modId, Path file, final ITurretAssemblyRegistry registry) {
        String fileName = FilenameUtils.getBaseName(file.toString());
        return fileName.startsWith("group_")
               || processJson(file, json -> registerJsonRecipes(new ResourceLocation(modId, fileName), json, registry));
    }

    private static boolean processJson(Path file, Ex2Function<JsonObject, Boolean, JsonParseException, IOException> callback) {
        if( !"json".equals(FilenameUtils.getExtension(file.toString())) || FilenameUtils.getName(file.toString()).startsWith("_") ) {
            return true;
        }

        try( BufferedReader reader = Files.newBufferedReader(file) ) {
            JsonObject json = JsonUtils.fromJson(reader, JsonObject.class);

            if( json == null || json.isJsonNull() ) {
                throw new JsonSyntaxException("Json cannot be null");
            }

            callback.apply(json);

            return true;
        } catch( JsonParseException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Parsing error loading assembly table recipe from %s", file), e);
            return false;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe from %s", file), e);
            return false;
        }
    }

    @SuppressWarnings({"SameReturnValue", "RedundantThrows"})
    private static boolean registerJsonGroup(JsonObject json, final ITurretAssemblyRegistry registry) throws JsonParseException, IOException {
        String groupName = JsonUtils.getStringVal(json.get("name"));
        ItemStack groupIcon = JsonUtils.getItemStack(json.get("item"));

        registry.registerGroup(groupName, groupIcon);

        return true;
    }

    @SuppressWarnings("RedundantThrows")
    private static boolean registerJsonRecipes(ResourceLocation id, JsonObject json, final ITurretAssemblyRegistry registry) throws JsonParseException, IOException {
        String group = JsonUtils.getStringVal(json.get("group"));
        int fluxPerTick = JsonUtils.getIntVal(json.get("fluxPerTick"));
        int ticksProcessing = JsonUtils.getIntVal(json.get("ticksProcessing"));
        ItemStack result = JsonUtils.getItemStack(json.get("result"));

        JsonElement ingredients = json.get("ingredients");
        if( !ingredients.isJsonArray() ) {
            throw new JsonSyntaxException("Ingredients must be an array");
        }
        List<IRecipeItem> entries = new ArrayList<>();
        ingredients.getAsJsonArray().forEach(elem -> {
            if( elem != null && elem.isJsonObject() ) {
                JsonObject elemObj = elem.getAsJsonObject();
                int count = JsonUtils.getIntVal(elemObj.get("count"));
                boolean showTooltipText = JsonUtils.getBoolVal(elemObj.get("showTooltipText"), false);
                NonNullList<ItemStack> items = JsonUtils.getItemStacks(elemObj.get("items"));

                int sz = items.size();
                if( sz > 0 ) {
                    IRecipeItem entry = new RecipeItem(count).put(items.toArray(new ItemStack[0]));
                    if( showTooltipText ) {
                        entry.drawTooltip();
                    }

                    entries.add(entry);
                }
            }
        });

        return registry.registerRecipe(id, registry.getGroup(group), result, fluxPerTick, ticksProcessing,
                                       entries.toArray(new IRecipeItem[0]));
    }
}
