/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.PlayerList;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public abstract class TargetProcessor
{
    protected static final Map<Class<? extends Entity>, Boolean> ENTITY_TARGET_LIST_STD = new HashMap<>();

    protected final Map<Class<? extends Entity>, Boolean> entityTargetList;
    protected final Map<UUID, Boolean> playerTargetList;

    protected int ammoCount;
    protected ItemStack ammoStack;
    protected int shootTicks;
    protected int initShootTicks;
    protected Entity entityToAttack;
    protected UUID entityToAttackUUID;
    protected EntityTurret turret;

    protected EntityTargetSelector selector;

    protected final SortedMap<Integer, List<TargetingListener>> tgtListeners;

    public TargetProcessor(EntityTurret turret) {
        this.entityTargetList = new HashMap<>(ENTITY_TARGET_LIST_STD);
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
        this.selector = new EntityTargetSelector();
        this.tgtListeners = new TreeMap<>();
        this.initShootTicks = 20;
    }

    public boolean addAmmo(ItemStack stack) {
        if( stack != null && this.isAmmoApplicable(stack) ) {
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(stack);
            UUID currType = this.ammoStack == null ? null : AmmoRegistry.INSTANCE.getType(this.ammoStack).getTypeId();

            if( currType != null && !currType.equals(type.getTypeId()) ) {
                this.dropAmmo();
            }

            int maxCapacity = this.getMaxAmmoCapacity() - this.ammoCount;
            if( maxCapacity > 0 ) {
                if( !this.hasAmmo() ) {
                    this.ammoStack = type.getStoringAmmoItem();
                } else if( !AmmoRegistry.INSTANCE.areAmmoItemsEqual(stack, this.ammoStack) ) {
                    return false;
                }

                int provided = type.getAmmoCapacity();
                int providedStack = stack.stackSize * provided; //provides 4*16=64, needs 56 = 56 / 64 * 4
                if( providedStack - maxCapacity > 0 ) {
                    int stackSub = MathHelper.floor_double(maxCapacity / (double)providedStack * stack.stackSize);
                    if( stackSub > 0 ) {
                        this.ammoCount += stackSub * provided;
                        stack.stackSize -= stackSub;
                    } else {
                        return false;
                    }
                } else {
                    this.ammoCount += providedStack;
                    stack.stackSize = 0;
                }
                return true;
            }
        }

        return false;
    }

    public int getAmmoCount() {
        return this.ammoCount;
    }

    public ItemStack getAmmoStack() {
        if( this.ammoStack != null ) {
            return this.ammoStack.copy();
        } else {
            return null;
        }
    }

    public boolean hasAmmo() {
        return ItemStackUtils.isValidStack(this.ammoStack) && this.ammoCount > 0;
    }

    public void dropExcessAmmo() {
        if( this.hasAmmo() ) {
            int decrAmmo = this.ammoCount - this.getMaxAmmoCapacity();
            if( decrAmmo > 0 ) {
                List<ItemStack> items = new ArrayList<>();
                TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
                int maxStackSize = this.ammoStack.getMaxStackSize();

                while( decrAmmo > 0 && type != null ) {
                    ItemStack stack = this.ammoStack.copy();
                    decrAmmo -= (stack.stackSize = Math.min(decrAmmo / type.getAmmoCapacity(), maxStackSize)) * type.getAmmoCapacity();
                    if( stack.stackSize <= 0 ) {
                        break;
                    }
                    items.add(stack);
                }

                this.ammoCount = this.getMaxAmmoCapacity();

                if( !items.isEmpty() ) {
                    for( ItemStack stack : items ) {
                        EntityItem item = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                        this.turret.worldObj.spawnEntityInWorld(item);
                    }
                }
            }
        }
    }

    public void dropAmmo() {
        if( this.hasAmmo() ) {
            List<ItemStack> items = new ArrayList<>();
            int maxStackSize = this.ammoStack.getMaxStackSize();
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
            while( this.ammoCount > 0 && type != null ) {
                ItemStack stack = this.ammoStack.copy();
                this.ammoCount -= (stack.stackSize = Math.min(this.ammoCount / type.getAmmoCapacity(), maxStackSize)) * type.getAmmoCapacity();
                if( stack.stackSize <= 0 ) {
                    this.ammoCount = 0;
                    break;
                }
                items.add(stack);
            }

            this.ammoStack = null;

            if( !items.isEmpty() ) {
                for( ItemStack stack : items ) {
                    EntityItem item = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                    this.turret.worldObj.spawnEntityInWorld(item);
                }
            }
        }
    }

    public void putAmmoInInventory(IInventory inventory) {
        if( this.hasAmmo() ) {
            List<ItemStack> items = new ArrayList<>();
            int maxStackSize = this.ammoStack.getMaxStackSize();
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
            while( this.ammoCount > 0 && type != null ) {
                ItemStack stack = this.ammoStack.copy();
                this.ammoCount -= (stack.stackSize = Math.min(this.ammoCount / type.getAmmoCapacity(), maxStackSize)) * type.getAmmoCapacity();
                if( stack.stackSize <= 0 ) {
                    this.ammoCount = 0;
                    break;
                }
                items.add(stack);
            }

            this.ammoStack = null;

            if( !items.isEmpty() ) {
                for( ItemStack stack : items ) {
                    stack = TmrUtils.addStackToInventory(stack, inventory);
                    if( stack != null ) {
                        EntityItem item = new EntityItem(this.turret.worldObj, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                        this.turret.worldObj.spawnEntityInWorld(item);
                    }
                }
            }
        }
    }

    public boolean isAmmoApplicable(ItemStack stack) {
        if( stack != null ) {
            TurretAmmo stackType = AmmoRegistry.INSTANCE.getType(stack);
            if( stackType != null ) {
                if( this.ammoStack == null || AmmoRegistry.INSTANCE.getType(this.ammoStack).getTypeId().equals(stackType.getTypeId()) ) {
                    return this.ammoCount < this.getMaxAmmoCapacity();
                } else {
                    List<TurretAmmo> types = AmmoRegistry.INSTANCE.getTypesForTurret(this.turret.getClass());
                    if( types.contains(stackType) ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public final int getMaxAmmoCapacity() {
        return MathHelper.ceiling_double_int(this.turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).getAttributeValue());
    }

    public final int getMaxShootTicks() {
        return MathHelper.ceiling_double_int(this.turret.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).getAttributeValue());
    }

    public final IProjectile getProjectile() {
        TurretAmmo ammo = AmmoRegistry.INSTANCE.getType(this.ammoStack);
        if( ammo != null ) {
            return ammo.getEntity(this.turret);
        }

        return null;
    }

    public abstract double getRange();

    public abstract SoundEvent getShootSound();

    public abstract SoundEvent getLowAmmoSound();

    public void addTargetingListener(TargetingListener listener) {
        int p = listener.getPriority();
        List<TargetingListener> listenerList = TmrUtils.valueOrDefault(this.tgtListeners.get(p), new ArrayList<>());
        if( !listenerList.contains(listener) ) {
            listenerList.add(listener);
        }
        this.tgtListeners.put(p, listenerList);
    }

    public boolean doAllowTarget(Entity e) {
        boolean ret = true;
        for( Map.Entry<Integer, List<TargetingListener>> listenerEntries : this.tgtListeners.entrySet() ) {
            for( TargetingListener listener : listenerEntries.getValue() ) {
                ret = listener.isTargetApplicable(this.turret, e, ret);
            }
        }

        return ret;
    }

    public void shootProjectile() {
        if( this.hasAmmo() ) {
            Entity projectile = (Entity) this.getProjectile();
            assert projectile != null;
            this.turret.worldObj.spawnEntityInWorld(projectile);
            this.turret.worldObj.playSound(null, this.turret.posX, this.turret.posY, this.turret.posZ, this.getShootSound(), SoundCategory.NEUTRAL, 1.8F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
            this.ammoCount--;
        } else {
            this.turret.worldObj.playSound(null, this.turret.posX, this.turret.posY, this.turret.posZ, this.getLowAmmoSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
        }
    }

    public void onTick() {
        boolean changed = false;

        if( this.shootTicks > 0 ) {
            this.shootTicks--;
        }

        if( this.entityToAttack == null && this.entityToAttackUUID != null ) {
            this.entityToAttack = TmrUtils.getEntityByUUID(turret.worldObj, this.entityToAttackUUID);
        }

        double range = this.getRange();
        if( this.entityToAttack == null ) {
            AxisAlignedBB aabb = this.turret.getEntityBoundingBox().expand(range, range, range);
            for( Object entityObj : this.turret.worldObj.getEntitiesInAABBexcluding(this.turret, aabb, this.selector) ) {
                EntityLivingBase livingBase = (EntityLivingBase) entityObj;
                boolean isEntityValid = this.turret.canEntityBeSeen(livingBase) && livingBase.isEntityAlive() && this.turret.getDistanceToEntity(livingBase) <= range;
                if( isEntityValid && doAllowTarget(livingBase) ) {
                    this.entityToAttack = livingBase;
                    this.entityToAttackUUID = livingBase.getUniqueID();
                    changed = true;
                    break;
                }
            }
        }

        if( this.entityToAttack != null ) {
            boolean isEntityValid = this.turret.canEntityBeSeen(this.entityToAttack) && this.entityToAttack.isEntityAlive() && this.turret.getDistanceToEntity(this.entityToAttack) <= range;
            boolean isTargetValid = Boolean.TRUE.equals(this.entityTargetList.get(this.entityToAttack.getClass()));
            boolean isPlayerValid = Boolean.TRUE.equals(this.playerTargetList.get(this.entityToAttack.getUniqueID())) || Boolean.TRUE.equals(this.playerTargetList.get(TmrUtils.EMPTY_UUID));
            if( isEntityValid && (isTargetValid || isPlayerValid) && doAllowTarget(this.entityToAttack) ) {
                if( this.shootTicks == 0 && --this.initShootTicks <= 0 ) {
                    shootProjectile();
                    this.shootTicks = this.getMaxShootTicks();
                    changed = true;
                }
            } else {
                this.initShootTicks = 15;
                this.entityToAttack = null;
                this.entityToAttackUUID = null;
                changed = true;
            }
        }

        if( changed ) {
            this.turret.updateState();
        }
    }

    public static void initialize() {
        EntityList.CLASS_TO_NAME.keySet().stream()
                .filter(cls -> EntityLiving.class.isAssignableFrom(cls) && !EntityTurret.class.isAssignableFrom(cls) && !EntityLiving.class.equals(cls))
                .forEach(cls -> ENTITY_TARGET_LIST_STD.put(cls, IMob.class.isAssignableFrom(cls)));
    }

    public EntityTurret getTurret() {
        return this.turret;
    }

    public boolean hasTarget() {
        return this.entityToAttack != null;
    }

    public Entity getTarget() {
        return this.entityToAttack;
    }

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("ammoCount", this.ammoCount);
        if( this.ammoStack != null ) {
            NBTTagCompound stackTag = new NBTTagCompound();
            this.ammoStack.writeToNBT(stackTag);
            nbt.setTag("ammoStack", stackTag);
        }
        if( this.entityToAttackUUID != null ) {
            nbt.setString("targetUUID", this.entityToAttackUUID.toString());
        }

        NBTTagList entityTargets = new NBTTagList();
        for( Class cls : this.getEnabledEntityTargets() ) {
            entityTargets.appendTag(new NBTTagString(cls.getName()));
        }
        nbt.setTag("entityTargets", entityTargets);

        NBTTagList playerTargets = new NBTTagList();
        for( UUID uuid : this.getEnabledPlayerTargets() ) {
            playerTargets.appendTag(new NBTTagString(uuid.toString()));
        }
        nbt.setTag("playerTargets", playerTargets);
    }

    public void readFromNbt(NBTTagCompound nbt) {
        this.ammoCount = nbt.getInteger("ammoCount");
        if( nbt.hasKey("ammoStack") ) {
            this.ammoStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ammoStack"));
        }
        if( nbt.hasKey("targetUUID") ) {
            this.entityToAttackUUID = UUID.fromString(nbt.getString("targetUUID"));
        }

        List<Class<? extends Entity>> entityTgt = new ArrayList<>();
        NBTTagList list = nbt.getTagList("entityTargets", Constants.NBT.TAG_STRING);
        for( int i = 0; i < list.tagCount(); i++ ) {
            try {
                Class cls = Class.forName(list.getStringTagAt(i));
                //noinspection unchecked
                entityTgt.add(cls);
            } catch( ClassNotFoundException | ClassCastException ignored ) { }
        }
        this.updateEntityTargets(entityTgt);

        List<UUID> playerTgt = new ArrayList<>();
        list = nbt.getTagList("playerTargets", Constants.NBT.TAG_STRING);
        for( int i = 0; i < list.tagCount(); i++ ) {
            try {
                playerTgt.add(UUID.fromString(list.getStringTagAt(i)));
            } catch( IllegalArgumentException ignored ) { }
        }
        this.updatePlayerTargets(playerTgt.toArray(new UUID[playerTgt.size()]));
    }

    public List<Class<? extends Entity>> getEnabledEntityTargets() {
        Collection<Class<? extends Entity>> enabledClasses = Maps.filterEntries(this.entityTargetList, input -> input != null && input.getValue()).keySet();

        return new ArrayList<>(enabledClasses);
    }

    public UUID[] getEnabledPlayerTargets() {
        Collection<UUID> enabledUUIDs = Maps.filterEntries(this.playerTargetList, input -> input != null && input.getValue()).keySet();

        return enabledUUIDs.toArray(new UUID[enabledUUIDs.size()]);
    }

    public Map<Class<? extends Entity>, Boolean> getEntityTargets() {
        return new HashMap<>(this.entityTargetList);
    }

    public Map<UUID, Boolean> getPlayerTargets() {
        return new HashMap<>(this.playerTargetList);
    }

    public void updateEntityTarget(Class<? extends Entity> cls, boolean active) {
        if( ENTITY_TARGET_LIST_STD.containsKey(cls) ) {
            this.entityTargetList.put(cls, active);
        }
    }

    public void updatePlayerTarget(UUID uid, boolean active) {
        this.playerTargetList.put(uid, active);
    }

    public void updateEntityTargets(List<Class<? extends Entity>> classes) {
        this.entityTargetList.entrySet().stream().forEach(entry -> entry.setValue(false));

        classes.stream().filter(cls -> cls != null && ENTITY_TARGET_LIST_STD.containsKey(cls)).forEach(cls -> this.entityTargetList.put(cls, true));
    }

    public void updatePlayerTargets(UUID[] uuids) {
        this.playerTargetList.entrySet().stream().forEach(entry ->entry.setValue(false));

        for( UUID uuid : uuids ) {
            if( uuid != null ) {
                this.playerTargetList.put(uuid, true);
            }
        }
    }

    public void updateClientState(int targetId, int ammoCount, ItemStack ammoStack) {
        if( this.turret.worldObj.isRemote ) {
            this.entityToAttack = targetId < 0 ? null : this.turret.worldObj.getEntityByID(targetId);
            this.ammoCount = ammoCount;
            this.ammoStack = ammoStack;
        }
    }

    public String getTargetName() {
        return this.hasTarget() ? EntityList.getEntityString(this.entityToAttack) : "";
    }

    private class EntityTargetSelector
            implements Predicate<Entity>
    {
        @Override
        public boolean apply(Entity entity) {
            if( entity instanceof EntityLiving ) {
                if( Boolean.TRUE.equals(TargetProcessor.this.entityTargetList.get(entity.getClass()))  ) {
                    return !entity.isDead;
                }
            } else if( entity instanceof EntityPlayer ) {
                if( Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(entity.getUniqueID()))  || Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(TmrUtils.EMPTY_UUID))) {
                    return !entity.isDead;
                }
            }
            return false;
        }
    }
}
