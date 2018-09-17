/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades.smartTargeting;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AdvTargetSettings
        implements IUpgradeInstance<AdvTargetSettings>
{
    private TurretAwareness turretAwareness = TurretAwareness.SAME_TYPE;
    private TamedAwareness tamedAwareness = TamedAwareness.UNAWARE;
    private ChildAwareness childAwareness = ChildAwareness.UNAWARE;
    private CountAwareness countAwareness = CountAwareness.NO_COUNT;
    private int countEntities = 0;

    @Override
    public void fromBytes(ObjectInputStream stream) throws IOException {
        this.setTurretAwareness(stream.readByte());
        this.setTamedAwareness(stream.readByte());
        this.setChildAwareness(stream.readByte());
        this.setCountAwareness(stream.readByte());
        this.setCountEntities(stream.readShort());
    }

    @Override
    public void toBytes(ObjectOutputStream stream) throws IOException {
        stream.writeByte(this.turretAwareness.ordinal());
        stream.writeByte(this.tamedAwareness.ordinal());
        stream.writeByte(this.childAwareness.ordinal());
        stream.writeByte(this.countAwareness.ordinal());
        stream.writeShort(this.countEntities);
    }

    public void loadFromNbt(NBTTagCompound nbt) {
        if( nbt.hasKey("turretAwareness") ) {
            this.setTurretAwareness(nbt.getByte("turretAwareness"));
        }
        this.setTamedAwareness(nbt.getByte("tamedAwareness"));
        this.setChildAwareness(nbt.getByte("childAwareness"));
        this.setCountAwareness(nbt.getByte("countAwareness"));
        this.setCountEntities(nbt.getShort("countEntities"));
    }

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setByte("turretAwareness", (byte) this.turretAwareness.ordinal());
        nbt.setByte("tamedAwareness", (byte) this.tamedAwareness.ordinal());
        nbt.setByte("childAwareness", (byte) this.childAwareness.ordinal());
        nbt.setByte("countAwareness", (byte) this.countAwareness.ordinal());
        nbt.setShort("countEntities", (short) this.countEntities);
    }

    private boolean checkValidity(Entity target, ITurretInst turretInst) {
        ITargetProcessor tgtProcessor = turretInst.getTargetProcessor();
        if( this.turretAwareness != TurretAwareness.UNAWARE ) {
            List<Entity> turrets = turretInst.get().world.getEntitiesInAABBexcluding(turretInst.get(), tgtProcessor.getAdjustedRange(true),
                                                                                           e -> e instanceof ITurretInst);
            for( Entity e : turrets ) {
                ITurretInst otherTurret = (ITurretInst) e;
                if( this.turretAwareness == TurretAwareness.SAME_TYPE && otherTurret.getTurret() != turretInst.getTurret() ) {
                    continue;
                }

                if( otherTurret.getTargetProcessor().getTarget() == target && otherTurret.getTargetProcessor().hasAmmo() ) {
                    return false;
                }
            }
        }

        if( this.tamedAwareness != TamedAwareness.UNAWARE && target instanceof IEntityOwnable ) {
            IEntityOwnable ownable = (IEntityOwnable) target;
            if( this.tamedAwareness == TamedAwareness.IGNORE_ALL_TAMED && ownable.getOwner() instanceof EntityPlayer ) {
                return false;
            } else if( ownable.getOwner() instanceof EntityPlayer && !tgtProcessor.isEntityTargeted(ownable.getOwner())) {
                return false;
            }
        }

        if( this.childAwareness != ChildAwareness.UNAWARE && target instanceof EntityLivingBase ) {
            boolean isChild = ((EntityLivingBase) target).isChild();
            if( this.childAwareness == ChildAwareness.CHILDREN_ONLY && !isChild ) {
                return false;
            } else if( this.childAwareness == ChildAwareness.ADULTS_ONLY && isChild ) {
                return false;
            }
        }

        return true;
    }

    public boolean isTargetValid(Entity target, ITurretInst turretInst, List<Entity> entitiesInRange) {
        if( !checkValidity(target, turretInst) ) {
            return false;
        }

        if( this.countAwareness != CountAwareness.NO_COUNT ) {
            long entityCount = entitiesInRange.size();
            Stream<Entity> entityStream = entitiesInRange.stream().filter(e -> checkValidity(e, turretInst));
            if( this.countAwareness.isGlobal ) {
                if( entityCount < this.countEntities ) {
                    return false;
                }
                entityCount = entityStream.filter(turretInst.getTargetProcessor()::isEntityTargeted).count();
            } else {
                entityCount = entityStream.filter(e -> e != null && e.getClass().equals(target.getClass())).count();
            }
            if( this.countAwareness.isBelow && entityCount < this.countEntities ) {
                return false;
            } else if( !this.countAwareness.isBelow && entityCount > this.countEntities ) {
                return false;
            }
        }

        return true;
    }

    public TurretAwareness getTurretAwareness() {
        return this.turretAwareness;
    }

    public void setTurretAwareness(TurretAwareness awareness) {
        this.turretAwareness = Objects.requireNonNull(awareness);
    }

    public void setTurretAwareness(int awareness) {
        if( awareness >= 0 && awareness < TurretAwareness.VALUES.length ) {
            this.turretAwareness = TurretAwareness.VALUES[awareness];
        }
    }

    public TamedAwareness getTamedAwareness() {
        return this.tamedAwareness;
    }

    public void setTamedAwareness(TamedAwareness awareness) {
        this.tamedAwareness = Objects.requireNonNull(awareness);
    }

    public void setTamedAwareness(int awareness) {
        if( awareness >= 0 && awareness < TamedAwareness.VALUES.length ) {
            this.tamedAwareness = TamedAwareness.VALUES[awareness];
        }
    }

    public ChildAwareness getChildAwareness() {
        return this.childAwareness;
    }

    public void setChildAwareness(ChildAwareness childAwareness) {
        this.childAwareness = Objects.requireNonNull(childAwareness);
    }

    public void setChildAwareness(int awareness) {
        if( awareness >= 0 && awareness < ChildAwareness.VALUES.length ) {
            this.childAwareness = ChildAwareness.VALUES[awareness];
        }
    }

    public CountAwareness getCountAwareness() {
        return this.countAwareness;
    }

    public void setCountAwareness(CountAwareness countAwareness) {
        this.countAwareness = Objects.requireNonNull(countAwareness);
    }

    public void setCountAwareness(int awareness) {
        if( awareness >= 0 && awareness < CountAwareness.VALUES.length ) {
            this.countAwareness = CountAwareness.VALUES[awareness];
        }
    }

    public int getCountEntities() {
        return this.countEntities;
    }

    public void setCountEntities(int count) {
        this.countEntities = count < 0 ? 0 : count > 256 ? 256 : count;
    }

    public enum TurretAwareness
    {
        UNAWARE,
        SAME_TYPE,
        ALL_TYPES;

        public static final TurretAwareness[] VALUES = values();
    }

    public enum TamedAwareness
    {
        UNAWARE,
        IGNORE_UNTARGETED_PLAYERS,
        IGNORE_ALL_TAMED;

        public static final TamedAwareness[] VALUES = values();
    }

    public enum ChildAwareness
    {
        UNAWARE,
        ADULTS_ONLY,
        CHILDREN_ONLY;

        public static final ChildAwareness[] VALUES = values();
    }

    public enum CountAwareness
    {
        NO_COUNT(false, false),
        IGNORE_IF_BELOW_GLOBAL(true, true),
        IGNORE_IF_BELOW_INDIVIDUAL(false, true),
        IGNORE_IF_ABOVE_GLOBAL(true, false),
        IGNORE_IF_ABOVE_INDIVIDUAL(false, false);

        public final boolean isGlobal;
        public final boolean isBelow;

        CountAwareness(boolean isGlobal, boolean isBelow) {
            this.isGlobal = isGlobal;
            this.isBelow = isBelow;
        }

        public static final CountAwareness[] VALUES = values();
    }
}
