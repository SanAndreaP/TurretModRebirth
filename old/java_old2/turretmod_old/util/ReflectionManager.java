/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;

public final class ReflectionManager
{
    private static Field recentlyHit;
    private static Field currentTarget;
    private static Field numTicksToChaseTarget;

    public static void setRecentlyHit(EntityLivingBase instance, int value) {
        try {
            if( recentlyHit == null ) {
                recentlyHit = ReflectionHelper.findField(EntityLivingBase.class, "recentlyHit", "field_70718_bc");
            }
            recentlyHit.setInt(instance, value);
        } catch( IllegalAccessException | UnableToAccessFieldException | UnableToFindFieldException ex ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Something went wrong with accessing the recentlyHit field!", ex);
        }
    }

    public static void setCurrentTarget(EntityLiving instance, Entity target, int ticksToChase) {
        try {
            if( currentTarget == null ) {
                currentTarget = ReflectionHelper.findField(EntityLiving.class, "currentTarget", "field_70776_bF");
            }
            if( numTicksToChaseTarget == null ) {
                numTicksToChaseTarget = ReflectionHelper.findField(EntityLiving.class, "numTicksToChaseTarget", "field_70700_bx");
            }

            currentTarget.set(instance, target);
            numTicksToChaseTarget.setInt(instance, ticksToChase);
        } catch( IllegalAccessException | UnableToAccessFieldException | UnableToFindFieldException ex ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Something went wrong with accessing the currentTarget/numTicksToChaseTarget field!", ex);
        }
    }
}
