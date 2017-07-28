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
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ElectrolyteHelper
{
    private static final Map<ItemStack, Fuel> FUELS_INTRN = new HashMap<>();
    private static final Map<ItemStack, Fuel> FUELS_UNMODIFY = Collections.unmodifiableMap(FUELS_INTRN);
    public static final Fuel NULL_FUEL = new Fuel(-1, -1, ItemStack.EMPTY, ItemStack.EMPTY);

    public static void initialize() {
        Loader.instance().getActiveModList().forEach(ElectrolyteHelper::loadRecipes);
    }

    private static boolean loadRecipes(ModContainer mod) {
        return TmrUtils.findFiles(mod, "assets/" + mod.getModId() + "/recipes/sapturretmod/electrolytegen/", null,
                (root, file) -> {
                    if( !"json".equals(FilenameUtils.getExtension(file.toString())) || root.relativize(file).toString().startsWith("_") ) {
                        return true;
                    }

                    try( BufferedReader reader = Files.newBufferedReader(file) ) {
                        JsonObject json = JsonUtils.fromJson(TmrUtils.GSON, reader, JsonObject.class);

                        if( json == null || json.isJsonNull() ) {
                            throw new JsonSyntaxException("Json cannot be null");
                        }

                        NonNullList<ItemStack> inputItems = TmrUtils.getItemStacks(json.get("electrolytes"), true);
                        float effectiveness = TmrUtils.getFloatVal(json.get("effectiveness"));
                        if( effectiveness < 1.0F ) {
                            throw new JsonParseException("Cannot have an effectiveness of less than 1.0");
                        }

                        int ticksProcessing = TmrUtils.getIntVal(json.get("timeProcessing"));
                        if( ticksProcessing < 1 ) {
                            throw new JsonParseException("Cannot have a time less than 1 tick");
                        }

                        ItemStack trash = TmrUtils.getItemStacks(json.get("trash"), false).get(0);

                        ItemStack treasure = ItemStack.EMPTY;

                        JsonElement treasureElem = json.get("treasure");
                        if( treasureElem != null && !treasureElem.isJsonNull() ) {
                            treasure = TmrUtils.getItemStacks(treasureElem, false).get(0);
                        }

                        Fuel fuelInst = new Fuel(effectiveness, ticksProcessing, trash, treasure);
                        inputItems.forEach(item -> FUELS_INTRN.put(item, fuelInst));
                    } catch( JsonParseException e ) {
                        TmrConstants.LOG.log(Level.ERROR, String.format("Parsing error loading electrolyte generator recipe from %s", file), e);
                        return false;
                    } catch( IOException e ) {
                        TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe from %s", file), e);
                        return false;
                    }

                    return true;
                }
        );
    }

    public static Map<ItemStack, Fuel> getFuelMap() {
        return FUELS_UNMODIFY;
    }

    @Nonnull
    public static Fuel getFuel(ItemStack stack) {
        for( Map.Entry<ItemStack, Fuel> entry : FUELS_INTRN.entrySet() ) {
            ItemStack key = entry.getKey();
            if( ItemStackUtils.areEqual(key, stack, key.hasTagCompound(), false, key.getItemDamage() == OreDictionary.WILDCARD_VALUE) ) {
                return entry.getValue();
            }
        }

        return NULL_FUEL;
    }

    public static final class Fuel
    {
        public final float effect;
        public final short ticksProc;
        @Nonnull
        public final ItemStack trash;
        @Nonnull
        public final ItemStack treasure;

        public Fuel(float effectiveness, int ticksProcessing, @Nonnull ItemStack trash, @Nonnull ItemStack treasure) {
            this.effect = effectiveness;
            this.ticksProc = (short) ticksProcessing;
            this.trash = trash;
            this.treasure = treasure;
        }

        public boolean isNull() {
            return this.effect < 1.0F || this.ticksProc < 1;
        }
    }
}
