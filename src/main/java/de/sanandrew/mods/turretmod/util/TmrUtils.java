/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TmrUtils
        implements ITmrUtils
{
    public static final TmrUtils INSTANCE = new TmrUtils();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public ITargetProcessor getNewTargetProcInstance(EntityTurret turret) {
        return new TargetProcessor(turret);
    }

    @Override
    public IUpgradeProcessor getNewUpgradeProcInstance(EntityTurret turret) {
        return new UpgradeProcessor(turret);
    }

    @Override
    public boolean isTCUItem(@Nonnull ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit);
    }

    @Override
    public void onTurretDeath(EntityTurret turret) {
        ((TargetProcessor) turret.getTargetProcessor()).dropAmmo();
        ((UpgradeProcessor) turret.getUpgradeProcessor()).dropUpgrades();
    }

    @Override
    public void updateTurretState(EntityTurret turret) {
        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(turret), turret.dimension, turret.posX, turret.posY, turret.posZ, 64.0D);
    }

    @Override
    @Nonnull
    public ItemStack getPickedTurretResult(RayTraceResult target, EntityTurret turret) {
        return ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(turret.getClass()));
    }

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        TurretModRebirth.proxy.openGui(player, id, x, y, z);
    }

    @Override
    public boolean canPlayerEditAll() {
        return TmrConfiguration.playerCanEditAll;
    }

    @Override
    public boolean canOpEditAll() {
        return TmrConfiguration.opCanEditAll;
    }

    @Override
    public <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return EntityUtils.getPassengersOfClass(e, psgClass);
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return ItemStackUtils.isValid(stack);
    }

    public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor) {
        File source = mod.getSource();

        if( source.isFile() ) {
            try( FileSystem fs = FileSystems.newFileSystem(source.toPath(), null) ) {
                return findFilesIntrn(fs.getPath('/' + base), mod, preprocessor, processor);
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, "Error loading FileSystem from jar: ", e);
                return false;
            }
        } else if( source.isDirectory() ) {
            return findFilesIntrn(source.toPath().resolve(base), mod, preprocessor, processor);
        }

        return false;
    }

    private static boolean findFilesIntrn(Path root, ModContainer mod, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor) {
        if( root == null || !Files.exists(root) ) {
            return false;
        }

        if( preprocessor != null && !MiscUtils.defIfNull(preprocessor.apply(root), false) ) {
            return false;
        }

        if( processor != null ) {
            Iterator<Path> itr;
            try {
                itr = Files.walk(root).iterator();
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, String.format("Error iterating filesystem for: %s", mod.getModId()), e);
                return false;
            }

            while( itr != null && itr.hasNext() ) {
                if( !MiscUtils.defIfNull(processor.apply(root, itr.next()), false) ) {
                    return false;
                }
            }
        }

        return true;
    }

    public static float getFloatVal(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be a floating point number");
        }

        return json.getAsFloat();
    }

    public static int getIntVal(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be an Integer");
        }

        return json.getAsInt();
    }

    public static NonNullList<ItemStack> getItemStacks(JsonElement json, boolean allowMultiple) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( json.isJsonArray() ) {
            if( allowMultiple ) {
                NonNullList<ItemStack> stacks = NonNullList.create();

                json.getAsJsonArray().forEach(elem -> stacks.addAll(getItemStacks(elem, true)));

                return stacks;
            } else {
                throw new JsonSyntaxException("Expected stack to be an object, not an array");
            }
        }

        if( !json.isJsonObject() ) {
            throw new JsonSyntaxException("Expcted stack to be an object or array of objects");
        }

        JsonObject jsonObj = (JsonObject) json;
        String itemName = JsonUtils.getString(jsonObj, "item");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if( item == null ) {
            throw new JsonParseException(String.format("Unknown item '%s'", itemName));
        }

        if( item.getHasSubtypes() && !jsonObj.has("data") ) {
            throw new JsonParseException(String.format("Missing data for item '%s'", itemName));
        }

        ItemStack stack;
        if( jsonObj.has("nbt") ) {
            try {
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(GSON.toJson(jsonObj.get("nbt")));
                NBTTagCompound tmp = new NBTTagCompound();
                if( nbt.hasKey("ForgeCaps") ) {
                    tmp.setTag("ForgeCaps", nbt.getTag("ForgeCaps"));
                    nbt.removeTag("ForgeCaps");
                }

                tmp.setTag("tag", nbt);
                tmp.setString("id", itemName);
                tmp.setInteger("Count", JsonUtils.getInt(jsonObj, "count", 1));
                tmp.setInteger("Damage", JsonUtils.getInt(jsonObj, "data", 0));

                stack = new ItemStack(tmp);
            } catch( NBTException e ) {
                throw new JsonParseException("Invalid NBT Entry: " + e.toString());
            }
        } else {
            stack = new ItemStack(item, JsonUtils.getInt(jsonObj, "count", 1), JsonUtils.getInt(jsonObj, "data", 0));
        }

        if( !ItemStackUtils.isValid(stack) ) {
            throw new JsonParseException("Invalid Item: " + stack.toString());
        }

        return NonNullList.from(ItemStack.EMPTY, stack);
    }
}
