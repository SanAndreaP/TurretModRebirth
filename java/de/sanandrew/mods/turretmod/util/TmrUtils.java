/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.darkhax.bookshelf.lib.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TmrUtils
{
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
}
