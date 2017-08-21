/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod;

import com.google.common.collect.ImmutableList;
import com.google.gson.stream.JsonWriter;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntryItem;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RecipeDump
        implements ITurretAssemblyRegistry
{
    @Override
    @SuppressWarnings("resource")
    public boolean registerRecipe(UUID uuid, IRecipeGroup group, @Nonnull ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeEntry... resources) {
        try( FileWriter w = new FileWriter("C:\\Temp\\mc\\" + uuid.toString() + ".json"); JsonWriter jw = new JsonWriter(w) ) {
            jw.setIndent("  ");
            jw.beginObject();
            jw.name("id").value(uuid.toString());
            jw.name("group").value(group.getName());
            jw.name("fluxPerTick").value(fluxPerTick);
            jw.name("ticksProcessing").value(ticksProcessing);
            jw.name("ingredients").beginArray();
            for( IRecipeEntry entry : resources ) {
                RecipeEntryItem entryCst = (RecipeEntryItem) entry;
                jw.beginObject();
                jw.name("count").value(entry.getItemCount());
                if( entry.shouldDrawTooltip() ) {
                    jw.name("showTooltipText").value(true);
                }
                jw.name("items").beginArray();
                for( ItemStack itm : entryCst.normalAlternatives ) {
                    jw.beginObject();
                    jw.name("item").value(itm.getItem().getRegistryName().toString());
                    if( itm.getItemDamage() > 0 ) {
                        jw.name("data").value(itm.getItemDamage());
                    }
                    if( itm.hasTagCompound() ) {
                        jw.name("nbt");
                        writeNbtToJson(jw, itm.getTagCompound());
                    }
                    jw.endObject();
                }
                for( String ore : entryCst.oreDictAlternatives ) {
                    jw.beginObject();
                    jw.name("type").value("forge:ore_dict");
                    jw.name("ore").value(ore);
                    jw.endObject();
                }
                jw.endArray();
                jw.endObject();
            }
            jw.endArray();
            jw.name("result").beginObject();
            jw.name("item").value(result.getItem().getRegistryName().toString());
            jw.name("count").value(result.getCount());
            if( result.getItemDamage() > 0 ) {
                jw.name("data").value(result.getItemDamage());
            }
            if( result.hasTagCompound() ) {
                jw.name("nbt");
                writeNbtToJson(jw, result.getTagCompound());
            }
            jw.endObject();
            jw.endObject();
        } catch( IOException | NullPointerException ignored ) { }

        return true;
    }

    @Override
    @SuppressWarnings("resource")
    public IRecipeGroup registerGroup(String name, @Nonnull ItemStack stack) {
        try( FileWriter w = new FileWriter("C:\\Temp\\mc\\" + name + ".json"); JsonWriter jw = new JsonWriter(w) ) {
            jw.setIndent("  ");
            jw.beginObject();
            jw.name("name").value(name);
            jw.name("item").beginObject();
            jw.name("item").value(stack.getItem().getRegistryName().toString());
            if( stack.hasTagCompound() ) {
                jw.name("nbt");
                writeNbtToJson(jw, stack.getTagCompound());
            }
            jw.endObject();
            jw.endObject();
        } catch( IOException | NullPointerException ignored ) { }

        return new TurretAssemblyRegistry.RecipeGroup(name, stack);
    }

    @SuppressWarnings("resource")
    private void writeNbtToJson(JsonWriter jw, NBTTagCompound nbt) throws IOException {
        jw.beginObject();
        nbt.getKeySet().forEach(key -> {
            try {
                jw.name(key);
                writeNbtTagToJson(jw, nbt.getTag(key));
            } catch(IOException ignored) {}
        });
        jw.endObject();
    }

    @SuppressWarnings("resource")
    private void writeNbtTagToJson(JsonWriter jw, NBTBase val) throws IOException {
        switch( val.getId() ) {
            case Constants.NBT.TAG_BYTE:
            case Constants.NBT.TAG_SHORT:
            case Constants.NBT.TAG_INT:
            case Constants.NBT.TAG_LONG:
                jw.value(((NBTPrimitive) val).getLong());
                break;
            case Constants.NBT.TAG_FLOAT:
            case Constants.NBT.TAG_DOUBLE:
            case Constants.NBT.TAG_ANY_NUMERIC:
                jw.value(((NBTPrimitive) val).getDouble());
                break;
            case Constants.NBT.TAG_BYTE_ARRAY:
                jw.beginArray();
                for( byte b : ((NBTTagByteArray) val).getByteArray() ) {
                    jw.value(b);
                }
                jw.endArray();
                break;
            case Constants.NBT.TAG_STRING:
                jw.value(((NBTTagString) val).getString());
                break;
            case Constants.NBT.TAG_LIST:
                NBTTagList lst = (NBTTagList) val;
                jw.beginArray();
                for( int i = 0, max = lst.tagCount(); i < max; i++ ) {
                    writeNbtTagToJson(jw, lst.get(i));
                }
                jw.endArray();
                break;
            case Constants.NBT.TAG_COMPOUND:
                writeNbtToJson(jw, (NBTTagCompound) val);
                break;
            case Constants.NBT.TAG_INT_ARRAY:
                jw.beginArray();
                for( int b : ((NBTTagIntArray) val).getIntArray() ) {
                    jw.value(b);
                }
                jw.endArray();
                break;
            case 12: //TAG_LONG_ARRAY
//                jw.beginArray();
//                for( long b : ((NBTTagLongArray) val). ) {
//                    jw.value(b);
//                }
//                jw.endArray();
                break;
        }
    }

    @Override
    public IRecipeGroup getGroup(String name) {
        return null;
    }

    @Override
    public List<TurretAssemblyRegistry.RecipeKeyEntry> getRecipeList() {
        return ImmutableList.of();
    }
}
