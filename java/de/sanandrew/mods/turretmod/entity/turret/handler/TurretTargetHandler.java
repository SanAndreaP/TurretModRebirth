/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.entity.turret.handler;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.TargetHandler;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetList;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetListRequest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class TurretTargetHandler
        implements TargetHandler<EntityTurretBase>
{
    private static final int DW_TARGET = 25; /* STRING */

    private IEntitySelector parentTargetSelector;

    protected Entity currentTarget;

    protected Map<Class<? extends EntityLiving>, Boolean> activeTargets = new HashMap<>();
    protected Integer targetMapHash = null;

    public void onTurretConstruct(EntityTurretBase turret) {
        parentTargetSelector = new ParentEntitySelector(this, turret);

        Predicate<Class> entityTargetPredicate = new EntityLivingPredicate();

        Collection<Class<? extends EntityLiving>> livingClsList = EntityLivingPredicate.getLivingList();

        for( Class<? extends EntityLiving> livingCls : livingClsList ) {
            this.activeTargets.put(livingCls, IMob.class.isAssignableFrom(livingCls));
        }

        if( !turret.worldObj.isRemote ) {
            this.targetMapHash = this.activeTargets.hashCode();
        }
    }

    public void onTurretInit(DataWatcher dataWatcher) {
        dataWatcher.addObject(DW_TARGET, ""); // Target name
    }

    public void onTurretUpdate(EntityTurretBase turret) {
        int currTargetHash = this.activeTargets.hashCode();
        if( this.targetMapHash == null || this.targetMapHash != currTargetHash ) {
            if( turret.worldObj.isRemote ) {
                PacketTargetListRequest.sendPacket(turret);
            } else {
                PacketTargetList.sendPacket(turret);
            }
            this.targetMapHash = currTargetHash; // prevent resending packet until it arrives
        }
    }

    @Override
    public Entity getCurrentTarget() {
        return this.currentTarget;
    }

    public void writeToNbt(EntityTurretBase turretBase, NBTTagCompound nbt) {
        NBTTagList targetList = new NBTTagList();
        for( Entry<Class<? extends EntityLiving>, Boolean> entry : this.activeTargets.entrySet() ) {
            NBTTagCompound targetNBT = new NBTTagCompound();
            targetNBT.setString("entityClass", (String) EntityList.classToStringMapping.get(entry.getKey()));
            targetNBT.setBoolean("isActive", entry.getValue());
            targetList.appendTag(targetNBT);
        }
        nbt.setTag("targetList", targetList);
    }

    public void readFromNbt(EntityTurretBase turretBase, NBTTagCompound nbt) {
        NBTTagList targetList = nbt.getTagList("targetList", NBT.TAG_COMPOUND);
        for( int i = 0; i < targetList.tagCount(); i++ ) {
            NBTTagCompound targetNBT = targetList.getCompoundTagAt(i);
            Class entityCls = (Class) EntityList.stringToClassMapping.get(targetNBT.getString("entityClass"));
            if( EntityLiving.class.isAssignableFrom(entityCls) && this.activeTargets.containsKey(entityCls) ) {
                this.activeTargets.put(SAPUtils.<Class<? extends EntityLiving>>getCasted(entityCls), targetNBT.getBoolean("isActive"));
            }
        }
    }

    @Override
    public IEntitySelector getParentTargetSelector() {
        return this.parentTargetSelector;
    }

    public void updateTarget(EntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            if( this.currentTarget == null ) {
                List<Entity> targets = turret.getValidTargets();
                if( targets.size() > 0 ) {
                    this.currentTarget = targets.get(turret.getRNG().nextInt(targets.size()));
                    turret.getDataWatcher().updateObject(DW_TARGET, EntityList.getEntityString(this.currentTarget));
                }
            } else {
                if( !this.parentTargetSelector.isEntityApplicable(this.currentTarget) ) {
                    this.currentTarget = null;
                    turret.getDataWatcher().updateObject(DW_TARGET, "");
                }
            }
        }
    }

    @Override
    public String getTargetName(EntityTurretBase turret) {
        return turret.getDataWatcher().getWatchableObjectString(DW_TARGET);
    }

    @Override
    public boolean hasTarget(EntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            return this.currentTarget != null;
        } else {
            String targetName = this.getTargetName(turret);
            return targetName != null && !targetName.isEmpty();
        }
    }

    @Override
    public Map<Class<? extends EntityLiving>, Boolean> getTargetList() {
        return Maps.newHashMap(this.activeTargets);
    }

    @Override
    public void setTargetList(List<Class<? extends EntityLiving>> activeTargetClsList) {
        for( Class<? extends EntityLiving> cls : this.activeTargets.keySet() ) {
            this.activeTargets.put(cls, activeTargetClsList.contains(cls));
        }
        this.targetMapHash = this.activeTargets.hashCode();
    }

    @Override
    public void toggleTarget(Class<? extends EntityLiving> entityCls, boolean flag) {
        this.activeTargets.put(entityCls, flag);
    }

    @Override
    public boolean isTargetActive(EntityLiving entity) {
        return this.isTargetActive(entity.getClass());
    }

    @Override
    public boolean isTargetActive(Class<? extends EntityLiving> entityCls) {
        return this.activeTargets.containsKey(entityCls) && this.activeTargets.get(entityCls);
    }

    private static final class EntityLivingPredicate implements Predicate<Class>
    {
        @Override
        public boolean apply(Class input) {
            return EntityLiving.class.isAssignableFrom(input) && input != EntityLivingBase.class && input != EntityLiving.class
                    && !EntityTurretBase.class.isAssignableFrom(input);
        }

        public static Collection<Class<? extends EntityLiving>> getLivingList() {
            return SAPUtils.getCasted(Collections2.filter(SAPUtils.<Collection<Class>>getCasted(EntityList.stringToClassMapping.values()),
                                                          new EntityLivingPredicate()));
        }
    }

    private static final class ParentEntitySelector implements IEntitySelector
    {
        private final TurretTargetHandler tgtHandler;
        private final EntityTurretBase myTurret;

        private ParentEntitySelector(TurretTargetHandler handler, EntityTurretBase turret) {
            this.tgtHandler = handler;
            this.myTurret = turret;
        }

        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof EntityLiving && this.isTargetApplicable((EntityLiving) entity);
        }

        private boolean isTargetApplicable(EntityLiving living) {
            Boolean isTargetClsActive = this.tgtHandler.activeTargets.get(living.getClass());
            return this.tgtHandler.activeTargets != null && isTargetClsActive != null && isTargetClsActive
                    && living.isEntityAlive() && !living.isEntityInvulnerable() && this.myTurret.getTargetSelector().isEntityApplicable(living);
        }
    }
}
