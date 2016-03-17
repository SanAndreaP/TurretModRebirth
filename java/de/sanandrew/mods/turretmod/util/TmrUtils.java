/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.darkhax.bookshelf.lib.util.NBTUtils;
import net.darkhax.bookshelf.lib.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TmrUtils
{
    public static final Random RNG = new Random();

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
}
