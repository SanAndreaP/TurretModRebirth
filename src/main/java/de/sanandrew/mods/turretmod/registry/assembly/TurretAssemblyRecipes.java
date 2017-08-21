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

@SuppressWarnings("unused")
public final class TurretAssemblyRecipes
{
    public static final UUID TURRET_MK1_CB    = UUID.fromString("21F88959-C157-44E3-815B-DD956B065052");
    public static final UUID TURRET_MK1_SG    = UUID.fromString("870EA4DD-0C1E-44B1-BE91-4DD33FC00EF8");
    public static final UUID TURRET_MK1_CL    = UUID.fromString("6743974B-5552-45F7-9124-FDCF844BB56C");
    public static final UUID TURRET_MK2_RV    = UUID.fromString("1A207F83-26E1-405A-A9A1-4AB6BB1C4C3A");
    public static final UUID TURRET_MK2_MG    = UUID.fromString("7D21F126-56B5-44DB-A511-CFFADC0782F0");
    public static final UUID TURRET_MK3_LR    = UUID.fromString("94676B1E-8279-490C-A3F6-10983566FE3A");
    public static final UUID TURRET_MK3_FT    = UUID.fromString("AFC33ABF-B973-4A91-A86E-24D0A78E58B2");
    public static final UUID ARROW_SNG        = UUID.fromString("1A011825-2E5B-4F17-925E-F734E6A732B9");
    public static final UUID ARROW_MTP        = UUID.fromString("C079D29A-E6E2-4BE8-8478-326BDFEDE08B");
    public static final UUID SGSHELL_SNG      = UUID.fromString("AB37D601-993D-41FE-B698-8AAC99D296EA");
    public static final UUID SGSHELL_MTP      = UUID.fromString("D17EC4A1-BDAA-4C80-B1F5-0C111EC13954");
    public static final UUID BULLET_SNG       = UUID.fromString("9F528407-8134-49CB-8FA8-23CF88E8CE4A");
    public static final UUID BULLET_MTP       = UUID.fromString("2933D4D6-6111-45E5-AD09-09D81CF03DA9");
    public static final UUID CRYOCELL_1_SNG   = UUID.fromString("EBF1AEAA-C4EC-46CA-9B0F-B818FF7D0770");
    public static final UUID CRYOCELL_1_MTP   = UUID.fromString("5CBDE28A-52B2-45EB-B169-4A81F94EC690");
    public static final UUID CRYOCELL_2_SNG   = UUID.fromString("BACB5D75-B408-4D34-AF6C-2F6C4048B82C");
    public static final UUID CRYOCELL_2_MTP   = UUID.fromString("08528F4F-3D3E-4501-A1BE-A508E5C23DC5");
    public static final UUID CRYOCELL_3_SNG   = UUID.fromString("081CA2A5-FB0C-4749-9359-12D680B58FAC");
    public static final UUID CRYOCELL_3_MTP   = UUID.fromString("052D31C1-05AD-45AF-9C36-D380A78F7E87");
    public static final UUID MGSHELL_SNG      = UUID.fromString("EA5B683F-7D84-4BAE-BFC3-35F2EA48AB2B");
    public static final UUID MGSHELL_MTP      = UUID.fromString("C69B50D8-EB88-4CFC-BF9E-792C75924C22");
    public static final UUID FLUXCELL_SNG     = UUID.fromString("78BA8E56-B161-49A0-8053-710083A39133");
    public static final UUID FLUXCELL_MTP     = UUID.fromString("8B4E5B02-A833-49BF-9E7A-3DF5676E3218");
    public static final UUID FUELTANK_SNG     = UUID.fromString("3F1B7AE0-CEDD-46D0-B0F0-289446560F21");
    public static final UUID FUELTANK_MTP     = UUID.fromString("85F480FD-299B-45CE-9796-C3ECC8EF5868");
    public static final UUID TCU              = UUID.fromString("47B68BE0-30D6-4849-B995-74C147C8CC5D");
    public static final UUID TINFO            = UUID.fromString("5A8C8AE3-878A-4580-9F84-2C8602B4275D");
    public static final UUID HEAL_MK1         = UUID.fromString("816758D6-7F00-4ACB-BD94-F7A8A0F86016");
    public static final UUID HEAL_MK2         = UUID.fromString("39A1A9C8-CECA-40CA-BCF7-ABD2B1A26C82");
    public static final UUID HEAL_MK3         = UUID.fromString("A70314BE-1709-4AE4-8FF7-69F3A69ACCA2");
    public static final UUID HEAL_MK4         = UUID.fromString("6FD0927F-61E0-49A0-B615-4B3E28A63EE4");
    public static final UUID REGEN_MK1        = UUID.fromString("531F0B05-5BB8-45FC-A899-226A3F52D5B7");
    public static final UUID UPG_EMPTY        = UUID.fromString("BC775E0D-7732-4E4E-8FA3-33B299CAF19D");
    public static final UUID UPG_HEALTH_MK1   = UUID.fromString("EF5192F1-0422-444D-B2D3-98540D962AE9");
    public static final UUID UPG_HEALTH_MK2   = UUID.fromString("185AB41E-BD30-47C8-BD42-A9D5C8749ECF");
    public static final UUID UPG_HEALTH_MK3   = UUID.fromString("AF3E8F87-DD88-4665-B3A9-314D4077CD00");
    public static final UUID UPG_HEALTH_MK4   = UUID.fromString("E14C4925-0C35-44CE-9626-490A7774A9FD");
    public static final UUID UPG_STORAGE_1    = UUID.fromString("FAF60679-5BC6-4B82-BB34-323988B56FFA");
    public static final UUID UPG_STORAGE_2    = UUID.fromString("2F90D6BC-0869-45DE-9527-396CCE547ECE");
    public static final UUID UPG_STORAGE_3    = UUID.fromString("9B7BA1F9-286E-43E4-8147-891DA0C243DC");
    public static final UUID UPG_RELOAD_1     = UUID.fromString("A891752D-AA2E-40D1-8E22-50DF0AF43490");
    public static final UUID UPG_RELOAD_2     = UUID.fromString("72BDED08-78DC-4A25-9460-6F5B8AEEE3A5");
    public static final UUID UPG_AMMO_STG     = UUID.fromString("56546F99-5612-4052-9A77-B81A6F1EB5DF");
    public static final UUID UPG_SMART_TGT    = UUID.fromString("A4750C8C-A0A0-4E73-8378-59345124A1FA");
    public static final UUID UPG_ECONOMY_I    = UUID.fromString("D8144A93-870F-4FBC-88F3-94CF8212EF84");
    public static final UUID UPG_ECONOMY_II   = UUID.fromString("1BDE1C44-9165-4BBC-A79D-2003CCE969D9");
    public static final UUID UPG_ECONOMY_INF  = UUID.fromString("7C5E0A1F-1BC3-4F72-A4A0-BB28A595CA0D");
    public static final UUID UPG_ENDER_MEDIUM = UUID.fromString("C6D6FA9C-9B3A-4DDD-B0D8-92CF5C2555F8");
    public static final UUID UPG_FUEL_PURIFY  = UUID.fromString("042E0949-E137-4646-A227-0620D2FB6A4D");
    public static final UUID UPG_AT_AUTO      = UUID.fromString("40EEE46D-835D-42F8-8005-764A00C90365");
    public static final UUID UPG_AT_FILTER    = UUID.fromString("BD48EB98-94A2-4516-90E0-4DC20E843490");
    public static final UUID UPG_AT_SPEED     = UUID.fromString("DF388B34-64ED-4D94-BEE0-C1A4AAB8E701");

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
                                   (root, file) -> root.relativize(file).toString().startsWith("group_") || processJson(root, file, json -> registerJsonRecipes(json, registry)));
    }

    private static boolean preProcessJson(Path root, final ITurretAssemblyRegistry registry) {
        if( Files.exists(root) ) {
            try {
                Files.find(root, Integer.MAX_VALUE, (filePth, attr) -> root.relativize(filePth).toString().startsWith("group_"))
                     .forEach(file -> processJson(root, file, json -> registerJsonGroup(json, registry)));
            } catch( IOException ex ) {
                TmrConstants.LOG.log(Level.ERROR, String.format("Couldn't read recipe group from irectory %s", root), ex);
                return false;
            }
        }

        return true;
    }

    private static boolean processJson(Path root, Path file, Ex2Function<JsonObject, Boolean, JsonParseException, IOException> callback) {
        if( !"json".equals(FilenameUtils.getExtension(file.toString())) || root.relativize(file).toString().startsWith("_") ) {
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

    private static boolean registerJsonGroup(JsonObject json, final ITurretAssemblyRegistry registry) throws JsonParseException, IOException {
        String groupName = JsonUtils.getStringVal(json.get("name"));
        ItemStack groupIcon = JsonUtils.getItemStack(json.get("item"));

        registry.registerGroup(groupName, groupIcon);

        return true;
    }

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
                    IRecipeEntry entry = new RecipeEntryItem(count).put(items.toArray(new ItemStack[sz]));
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
