package de.sanandrew.mods.turretmod.entity.turret;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.PlayerList;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public abstract class TargetProcessor
{
    private static final Map<Class, Boolean> ENTITY_TARGET_LIST_STD = new HashMap<>();

    private final Map<Class, Boolean> entityTargetList;
    private final Map<UUID, Boolean> playerTargetList;

    private int ammoCount;
    private ItemStack ammoStack;
    private int shootTicks;
    private Entity entityToAttack;
    private UUID entityToAttackUUID;
    private EntityTurret turret;

    private EntityTargetSelector selector = new EntityTargetSelector();

    public TargetProcessor(EntityTurret turret) {
        this.entityTargetList = new HashMap<>(ENTITY_TARGET_LIST_STD);
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
    }

    public boolean addAmmo(ItemStack stack) {
        if( stack != null && this.isAmmoApplicable(stack) ) {
            TurretAmmo type = ItemRegistry.ammo.getAmmoType(stack);
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
                TurretAmmo type = ItemRegistry.ammo.getAmmoType(this.ammoStack);
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
            TurretAmmo type = ItemRegistry.ammo.getAmmoType(this.ammoStack);
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
            TurretAmmo type = ItemRegistry.ammo.getAmmoType(this.ammoStack);
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
        if( stack != null && this.ammoCount < this.getMaxAmmoCapacity() ) {
            TurretAmmo stackType = ItemRegistry.ammo.getAmmoType(stack);
            if( stackType != null ) {
                List<TurretAmmo> types = AmmoRegistry.INSTANCE.getTypesForTurret(this.turret.getClass());
                if( types.contains(stackType) ) {
                    return true;
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

    public abstract EntityTurretProjectile getProjectile();

    public abstract double getRange();

    public abstract String getShootSound();

    public abstract String getLowAmmoSound();

    public void shootProjectile() {
        if( this.hasAmmo() ) {
            EntityTurretProjectile projectile = this.getProjectile();
            this.turret.worldObj.spawnEntityInWorld(projectile);
            this.turret.worldObj.playSoundAtEntity(this.turret, this.getShootSound(), 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
            this.ammoCount--;
        } else {
            this.turret.worldObj.playSoundAtEntity(this.turret, this.getLowAmmoSound(), 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
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
            AxisAlignedBB aabb = turret.boundingBox.expand(range, range, range);
            for( Object entityObj : turret.worldObj.getEntitiesWithinAABBExcludingEntity(turret, aabb, this.selector) ) {
                EntityLivingBase livingBase = (EntityLivingBase) entityObj;
                if( turret.canEntityBeSeen(livingBase) ) {
                    this.entityToAttack = livingBase;
                    this.entityToAttackUUID = livingBase.getUniqueID();
                    changed = true;
                    break;
                }
            }
        }

        if( this.entityToAttack != null ) {
            boolean isEntityValid = turret.canEntityBeSeen(this.entityToAttack) && this.entityToAttack.isEntityAlive() && turret.getDistanceToEntity(this.entityToAttack) <= range;
            boolean isTargetValid = Boolean.TRUE.equals(this.entityTargetList.get(this.entityToAttack.getClass()));
            boolean isPlayerValid = Boolean.TRUE.equals(this.playerTargetList.get(this.entityToAttack.getUniqueID())) || Boolean.TRUE.equals(this.playerTargetList.get(PlayerList.EMPTY_UUID));
            if( isEntityValid && (isTargetValid || isPlayerValid) ) {
                if( this.shootTicks == 0 ) {
                    shootProjectile();
                    this.shootTicks = this.getMaxShootTicks();
                    changed = true;
                }
            } else {
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
        for( Object clsObj : EntityList.classToStringMapping.keySet() ) {
            Class cls = (Class) clsObj;
            if( EntityLiving.class.isAssignableFrom(cls) && !EntityTurret.class.isAssignableFrom(cls) && !EntityLiving.class.equals(cls) ) {
                ENTITY_TARGET_LIST_STD.put(cls, IMob.class.isAssignableFrom(cls));
            }
        }
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

        List<Class> entityTgt = new ArrayList<>();
        NBTTagList list = nbt.getTagList("entityTargets", Constants.NBT.TAG_STRING);
        for( int i = 0; i < list.tagCount(); i++ ) {
            try {
                entityTgt.add(Class.forName(list.getStringTagAt(i)));
            } catch( ClassNotFoundException ignored ) { }
        }
        this.updateEntityTargets(entityTgt.toArray(new Class[entityTgt.size()]));

        List<UUID> playerTgt = new ArrayList<>();
        list = nbt.getTagList("playerTargets", Constants.NBT.TAG_STRING);
        for( int i = 0; i < list.tagCount(); i++ ) {
            try {
                playerTgt.add(UUID.fromString(list.getStringTagAt(i)));
            } catch( IllegalArgumentException ignored ) { }
        }
        this.updatePlayerTargets(playerTgt.toArray(new UUID[playerTgt.size()]));
    }

    public Class[] getEnabledEntityTargets() {
        Collection<Class> enabledClasses = Maps.filterEntries(this.entityTargetList, new Predicate<Map.Entry<Class, Boolean>>() {
            @Override
            public boolean apply(Map.Entry<Class, Boolean> input) {
                return input != null && input.getValue();
            }
        }).keySet();

        return enabledClasses.toArray(new Class[enabledClasses.size()]);
    }

    public UUID[] getEnabledPlayerTargets() {
        Collection<UUID> enabledUUIDs = Maps.filterEntries(this.playerTargetList, new Predicate<Map.Entry<UUID, Boolean>>() {
            @Override
            public boolean apply(Map.Entry<UUID, Boolean> input) {
                return input != null && input.getValue();
            }
        }).keySet();

        return enabledUUIDs.toArray(new UUID[enabledUUIDs.size()]);
    }

    public Map<Class, Boolean> getEntityTargets() {
        return new HashMap<>(this.entityTargetList);
    }

    public Map<UUID, Boolean> getPlayerTargets() {
        return new HashMap<>(this.playerTargetList);
    }

    public void updateEntityTarget(Class cls, boolean active) {
        if( ENTITY_TARGET_LIST_STD.containsKey(cls) ) {
            this.entityTargetList.put(cls, active);
        }
    }

    public void updatePlayerTarget(UUID uid, boolean active) {
        this.playerTargetList.put(uid, active);
    }

    public void updateEntityTargets(Class[] classes) {
        for( Map.Entry<Class, Boolean> entry : this.entityTargetList.entrySet() ) {
            entry.setValue(false);
        }

        for( Class cls : classes ) {
            if( cls != null && ENTITY_TARGET_LIST_STD.containsKey(cls) ) {
                this.entityTargetList.put(cls, true);
            }
        }
    }

    public void updatePlayerTargets(UUID[] uuids) {
        for( Map.Entry<UUID, Boolean> entry : this.playerTargetList.entrySet() ) {
            entry.setValue(false);
        }

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
            implements IEntitySelector
    {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            if( entity instanceof EntityLiving ) {
                if( Boolean.TRUE.equals(TargetProcessor.this.entityTargetList.get(entity.getClass()))  ) {
                    return !entity.isDead;
                }
            } else if( entity instanceof EntityPlayer ) {
                if( Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(entity.getUniqueID()))  || Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(PlayerList.EMPTY_UUID))) {
                    return !entity.isDead;
                }
            }
            return false;
        }
    }
}
