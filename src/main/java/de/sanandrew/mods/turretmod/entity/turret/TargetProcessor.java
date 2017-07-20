/*
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
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TargetProcessor
        implements ITargetProcessor
{
    protected static final Map<Class<? extends Entity>, Boolean> ENTITY_TARGET_LIST_STD = new HashMap<>();
    protected static final int MAX_INIT_SHOOT_TICKS = 20;

    protected final Map<Class<? extends Entity>, Boolean> entityTargetList;
    protected final Map<UUID, Boolean> playerTargetList;

    protected int ammoCount;
    @Nonnull
    protected ItemStack ammoStack;
    protected int shootTicks;
    protected int initShootTicks;
    protected Entity entityToAttack;
    protected UUID entityToAttackUUID;
    protected EntityTurret turret;
    protected boolean isShootingClt;

    protected EntityTargetSelector selector;

    public TargetProcessor(EntityTurret turret) {
        this.entityTargetList = new HashMap<>(ENTITY_TARGET_LIST_STD);
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
        this.selector = new EntityTargetSelector();
        this.initShootTicks = 20;
    }

    @Override
    public boolean addAmmo(@Nonnull ItemStack stack) {
        if( this.isAmmoApplicable(stack) ) {
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(stack);
            UUID currType = ItemStackUtils.isValid(this.ammoStack) ? AmmoRegistry.INSTANCE.getType(this.ammoStack).getTypeId() : null;

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
                int providedStack = stack.getCount() * provided; //provides 4*16=64, needs 56 = 56 / 64 * 4
                if( providedStack - maxCapacity > 0 ) {
                    int stackSub = MathHelper.floor(maxCapacity / (double)providedStack * stack.getCount());
                    if( stackSub > 0 ) {
                        this.ammoCount += stackSub * provided;
                        stack.shrink(stackSub);
                    } else {
                        return false;
                    }
                } else {
                    this.ammoCount += providedStack;
                    stack.setCount(0);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public int getAmmoCount() {
        return this.ammoCount;
    }

    @Override
    @Nonnull
    public ItemStack getAmmoStack() {
        if( ItemStackUtils.isValid(this.ammoStack) ) {
            return this.ammoStack.copy();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean hasAmmo() {
        return ItemStackUtils.isValid(this.ammoStack) && this.ammoCount > 0;
    }

    @Override
    public void dropExcessAmmo() {
        if( this.hasAmmo() ) {
            int decrAmmo = this.ammoCount - this.getMaxAmmoCapacity();
            if( decrAmmo > 0 ) {
                List<ItemStack> items = new ArrayList<>();
                TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
                int maxStackSize = this.ammoStack.getMaxStackSize();

                while( decrAmmo > 0 && type != AmmoRegistry.NULL_TYPE ) {
                    ItemStack stack = this.ammoStack.copy();
                    stack.setCount(Math.min(decrAmmo / type.getAmmoCapacity(), maxStackSize));
                    decrAmmo -= stack.getCount() * type.getAmmoCapacity();
                    if( stack.getCount() <= 0 ) {
                        break;
                    }
                    items.add(stack);
                }

                this.ammoCount = this.getMaxAmmoCapacity();

                if( !items.isEmpty() ) {
                    for( ItemStack stack : items ) {
                        EntityItem item = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                        this.turret.world.spawnEntity(item);
                    }
                }
            }
        }
    }

    @Override
    public void decrAmmo() {
        TargetingEvent.ConsumeAmmo event = new TargetingEvent.ConsumeAmmo(this, this.ammoStack, 1);
        if( !TARGET_BUS.post(event) && event.getResult() != Event.Result.DENY ) {
            this.ammoCount -= event.consumeAmount;
            if( this.ammoCount < 0 ) {
                this.ammoCount = 0;
            }
        }
    }

    public void dropAmmo() {
        if( this.hasAmmo() ) {
            List<ItemStack> items = new ArrayList<>();
            int maxStackSize = this.ammoStack.getMaxStackSize();
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
            while( this.ammoCount > 0 && type != AmmoRegistry.NULL_TYPE ) {
                ItemStack stack = this.ammoStack.copy();
                stack.setCount(Math.min(this.ammoCount / type.getAmmoCapacity(), maxStackSize));
                this.ammoCount -= stack.getCount() * type.getAmmoCapacity();
                if( stack.getCount() <= 0 ) {
                    this.ammoCount = 0;
                    break;
                }
                items.add(stack);
            }

            this.ammoStack = ItemStack.EMPTY;

            if( !items.isEmpty() ) {
                for( ItemStack stack : items ) {
                    EntityItem item = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                    this.turret.world.spawnEntity(item);
                }
            }
        }
    }

    public void putAmmoInInventory(IInventory inventory) {
        if( this.hasAmmo() ) {
            List<ItemStack> items = new ArrayList<>();
            int maxStackSize = this.ammoStack.getMaxStackSize();
            TurretAmmo type = AmmoRegistry.INSTANCE.getType(this.ammoStack);
            while( this.ammoCount > 0 && type != AmmoRegistry.NULL_TYPE ) {
                ItemStack stack = this.ammoStack.copy();
                stack.setCount(Math.min(this.ammoCount / type.getAmmoCapacity(), maxStackSize));
                this.ammoCount -= stack.getCount() * type.getAmmoCapacity();
                if( stack.getCount() <= 0 ) {
                    this.ammoCount = 0;
                    break;
                }
                items.add(stack);
            }

            this.ammoStack = ItemStack.EMPTY;

            if( !items.isEmpty() ) {
                for( ItemStack stack : items ) {
                    stack = InventoryUtils.addStackToInventory(stack, inventory);
                    if( ItemStackUtils.isValid(stack) ) {
                        EntityItem item = new EntityItem(this.turret.world, this.turret.posX, this.turret.posY, this.turret.posZ, stack);
                        this.turret.world.spawnEntity(item);
                    }
                }
            }
        }
    }

    @Override
    public boolean isAmmoApplicable(ItemStack stack) {
        if( stack != null ) {
            TurretAmmo stackType = AmmoRegistry.INSTANCE.getType(stack);
            if( stackType != AmmoRegistry.NULL_TYPE ) {
                if( AmmoRegistry.INSTANCE.getType(this.ammoStack).getTypeId().equals(stackType.getTypeId()) ) {
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

    @Override
    public final int getMaxAmmoCapacity() {
        return MathHelper.ceil(this.turret.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).getAttributeValue());
    }

    @Override
    public final int getMaxShootTicks() {
        return MathHelper.ceil(this.turret.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).getAttributeValue());
    }

    @Override
    public final boolean isShooting() {
        return this.initShootTicks <= 0 || this.isShootingClt;
    }

    @Override
    public Entity getProjectile() {
        TurretAmmo ammo = AmmoRegistry.INSTANCE.getType(this.ammoStack);
        if( ammo != AmmoRegistry.NULL_TYPE ) {
            return ammo.getEntity(this.turret);
        }

        return null;
    }

    @Override
    public double getRangeVal() {
        AxisAlignedBB aabb = getAdjustedRange(false);
        return Math.max(aabb.maxX - aabb.minX, Math.max(aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ)) / 2.0D;
    }

    @Override
    public AxisAlignedBB getAdjustedRange(boolean doOffset) {
        AxisAlignedBB aabb = this.turret.getRangeBB();
        if( this.turret.isUpsideDown ) {
            aabb = new AxisAlignedBB(aabb.minX, -aabb.maxY, aabb.minZ, aabb.maxX, -aabb.minY, aabb.maxZ);
        }
        return doOffset ? aabb.offset(this.turret.posX, this.turret.posY, this.turret.posZ) : aabb;
    }

    private boolean checkTargetListeners(Entity e) {
        TargetingEvent.TargetCheck event = new TargetingEvent.TargetCheck(this, e);
        return !TARGET_BUS.post(event) && event.getResult() != Event.Result.DENY;
    }

    @Override
    public boolean shootProjectile() {
        TargetingEvent.Shooting event = new TargetingEvent.Shooting(this);
        if( TARGET_BUS.post(event) ) {
            return event.getResult() != Event.Result.DENY;
        }

        if( this.hasAmmo() ) {
            Entity projectile = this.getProjectile();
            assert projectile != null;
            this.turret.world.spawnEntity(projectile);
            this.playSound(this.turret.getShootSound(), 1.8F);
            this.turret.setShooting();
            this.decrAmmo();
            return event.getResult() != Event.Result.DENY;
        } else {
            this.playSound(this.turret.getNoAmmoSound(), 1.0F);
            return event.getResult() != Event.Result.ALLOW;
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume) {
        final float pitch = 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F;
        this.turret.world.playSound(null, this.turret.posX, this.turret.posY, this.turret.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
    }

    @Override
    public void onTick() {
        boolean changed = false;

        if( this.shootTicks > 0 ) {
            this.shootTicks--;
        }

        if( this.entityToAttack == null && this.entityToAttackUUID != null ) {
            this.entityToAttack = EntityUtils.getEntityByUUID(turret.world, this.entityToAttackUUID);
        }

        AxisAlignedBB aabb = this.getAdjustedRange(true);
        if( this.entityToAttack == null ) {
            for( Object entityObj : this.turret.world.getEntitiesInAABBexcluding(this.turret, aabb, this.selector) ) {
                EntityLivingBase livingBase = (EntityLivingBase) entityObj;
                boolean isEntityValid = this.turret.canEntityBeSeen(livingBase) && livingBase.isEntityAlive() && livingBase.getEntityBoundingBox().intersects(aabb);
                if( isEntityValid && checkTargetListeners(livingBase) ) {
                    this.entityToAttack = livingBase;
                    this.entityToAttackUUID = livingBase.getUniqueID();
                    changed = true;
                    break;
                }
            }
        }

        if( this.entityToAttack != null ) {
            boolean isEntityValid = this.turret.canEntityBeSeen(this.entityToAttack) && this.entityToAttack.isEntityAlive() && this.entityToAttack.getEntityBoundingBox().intersects(aabb);
            boolean isTargetValid = Boolean.TRUE.equals(this.entityTargetList.get(this.entityToAttack.getClass()));
            boolean isPlayerValid = Boolean.TRUE.equals(this.playerTargetList.get(this.entityToAttack.getUniqueID())) || Boolean.TRUE.equals(this.playerTargetList.get(UuidUtils.EMPTY_UUID));
            if( isEntityValid && (isTargetValid || isPlayerValid) && checkTargetListeners(this.entityToAttack) ) {
                if( this.initShootTicks <= 0 && this.shootTicks == 0 ) {
                    boolean success = shootProjectile();
                    this.shootTicks = success ? this.getMaxShootTicks() : MAX_INIT_SHOOT_TICKS;
                    changed = true;
                } else if( this.initShootTicks > 0 ) {
                    this.initShootTicks--;
                }
            } else {
                this.initShootTicks = MAX_INIT_SHOOT_TICKS;
                this.entityToAttack = null;
                this.entityToAttackUUID = null;
                changed = true;
            }
        }

        if( changed ) {
            TmrConstants.utils.updateTurretState(this.turret);
        }
    }

    public static void initialize() {
        EntityList.getEntityNameList().stream().map(EntityList::getClass)
                .filter(cls -> EntityLiving.class.isAssignableFrom(cls) && !EntityTurret.class.isAssignableFrom(cls) && !EntityLiving.class.equals(cls))
                .forEach(cls -> ENTITY_TARGET_LIST_STD.put(cls, IMob.class.isAssignableFrom(cls)));
    }

    @Override
    public EntityTurret getTurret() {
        return this.turret;
    }

    @Override
    public boolean hasTarget() {
        return this.entityToAttack != null;
    }

    @Override
    public Entity getTarget() {
        return this.entityToAttack;
    }

    @Override
    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("ammoCount", this.ammoCount);
        if( ItemStackUtils.isValid(this.ammoStack) ) {
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

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        this.ammoCount = nbt.getInteger("ammoCount");
        if( nbt.hasKey("ammoStack") ) {
            this.ammoStack = new ItemStack(nbt.getCompoundTag("ammoStack"));
        }
        if( nbt.hasKey("targetUUID") ) {
            this.entityToAttackUUID = UUID.fromString(nbt.getString("targetUUID"));
        }

        List<Class<? extends Entity>> entityTgt = new ArrayList<>();
        NBTTagList list = nbt.getTagList("entityTargets", Constants.NBT.TAG_STRING);
        for( int i = 0; i < list.tagCount(); i++ ) {
            Class<? extends Entity> cls = ReflectionUtils.getClass(list.getStringTagAt(i));
            if( cls != null ) {
                entityTgt.add(cls);
            }
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

    @Override
    public List<Class<? extends Entity>> getEnabledEntityTargets() {
        Collection<Class<? extends Entity>> enabledClasses = Maps.filterEntries(this.entityTargetList, input -> input != null && input.getValue()).keySet();

        return new ArrayList<>(enabledClasses);
    }

    @Override
    public UUID[] getEnabledPlayerTargets() {
        Collection<UUID> enabledUUIDs = Maps.filterEntries(this.playerTargetList, input -> input != null && input.getValue()).keySet();

        return enabledUUIDs.toArray(new UUID[enabledUUIDs.size()]);
    }

    @Override
    public Map<Class<? extends Entity>, Boolean> getEntityTargets() {
        return new HashMap<>(this.entityTargetList);
    }

    @Override
    public Map<UUID, Boolean> getPlayerTargets() {
        return new HashMap<>(this.playerTargetList);
    }

    @Override
    public void updateEntityTarget(Class<? extends Entity> cls, boolean active) {
        if( ENTITY_TARGET_LIST_STD.containsKey(cls) ) {
            this.entityTargetList.put(cls, active);
        }
    }

    @Override
    public void updatePlayerTarget(UUID uid, boolean active) {
        this.playerTargetList.put(uid, active);
    }

    @Override
    public void updateEntityTargets(List<Class<? extends Entity>> classes) {
        this.entityTargetList.entrySet().forEach(entry -> entry.setValue(false));

        classes.stream().filter(cls -> cls != null && ENTITY_TARGET_LIST_STD.containsKey(cls)).forEach(cls -> this.entityTargetList.put(cls, true));
    }

    @Override
    public void updatePlayerTargets(UUID[] uuids) {
        this.playerTargetList.entrySet().forEach(entry -> entry.setValue(false));

        for( UUID uuid : uuids ) {
            if( uuid != null ) {
                this.playerTargetList.put(uuid, true);
            }
        }
    }

    public void updateClientState(int targetId, int ammoCount, ItemStack ammoStack, boolean isShooting) {
        if( this.turret.world.isRemote ) {
            this.entityToAttack = targetId < 0 ? null : this.turret.world.getEntityByID(targetId);
            this.ammoCount = ammoCount;
            this.ammoStack = ammoStack;
            this.isShootingClt = isShooting;
        }
    }

    @Override
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
                if( Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(entity.getUniqueID()))  || Boolean.TRUE.equals(TargetProcessor.this.playerTargetList.get(UuidUtils.EMPTY_UUID))) {
                    return !entity.isDead;
                }
            }
            return false;
        }
    }
}
