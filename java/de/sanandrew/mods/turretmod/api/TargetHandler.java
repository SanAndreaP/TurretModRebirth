/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import java.util.List;
import java.util.Map;

public interface TargetHandler<T extends Turret>
{
    Entity getCurrentTarget();
    IEntitySelector getParentTargetSelector();
    String getTargetName(T turret);
    boolean hasTarget(T turret);
    Map<Class<? extends EntityLiving>, Boolean> getTargetList();
    void setTargetList(List<Class<? extends EntityLiving>> activeTargetClsList);
    void toggleTarget(Class<? extends EntityLiving> entityCls, boolean flag);
    boolean isTargetActive(EntityLiving entity);
    boolean isTargetActive(Class<? extends EntityLiving> entityCls);
}
