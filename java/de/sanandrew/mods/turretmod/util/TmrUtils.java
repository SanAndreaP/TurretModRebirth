/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class TmrUtils
{
    public static final Random RNG = new Random();

    public static final int ATTR_ADD_VAL_TO_BASE = 0;
    public static final int ATTR_ADD_PERC_VAL_TO_SUM = 1;
    public static final int RISE_SUM_WITH_PERC_VAL = 2;

    public static final Comparator<NBTTagCompound> NBT_COMPARATOR_FIXD = (firstTag, secondTag) -> firstTag != null ? (firstTag.equals(secondTag) ? 0 : 1) : (secondTag != null ? -1 : 0);

    public static Entity getEntityByUUID(World worldObj, UUID uuid) {
        for( Object entity : worldObj.loadedEntityList ) {
            if( entity instanceof Entity && ((Entity) entity).getUniqueID().equals(uuid) ) {
                return (Entity) entity;
            }
        }

        return null;
    }

    public static <T> T valueOrDefault(T val, T def) {
        return val != null ? val : def;
    }

//    public static boolean getIsAIEnabled(EntityLivingBase entity) {
//        if( entity == null ) {
//            return false;
//        }
//
//        return ReflectionUtils.invokeCachedMethod(EntityLivingBase.class, entity, "isAIEnabled", "func_70650_aV", null, null);
//    }

    public static Entity getFirstPassengerOfClass(Entity e, Class<? extends Entity> psgClass) {
        for( Entity psg : e.getPassengers() ) {
            if( psgClass.isInstance(psg.getClass()) ) {
                return psg;
            }
        }

        return null;
    }

    public static boolean getIsPotionSplash(PotionEffect potionEffect) {
        if( potionEffect == null ) {
            return false;
        }

        return ReflectionUtils.getCachedFieldValue(PotionEffect.class, potionEffect, "isSplashPotion", "field_82723_d");
    }

    public static int getOreRecipeWidth(ShapedOreRecipe recipe) {
        if( recipe == null ) {
            return 0;
        }

        return ReflectionUtils.getCachedFieldValue(ShapedOreRecipe.class, recipe, "width", "width");
    }

    public static int getOreRecipeHeight(ShapedOreRecipe recipe) {
        if( recipe == null ) {
            return 0;
        }

        return ReflectionUtils.getCachedFieldValue(ShapedOreRecipe.class, recipe, "height", "height");
    }

    public static float getLastDamage(EntityLivingBase entity) {
        if( entity == null ) {
            return 0.0F;
        }

        return ReflectionUtils.getCachedFieldValue(EntityLivingBase.class, entity, "lastDamage", "field_110153_bc");
    }

    public static void setAiMoveTowardsTargetEntity(EntityAIMoveTowardsTarget ai, EntityLivingBase e) {
        ReflectionUtils.setCachedFieldValue(EntityAIMoveTowardsTarget.class, ai, "targetEntity", "field_75429_b", e);
    }

    public static short getShortTagAt(NBTTagList list, int index) {
        List tagList = ReflectionUtils.getCachedFieldValue(NBTTagList.class, list, "tagList", "field_74747_a");

        if( index >= 0 && index < tagList.size() ) {
            NBTBase nbtbase = (NBTBase)tagList.get(index);
            return nbtbase.getId() == Constants.NBT.TAG_SHORT ? ((NBTTagShort)nbtbase).getShort() : 0;
        } else {
            return 0;
        }
    }

    public static ShapedRecipes findShapedRecipe(ItemStack result) {
        List recipes = CraftingManager.getInstance().getRecipeList();
        for( Object recipe : recipes ) {
            if( recipe instanceof ShapedRecipes ) {
                ShapedRecipes sRecipe = ((ShapedRecipes) recipe);
                ItemStack recipeResult = sRecipe.getRecipeOutput();
                if( areStacksEqual(recipeResult, result, result.hasTagCompound() ? NBT_COMPARATOR_FIXD : null) ) {
                    return sRecipe;
                }
            }
        }

        return null;
    }

    public static ShapedOreRecipe findShapedOreRecipe(ItemStack result) {
        List recipes = CraftingManager.getInstance().getRecipeList();
        for( Object recipe : recipes ) {
            if( recipe instanceof ShapedOreRecipe ) {
                ShapedOreRecipe sRecipe = ((ShapedOreRecipe) recipe);
                ItemStack recipeResult = sRecipe.getRecipeOutput();
                if( areStacksEqual(recipeResult, result, result.hasTagCompound() ? NBT_COMPARATOR_FIXD : null) ) {
                    return sRecipe;
                }
            }
        }

        return null;
    }

    public static ShapelessRecipes findShapelessRecipe(ItemStack result) {
        List recipes = CraftingManager.getInstance().getRecipeList();
        for( Object recipe : recipes ) {
            if( recipe instanceof ShapelessRecipes ) {
                ShapelessRecipes sRecipe = ((ShapelessRecipes) recipe);
                ItemStack recipeResult = sRecipe.getRecipeOutput();
                if( areStacksEqual(recipeResult, result, result.hasTagCompound() ? NBT_COMPARATOR_FIXD : null) ) {
                    return sRecipe;
                }
            }
        }

        return null;
    }

    public static EntityAIBase getAIFromTaskList(Set<EntityAITasks.EntityAITaskEntry> taskList, Class<? extends EntityAIBase> cls) {
        for( Object obj : taskList ) {
            if( obj instanceof EntityAITasks.EntityAITaskEntry ) {
                EntityAITasks.EntityAITaskEntry entry = (EntityAITasks.EntityAITaskEntry) obj;
                if( entry.action.getClass().equals(cls) ) {
                    return entry.action;
                }
            }
        }

        return null;
    }

    public static boolean areStacksEqual(ItemStack firstStack, ItemStack secondStack, Comparator<NBTTagCompound> comparator) {
        if(firstStack != null && secondStack != null) {
            Item firstItem = firstStack.getItem();
            Item secondItem = secondStack.getItem();
            return (firstItem == secondItem && ((comparator == null || comparator.compare(firstStack.getTagCompound(), secondStack.getTagCompound()) == 0) && (firstStack
                    .getItemDamage() == OreDictionary.WILDCARD_VALUE || secondStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || firstStack.getItemDamage() == secondStack.getItemDamage()))
               );
        } else {
            return firstStack == secondStack;
        }
    }

    public static boolean isStackInArray(ItemStack stack, ItemStack... stacks) {
        for( ItemStack currentStack : stacks ) {
            if( areStacksEqual(stack, currentStack, NBT_COMPARATOR_FIXD) ) {
                return true;
            }
        }

        return false;
    }

    public static Pair<Integer, ItemStack> getSimilarStackFromInventory(ItemStack stack, IInventory inv, Comparator<NBTTagCompound> comparator) {
        if( !ItemStackUtils.isValidStack(stack) ) {
            return null;
        }

        if( inv == null ) {
            return null;
        }

        int size = inv.getSizeInventory();
        for( int i = 0; i < size; i++ ) {
            ItemStack invStack = inv.getStackInSlot(i);
            if( ItemStackUtils.isValidStack(invStack) ) {
                if( comparator == null ) {
                    if( ItemStackUtils.areStacksEqual(stack, invStack, false) ) {
                        return Pair.with(i, invStack);
                    }
                } else {
                    if( areStacksEqual(stack, invStack, comparator) ) {
                        return Pair.with(i, invStack);
                    }
                }
            }
        }

        return null;
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv) {
        return addStackToInventory(is, inv, true);
    }

    public static boolean canStackFitInInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return canStackFitInInventory(is, inv, checkNBT, maxStackSize, 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static boolean canStackFitInInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        ItemStack stack = is.copy();

        for( int i = begin; i < end; i++ ) {
            ItemStack invIS = inv.getStackInSlot(i);
            if( invIS != null && ((checkNBT && areStacksEqual(is, invIS, NBT_COMPARATOR_FIXD)) || (!checkNBT && ItemStackUtils.areStacksEqual(is, invIS, false))) ) {
                int fit = StrictMath.min(invIS.getMaxStackSize(), maxStackSize) - invIS.stackSize;
                if( fit >= stack.stackSize ) {
                    return true;
                } else {
                    stack.stackSize -= fit;
                }
            } else if( invIS == null && inv.isItemValidForSlot(i, stack) ) {
                int max = StrictMath.min(stack.getMaxStackSize(), maxStackSize);
                if( stack.stackSize - max <= 0 ) {
                    return true;
                } else {
                    stack.stackSize -= max;
                }
            }
        }

        return false;
    }
    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT) {
        return addStackToInventory(is, inv, checkNBT, inv.getInventoryStackLimit());
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        int invSize = inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0);

        ItemStack invIS;
        int rest;
        for( int i = 0; i < invSize && is != null; ++i ) {
            invIS = inv.getStackInSlot(i);
            if( invIS != null && ((checkNBT && areStacksEqual(is, invIS, NBT_COMPARATOR_FIXD)) || (!checkNBT && ItemStackUtils.areStacksEqual(is, invIS, false))) ) {
                rest = is.stackSize + invIS.stackSize;
                int maxStack = Math.min(invIS.getMaxStackSize(), maxStackSize);
                if( rest <= maxStack ) {
                    invIS.stackSize = rest;
                    inv.setInventorySlotContents(i, invIS.copy());
                    is = null;
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.stackSize = maxStack;
                inv.setInventorySlotContents(i, invIS.copy());
                is.stackSize = rest1;
            } else if( invIS == null && inv.isItemValidForSlot(i, is) ) {
                if( is.stackSize <= maxStackSize ) {
                    inv.setInventorySlotContents(i, is.copy());
                    is = null;
                    break;
                }

                rest = is.stackSize - maxStackSize;
                is.stackSize = maxStackSize;
                inv.setInventorySlotContents(i, is.copy());
                is.stackSize = rest;
            }
        }

        return is;
    }

    /* Stuff from CodeChickenCore here (may be modified by me). License:

The MIT License (MIT)

Copyright (c) 2014 ChickenBones

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

     */

    public static boolean canStack(ItemStack stack1, ItemStack stack2, boolean consumeAll) {
        return stack1 == null || stack2 == null
                || (stack1.isStackable()
                    && stack1.getItem() == stack2.getItem()
                    && (!stack2.getHasSubtypes() || stack2.getItemDamage() == stack1.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(stack2, stack1)
                    && (!consumeAll || stack1.stackSize + stack2.stackSize <= stack1.getMaxStackSize()));
    }

    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
        return writeItemStacksToTag(items, maxQuantity, null, null);
    }

    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity, Object caller, String callbackMethod) {
        NBTTagList tagList = new NBTTagList();

        for( int i = 0; i < items.length; i++ ) {
            if( items[i] != null ) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("Slot", (short) i);
                items[i].writeToNBT(tag);
                if( maxQuantity > Short.MAX_VALUE ) {
                    tag.setInteger("Quantity", Math.min(items[i].stackSize, maxQuantity));
                } else if( maxQuantity > Byte.MAX_VALUE ) {
                    tag.setShort("Quantity", (short) Math.min(items[i].stackSize, maxQuantity));
                } else {
                    tag.setByte("Quantity", (byte) Math.min(items[i].stackSize, maxQuantity));
                }

                if( callbackMethod != null ) {
                    try {
                        Method callback = ReflectionUtils.getCachedMethod(caller.getClass(), callbackMethod, callbackMethod, ItemStack.class, NBTTagCompound.class);
                        NBTTagCompound stackNbt = new NBTTagCompound();
                        callback.invoke(caller, items[i], stackNbt);
                        tag.setTag("StackNBT", stackNbt);
                    } catch( IllegalAccessException | InvocationTargetException e ) {
                        throw new RuntimeException("Cannot call callback method for writeItemStacksToTag()!", e);
                    }
                }

                tagList.appendTag(tag);
            }
        }

        return tagList;
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
        readItemStacksFromTag(items, tagList, null, null);
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList, Object caller, String callbackMethod) {
        for( int i = 0; i < tagList.tagCount(); i++ ) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            short b = tag.getShort("Slot");
            items[b] = ItemStack.loadItemStackFromNBT(tag);
            if(tag.hasKey("Quantity")) {
                items[b].stackSize = ((NBTBase.NBTPrimitive)tag.getTag("Quantity")).getInt();
            }

            if( callbackMethod != null && tag.hasKey("StackNBT") ) {
                try {
                    Method callback = ReflectionUtils.getCachedMethod(caller.getClass(), callbackMethod, callbackMethod, ItemStack.class, NBTTagCompound.class);
                    callback.invoke(caller, items[b], tag.getTag("StackNBT"));
                } catch( IllegalAccessException | InvocationTargetException e ) {
                    throw new RuntimeException("Cannot call callback method for readItemStacksFromTag()!", e);
                }
            }
        }
    }
}
