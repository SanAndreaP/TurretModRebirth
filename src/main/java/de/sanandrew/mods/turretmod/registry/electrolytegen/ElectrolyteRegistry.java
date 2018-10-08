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
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ElectrolyteRegistry
{
    private static final Map<ItemStack, Fuel> FUELS_INTRN = new HashMap<>();
    private static final Map<ItemStack, Fuel> FUELS_UNMODIFY = Collections.unmodifiableMap(FUELS_INTRN);
    private static final Fuel NULL_FUEL = new Fuel(-1, -1, ItemStackUtils.getEmpty(), ItemStackUtils.getEmpty());

    public static void initialize() {
        TmrConstants.LOG.log(Level.INFO, "Initializing Electrolyte Generator recipes...");
        long prevTime = System.nanoTime();
        Loader.instance().getActiveModList().forEach(ElectrolyteRegistry::loadJsonRecipes);
        long timeDelta = (System.nanoTime() - prevTime) / 1_000_000;
        TmrConstants.LOG.log(Level.INFO, String.format("Initializing Electrolyte Generator recipes done in %d ms. Found %d recipes.", timeDelta, ElectrolyteRegistry.getFuelMap().size()));
    }

    private static boolean loadJsonRecipes(ModContainer mod) {
        return MiscUtils.findFiles(mod, "assets/" + mod.getModId() + "/recipes_sapturretmod/electrolytegen/", null, ElectrolyteRegistry::processJson);
    }

    private static boolean processJson(Path root, Path file) {
        if( !"json".equals(FilenameUtils.getExtension(file.toString())) || root.relativize(file).toString().startsWith("_") ) {
            return true;
        }

        try( BufferedReader reader = Files.newBufferedReader(file) ) {
            JsonObject json = JsonUtils.fromJson(reader, JsonObject.class);

            if( json == null || json.isJsonNull() ) {
                throw new JsonSyntaxException("Json cannot be null");
            }

            NonNullList<ItemStack> inputItems = JsonUtils.getItemStacks(json.get("electrolytes"));
            float effectiveness = JsonUtils.getFloatVal(json.get("effectiveness"));
            int ticksProcessing = JsonUtils.getIntVal(json.get("timeProcessing"));
            ItemStack trash = ItemStackUtils.getEmpty();
            ItemStack treasure = ItemStackUtils.getEmpty();

            JsonElement elem = json.get("trash");
            if( elem != null && !elem.isJsonNull() ) {
                trash = JsonUtils.getItemStack(elem);
            }
            elem = json.get("treasure");
            if( elem != null && !elem.isJsonNull() ) {
                treasure = JsonUtils.getItemStack(elem);
            }

            registerFuels(inputItems, effectiveness, ticksProcessing, trash, treasure);
        } catch( JsonParseException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Parsing error loading electrolyte generator recipe from %s", file), e);
            return false;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe from %s", file), e);
            return false;
        }

        return true;
    }

    private static Map<ItemStack, Fuel> getFuelMap() {
        return FUELS_UNMODIFY;
    }

    @Nonnull
    public static Fuel getFuel(ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            return NULL_FUEL;
        }

        for( Map.Entry<ItemStack, Fuel> entry : FUELS_INTRN.entrySet() ) {
            ItemStack key = entry.getKey();
            if( ItemStackUtils.areEqual(key, stack, key.hasTagCompound(), false, key.getItemDamage() == OreDictionary.WILDCARD_VALUE) ) {
                return entry.getValue();
            }
        }

        return NULL_FUEL;
    }

    public static boolean isFuel(ItemStack stack) {
        return getFuel(stack).isValid();
    }

    private static boolean registerFuels(NonNullList<ItemStack> electrolytes, float effectiveness, int ticksProcessing, @Nonnull ItemStack trash, @Nonnull ItemStack treasure) {
        if( effectiveness < 1.0F ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot have an effectiveness of less than 1.0");
            return false;
        }

        if( ticksProcessing < 1 ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot have a time less than 1 tick");
            return false;
        }

        if( electrolytes.stream().anyMatch(item -> getFuel(item).isValid()) ) {
            TmrConstants.LOG.log(Level.ERROR, "Electrolyte item is already registered");
            return false;
        }

        Fuel fuelInst = new Fuel(effectiveness, ticksProcessing, trash, treasure);
        electrolytes.forEach(item -> FUELS_INTRN.put(item, fuelInst));

        return true;
    }

    public static boolean removeFuel(ItemStack stack) {
        for( Iterator<Map.Entry<ItemStack, Fuel>> it = FUELS_INTRN.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<ItemStack, Fuel> entry = it.next();
            ItemStack key = entry.getKey();
            if( ItemStackUtils.areEqual(key, stack, key.hasTagCompound(), false, key.getItemDamage() == OreDictionary.WILDCARD_VALUE) ) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    public static final class Fuel
    {
        final float effect;
        final short ticksProc;
        @Nonnull
        final ItemStack trash;
        @Nonnull
        final ItemStack treasure;

        public Fuel(float effectiveness, int ticksProcessing, @Nonnull ItemStack trash, @Nonnull ItemStack treasure) {
            this.effect = effectiveness;
            this.ticksProc = (short) ticksProcessing;
            this.trash = trash;
            this.treasure = treasure;
        }

        public boolean isValid() {
            return this.effect >= 1.0F && this.ticksProc >= 1;
        }
    }
}
