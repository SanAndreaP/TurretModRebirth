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
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TmrUtils
{
    public static final Random RNG = new Random();

    public static final Comparator<NBTTagCompound> NBT_COMPARATOR_FIXD = new Comparator<NBTTagCompound>() {
        public int compare(NBTTagCompound firstTag, NBTTagCompound secondTag) {
            return firstTag != null && firstTag.equals(secondTag) ? 0 : (secondTag != null ? -1 : 1);
        }
    };

    public static Entity getEntityByUUID(World worldObj, UUID uuid) {
        for( Object entity : worldObj.loadedEntityList ) {
            if( entity instanceof Entity && ((Entity) entity).getUniqueID().equals(uuid) ) {
                return (Entity) entity;
            }
        }

        return null;
    }

    public static boolean getIsAIEnabled(EntityLivingBase entity) {
        if( entity == null ) {
            return false;
        }

        return ReflectionUtils.invokeCachedMethod(EntityLivingBase.class, entity, "isAIEnabled", "func_70650_aV", null, null);
    }

    public static boolean getIsPotionSplash(PotionEffect potionEffect) {
        if( potionEffect == null ) {
            return false;
        }

        return ReflectionUtils.getCachedFieldValue(PotionEffect.class, potionEffect, "isSplashPotion", "field_82723_d");
    }

    public static EntityAIBase getAIFromTaskList(List<?> taskList, Class<?> cls) {
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
            return firstItem != null && secondItem != null
                    ? (firstItem == secondItem
                        && (comparator.compare(firstStack.getTagCompound(), secondStack.getTagCompound()) == 0
                            && (firstStack.getItemDamage() == OreDictionary.WILDCARD_VALUE
                                || secondStack.getItemDamage() == OreDictionary.WILDCARD_VALUE
                                || firstStack.getItemDamage() == secondStack.getItemDamage())
                               )
                           )
                    : firstItem == secondItem;
        } else {
            return firstStack == secondStack;
        }
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

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT) {
        int invSize = inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0);

        ItemStack invIS;
        int rest;
        for( int i = 0; i < invSize && is != null; ++i ) {
            invIS = inv.getStackInSlot(i);
            if( invIS != null && ((checkNBT && areStacksEqual(is, invIS, NBT_COMPARATOR_FIXD)) || (!checkNBT && ItemStackUtils.areStacksEqual(is, invIS, false))) ) {
                rest = is.stackSize + invIS.stackSize;
                int maxStack = Math.min(invIS.getMaxStackSize(), inv.getInventoryStackLimit());
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
                if( is.stackSize <= inv.getInventoryStackLimit() ) {
                    inv.setInventorySlotContents(i, is.copy());
                    is = null;
                    break;
                }

                rest = is.stackSize - inv.getInventoryStackLimit();
                is.stackSize = inv.getInventoryStackLimit();
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
        NBTTagList tagList = new NBTTagList();

        for(int i = 0; i < items.length; ++i) {
            if(items[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("Slot", (short)i);
                items[i].writeToNBT(tag);
                if(maxQuantity > 32767) {
                    tag.setInteger("Quantity", items[i].stackSize);
                } else if(maxQuantity > 127) {
                    tag.setShort("Quantity", (short)items[i].stackSize);
                }

                tagList.appendTag(tag);
            }
        }

        return tagList;
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
        for(int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            short b = tag.getShort("Slot");
            items[b] = ItemStack.loadItemStackFromNBT(tag);
            if(tag.hasKey("Quantity")) {
                items[b].stackSize = ((NBTBase.NBTPrimitive)tag.getTag("Quantity")).func_150287_d();
            }
        }
    }
}
