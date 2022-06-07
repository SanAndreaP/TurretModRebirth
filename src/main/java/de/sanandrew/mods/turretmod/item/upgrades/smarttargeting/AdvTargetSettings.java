/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item.upgrades.smarttargeting;

import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.Leveling;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@IUpgradeData.Syncable
public class AdvTargetSettings
        implements IUpgradeData<AdvTargetSettings>
{
    public static final String NBT_TURRET_AWS = "TurretAwareness";
    public static final String NBT_TAMED_AWS = "TamedAwareness";
    public static final String NBT_CHILD_AWS    = "ChildAwareness";
    public static final String NBT_COUNT_AWS    = "CountAwareness";
    public static final String NBT_COUNT_AMOUNT = "CountAwarenessAmount";

    private TurretAwareness turretAwareness = TurretAwareness.SAME_TYPE;
    private TamedAwareness tamedAwareness = TamedAwareness.UNAWARE;
    private ChildAwareness childAwareness = ChildAwareness.UNAWARE;
    private CountAwareness countAwareness = CountAwareness.NO_COUNT;
    private short countEntities = 0;

    private boolean hasChanged = false;

    AdvTargetSettings() { }

    @Override
    public void load(ITurretEntity turret, @Nonnull CompoundNBT nbt) {
        this.setTurretAwareness(TurretAwareness.fromIndex(nbt.getByte(NBT_TURRET_AWS)));
        this.setTamedAwareness(TamedAwareness.fromIndex(nbt.getByte(NBT_TAMED_AWS)));
        this.setChildAwareness(ChildAwareness.fromIndex(nbt.getByte(NBT_CHILD_AWS)));
        this.setCountAwareness(CountAwareness.fromIndex(nbt.getByte(NBT_COUNT_AWS)));
        this.setCountEntities(nbt.getShort(NBT_COUNT_AMOUNT));
    }

    @Override
    public void save(ITurretEntity turret, @Nonnull CompoundNBT nbt) {
        nbt.putByte(NBT_TURRET_AWS, this.turretAwareness.getIndex());
        nbt.putByte(NBT_TAMED_AWS, this.tamedAwareness.getIndex());
        nbt.putByte(NBT_CHILD_AWS, this.childAwareness.getIndex());
        nbt.putByte(NBT_COUNT_AWS, this.countAwareness.getIndex());
        nbt.putShort(NBT_COUNT_AMOUNT, this.countEntities);
    }

    @Override
    public void onTick(ITurretEntity turretInst) {
        if( this.hasChanged ) {
            turretInst.getUpgradeProcessor().syncUpgrade(SmartTargeting.ID);
            this.hasChanged = false;
        }
    }

    private boolean checkValidity(Entity target, ITurretEntity turret, ITargetProcessor processor) {
        if( !this.turretAwareness.check(target, turret, processor) ) {
            return false;
        }

        if( !this.tamedAwareness.check(target, processor) ) {
            return false;
        }

        return this.childAwareness.check(target);
    }

    public boolean isTargetValid(Entity target, ITurretEntity turret, List<Entity> possibleTargets) {
        ITargetProcessor tgtProcessor = turret.getTargetProcessor();

        if( !checkValidity(target, turret, tgtProcessor) ) {
            return false;
        }

        return this.countAwareness.check(target, this.countEntities, possibleTargets, tgtProcessor, e -> checkValidity(e, turret, tgtProcessor));
    }

    public TurretAwareness getTurretAwareness() {
        return this.turretAwareness;
    }

    public void setTurretAwareness(TurretAwareness awareness) {
        this.turretAwareness = Objects.requireNonNull(awareness);
        this.hasChanged = true;
    }

    public TamedAwareness getTamedAwareness() {
        return this.tamedAwareness;
    }

    public void setTamedAwareness(TamedAwareness awareness) {
        this.tamedAwareness = Objects.requireNonNull(awareness);
        this.hasChanged = true;
    }

    public ChildAwareness getChildAwareness() {
        return this.childAwareness;
    }

    public void setChildAwareness(ChildAwareness childAwareness) {
        this.childAwareness = Objects.requireNonNull(childAwareness);
        this.hasChanged = true;
    }

    public CountAwareness getCountAwareness() {
        return this.countAwareness;
    }

    public void setCountAwareness(CountAwareness countAwareness) {
        this.countAwareness = Objects.requireNonNull(countAwareness);
        this.hasChanged = true;
    }

    public int getCountEntities() {
        return this.countEntities;
    }

    public void setCountEntities(short count) {
        this.countEntities = (short) Math.max(0, Math.min(count, 256));
        this.hasChanged = true;
    }

    public enum TurretAwareness
    {
        UNAWARE,
        SAME_TYPE,
        ALL_TYPES;

        static final TurretAwareness[] VALUES = values();

        public static TurretAwareness fromIndex(int id) {
            if( 0 <= id && id < VALUES.length ) {
                return VALUES[id];
            }

            return UNAWARE;
        }

        public byte getIndex() {
            return (byte) this.ordinal();
        }

        public boolean check(Entity target, ITurretEntity turret, ITargetProcessor processor) {
            List<Entity> turrets = turret.get().level.getEntities(turret.get(), processor.getAdjustedRange(true), ITurretEntity.class::isInstance);
            for( Entity e : turrets ) {
                ITurretEntity otherTurret = (ITurretEntity) e;
                if( this == SAME_TYPE && otherTurret.getDelegate() != turret.getDelegate() ) {
                    continue;
                }

                if( otherTurret.getTargetProcessor().getTarget() == target && otherTurret.getTargetProcessor().hasAmmo() ) {
                    return false;
                }
            }

            return true;
        }
    }

    public enum TamedAwareness
    {
        UNAWARE,
        TARGETED_PLAYERS,
        IGNORE_ALL_TAMED;

        static final TamedAwareness[] VALUES = values();

        public static TamedAwareness fromIndex(int id) {
            if( 0 <= id && id < VALUES.length ) {
                return VALUES[id];
            }

            return UNAWARE;
        }

        public byte getIndex() {
            return (byte) this.ordinal();
        }

        public boolean check(Entity target, ITargetProcessor processor) {
            if( this != UNAWARE && target instanceof TameableEntity ) {
                TameableEntity ownable = (TameableEntity) target;
                return !(ownable.getOwner() instanceof PlayerEntity)
                       || (this != IGNORE_ALL_TAMED && processor.isEntityTargeted(ownable.getOwner()));
            }

            return true;
        }
    }

    public enum ChildAwareness
    {
        UNAWARE,
        ADULTS_ONLY,
        CHILDREN_ONLY;

        static final ChildAwareness[] VALUES = values();

        public static ChildAwareness fromIndex(int id) {
            if( 0 <= id && id < VALUES.length ) {
                return VALUES[id];
            }

            return UNAWARE;
        }

        public byte getIndex() {
            return (byte) this.ordinal();
        }

        public boolean check(Entity target) {
            if( this != UNAWARE && target instanceof AgeableEntity ) {
                boolean isChild = ((AgeableEntity) target).isBaby();
                if( this == CHILDREN_ONLY && !isChild ) {
                    return false;
                } else {
                    return this != ADULTS_ONLY || !isChild;
                }
            }

            return true;
        }
    }

    public enum CountAwareness
    {
        NO_COUNT(false, false),
        ABOVE_GLOBAL(true, true),
        ABOVE_INDIVIDUAL(false, true),
        BELOW_GLOBAL(true, false),
        BELOW_INDIVIDUAL(false, false);

        final boolean isGlobal;
        final boolean isAbove;

        CountAwareness(boolean isGlobal, boolean isAbove) {
            this.isGlobal = isGlobal;
            this.isAbove = isAbove;
        }

        static final CountAwareness[] VALUES = values();

        public static CountAwareness fromIndex(int id) {
            if( 0 <= id && id < VALUES.length ) {
                return VALUES[id];
            }

            return NO_COUNT;
        }

        public byte getIndex() {
            return (byte) this.ordinal();
        }

        public boolean check(Entity target, short aimedCount, List<Entity> possibleTargets, ITargetProcessor processor, Predicate<Entity> entityFilter) {
            if( this != NO_COUNT ) {
                Predicate<Entity> entityTest;

                if( this.isGlobal ) {
                    entityTest = processor::isEntityTargeted;
                } else {
                    entityTest = e -> e != null && e.getType().equals(target.getType());
                }
                long entityCount = possibleTargets.stream().filter(entityFilter).filter(entityTest).count();

                if( this.isAbove && entityCount <= aimedCount ) {
                    return false;
                } else {
                    return this.isAbove || entityCount < aimedCount;
                }
            }

            return true;
        }
    }
}
