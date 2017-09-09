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
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
import java.util.UUID;

public final class TurretAssemblyRecipes
{
    public static void initialize(final ITurretAssemblyRegistry registry) {
        TmrConstants.LOG.log(Level.INFO, "Initializing Turret Assembly recipes...");
        long prevTime = System.nanoTime();
        Loader.instance().getActiveModList().forEach(mod -> loadJsonRecipes(mod, registry));
        long timeDelta = (System.nanoTime() - prevTime) / 1_000_000;
        TmrConstants.LOG.log(Level.INFO, String.format("Initializing Turret Assembly recipes done in %d ms. Found %d recipes.", timeDelta, registry.getRecipeList().size()));
    }

    private static boolean loadJsonRecipes(ModContainer mod, final ITurretAssemblyRegistry registry) {
        return MiscUtils.findFiles(mod, "assets/" + mod.getModId() + "/recipes_sapturretmod/assembly/",
                                   root -> preProcessJson(root, registry),
                                   (root, file) -> file.getFileName().startsWith("group_") || processJson(file, json -> registerJsonRecipes(json, registry)));
    }

    private static boolean preProcessJson(Path root, final ITurretAssemblyRegistry registry) {
        if( Files.exists(root) ) {
            try {
                Files.find(root, Integer.MAX_VALUE, (filePth, attr) -> filePth.getFileName().toString().startsWith("group_"))
                     .forEach(file -> processJson(file, json -> registerJsonGroup(json, registry)));
            } catch( IOException ex ) {
                TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe group from directory %s", root), ex);
                return false;
            }
        }

        return true;
    }

    private static boolean processJson(Path file, Ex2Function<JsonObject, Boolean, JsonParseException, IOException> callback) {
        if( !"json".equals(FilenameUtils.getExtension(file.toString())) || file.getFileName().toString().startsWith("_") ) {
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
            TmrConstants.LOG.log(Level.ERROR, String.format("Parsing error loading electrolyte generator recipe from %s", file), e);
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
    private static boolean registerJsonRecipes(JsonObject json, final ITurretAssemblyRegistry registry) throws JsonParseException, IOException {
        String id = JsonUtils.getStringVal(json.get("id"));
        String group = JsonUtils.getStringVal(json.get("group"));
        int fluxPerTick = JsonUtils.getIntVal(json.get("fluxPerTick"));
        int ticksProcessing = JsonUtils.getIntVal(json.get("ticksProcessing"));
        ItemStack result = JsonUtils.getItemStack(json.get("result"));

        JsonElement ingredients = json.get("ingredients");
        if( !ingredients.isJsonArray() ) {
            throw new JsonSyntaxException("Ingredients must be an array");
        }
        List<IRecipeEntry> entries = new ArrayList<>();
        ingredients.getAsJsonArray().forEach(elem -> {
            if( elem != null && elem.isJsonObject() ) {
                JsonObject elemObj = elem.getAsJsonObject();
                int count = JsonUtils.getIntVal(elemObj.get("count"));
                boolean showTooltipText = JsonUtils.getBoolVal(elemObj.get("showTooltipText"), false);
                NonNullList<ItemStack> items = JsonUtils.getItemStacks(elemObj.get("items"));

                int sz = items.size();
                if( sz > 0 ) {
                    IRecipeEntry entry = new RecipeEntry(count).put(items.toArray(new ItemStack[sz]));
                    if( showTooltipText ) {
                        entry.drawTooltip();
                    }

                    entries.add(entry);
                }
            }
        });

        return registry.registerRecipe(UUID.fromString(id), registry.getGroup(group), result, fluxPerTick, ticksProcessing,
                                       entries.toArray(new IRecipeEntry[entries.size()]));
    }
}
