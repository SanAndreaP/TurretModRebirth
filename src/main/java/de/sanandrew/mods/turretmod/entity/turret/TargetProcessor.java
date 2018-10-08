/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.google.common.collect.Maps;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
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
import net.minecraft.util.NonNullList;
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
    private static final Map<Class<? extends Entity>, Boolean> ENTITY_TARGET_LIST_STD = new HashMap<>();

    private final Map<Class<? extends Entity>, Boolean> entityTargetList;
    private final Map<UUID, Boolean> playerTargetList;
    private final ITurretInst turret;

    private int ammoCount;
    @Nonnull
    private ItemStack ammoStack;
    private int shootTicks;
    private int initShootTicks;
    private Entity entityToAttack;
    private UUID entityToAttackUUID;
    private boolean isShootingClt;
    private boolean isBlacklistEntity = false;
    private boolean isBlacklistPlayer = false;

    private long processTicks = 0;

    TargetProcessor(ITurretInst turret) {
        this.entityTargetList = new HashMap<>(ENTITY_TARGET_LIST_STD);
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
        this.initShootTicks = 20;
        this.ammoStack = ItemStackUtils.getEmpty();
    }

    @Override
    public boolean addAmmo(@Nonnull ItemStack stack) {
        if( this.isAmmoApplicable(stack) ) {
            IAmmunition type = AmmunitionRegistry.INSTANCE.getType(stack);
            UUID currType = ItemStackUtils.isValid(this.ammoStack) ? AmmunitionRegistry.INSTANCE.getType(this.ammoStack).getTypeId() : null;

            if( currType != null && !currType.equals(type.getTypeId()) ) {
                this.dropAmmo();
            }

            int maxCapacity = this.getMaxAmmoCapacity() - this.ammoCount;
            if( maxCapacity > 0 ) {
                if( !this.hasAmmo() ) {
                    this.ammoStack = AmmunitionRegistry.INSTANCE.getLowestRoundedTypeItem(type.getTypeId());
                } else if( !AmmunitionRegistry.INSTANCE.areAmmoItemsEqual(stack, this.ammoStack) ) {
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
            return ItemStackUtils.getEmpty();
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
                NonNullList<ItemStack> items = NonNullList.create();
                IAmmunition type = AmmunitionRegistry.INSTANCE.getType(this.ammoStack);
                int maxStackSize = this.ammoStack.getMaxStackSize();

                while( decrAmmo > 0 && type != AmmunitionRegistry.NULL_TYPE ) {
                    ItemStack stack = this.ammoStack.copy();
                    stack.setCount(Math.min(decrAmmo / type.getAmmoCapacity(), maxStackSize));
                    decrAmmo -= stack.getCount() * type.getAmmoCapacity();
                    if( stack.getCount() <= 0 ) {
                        break;
                    }
                    items.add(stack);
                }

                this.ammoCount = this.getMaxAmmoCapacity();

                this.spawnItemEntities(items);
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

    private NonNullList<ItemStack> extractAmmoItems() {
        NonNullList<ItemStack> items = NonNullList.create();
        int maxStackSize = this.ammoStack.getMaxStackSize();
        IAmmunition type = AmmunitionRegistry.INSTANCE.getType(this.ammoStack);

        while( this.ammoCount > 0 && type != AmmunitionRegistry.NULL_TYPE ) {
            ItemStack stack = this.ammoStack.copy();
            stack.setCount(Math.min(this.ammoCount / type.getAmmoCapacity(), maxStackSize));
            this.ammoCount -= stack.getCount() * type.getAmmoCapacity();
            if( stack.getCount() <= 0 ) {
                this.ammoCount = 0;
                break;
            }
            items.add(stack);
        }

        return items;
    }

    private void spawnItemEntities(NonNullList<ItemStack> stacks) {
        if( !stacks.isEmpty() ) {
            EntityLiving turretL = this.turret.get();
            stacks.forEach(stack -> {
                EntityItem item = new EntityItem(turretL.world, turretL.posX, turretL.posY, turretL.posZ, stack);
                turretL.world.spawnEntity(item);
            });
        }
    }

    void dropAmmo() {
        if( this.hasAmmo() ) {
            NonNullList<ItemStack> items = this.extractAmmoItems();
            this.ammoStack = ItemStackUtils.getEmpty();
            this.spawnItemEntities(items);
        }
    }

    public void putAmmoInInventory(IInventory inventory) {
        if( this.hasAmmo() ) {
            NonNullList<ItemStack> items = this.extractAmmoItems();

            this.ammoStack = ItemStackUtils.getEmpty();

            if( !items.isEmpty() ) {
                EntityLiving turretL = this.turret.get();
                for( ItemStack stack : items ) {
                    stack = InventoryUtils.addStackToInventory(stack, inventory);
                    if( ItemStackUtils.isValid(stack) ) {
                        EntityItem item = new EntityItem(turretL.world, turretL.posX, turretL.posY, turretL.posZ, stack);
                        turretL.world.spawnEntity(item);
                    }
                }
            }
        }
    }

    @Override
    public boolean isAmmoApplicable(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) ) {
            IAmmunition stackType = AmmunitionRegistry.INSTANCE.getType(stack);
            if( stackType != AmmunitionRegistry.NULL_TYPE ) {
                if( AmmunitionRegistry.INSTANCE.getType(this.ammoStack).getTypeId().equals(stackType.getTypeId()) ) {
                    return this.ammoCount < this.getMaxAmmoCapacity();
                } else {
                    List<IAmmunition> types = AmmunitionRegistry.INSTANCE.getTypesForTurret(this.turret.getTurret());
                    return types.contains(stackType);
                }
            }
        }

        return false;
    }

    @Override
    public final int getMaxAmmoCapacity() {
        return MathHelper.ceil(this.turret.get().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).getAttributeValue());
    }

    @Override
    public final int getMaxShootTicks() {
        return MathHelper.ceil(this.turret.get().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).getAttributeValue());
    }

    @Override
    public final boolean isShooting() {
        return this.initShootTicks <= 0 || this.isShootingClt;
    }

    @Override
    public boolean canShoot() {
        return this.initShootTicks <= 0 && this.shootTicks == 0;
    }

    @Override
    public void setShot(boolean success) {
        this.shootTicks = success ? this.getMaxShootTicks() : this.getMaxInitShootTicks();
    }

    @Override
    public void decrInitShootTicks() {
        this.initShootTicks--;
    }

    @Override
    public void resetInitShootTicks() {
        this.initShootTicks = this.getMaxInitShootTicks();
    }

    private int getMaxInitShootTicks() {
        return (int) Math.round(this.turret.get().getEntityAttribute(TurretAttributes.MAX_INIT_SHOOT_TICKS).getAttributeValue());
    }

    @Override
    public Entity getProjectile() {
        if( this.hasAmmo() ) {
            ITurretProjectile proj = AmmunitionRegistry.INSTANCE.getType(this.ammoStack).getProjectile(this.turret);
            if( proj != null ) {
                if( this.entityToAttack != null ) {
                    return new EntityTurretProjectile(this.turret.get().world, proj, (EntityTurret) this.turret, this.entityToAttack);
                } else {
                    return new EntityTurretProjectile(this.turret.get().world, proj, (EntityTurret) this.turret, this.turret.get().getLookVec());
                }
            }
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
        if( this.turret.isUpsideDown() ) {
            aabb = new AxisAlignedBB(aabb.minX, -aabb.maxY, aabb.minZ, aabb.maxX, -aabb.minY, aabb.maxZ);
        }
        EntityLiving turretL = this.turret.get();
        return doOffset ? aabb.offset(turretL.posX, turretL.posY, turretL.posZ) : aabb;
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

        Entity projectile = this.getProjectile();
        if( projectile != null ) {
            this.turret.get().world.spawnEntity(projectile);
            this.playSound(this.turret.getShootSound(), 1.8F);
            this.turret.setShooting();
            this.decrAmmo();
            return event.getResult() != Event.Result.DENY;
        } else {
            this.playSound(this.turret.getNoAmmoSound(), 1.0F);
            return event.getResult() == Event.Result.ALLOW;
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume) {
        EntityLiving turretL = this.turret.get();
        final float pitch = 1.0F / (turretL.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F;
        turretL.world.playSound(null, turretL.posX, turretL.posY, turretL.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
    }

    @Override
    public void onTick() {
        boolean changed = false;
        EntityLiving turretL = this.turret.get();

        if( !this.turret.isActive() ) {
            if( this.entityToAttack != null || this.entityToAttackUUID != null ) {
                this.resetInitShootTicks();
                this.entityToAttack = null;
                this.entityToAttackUUID = null;
                this.turret.updateState();
            }
            return;
        }

        if( this.shootTicks > 0 ) {
            this.shootTicks--;
        }

        if( this.entityToAttack == null && this.entityToAttackUUID != null ) {
            this.entityToAttack = EntityUtils.getEntityByUUID(turretL.world, this.entityToAttackUUID);
        }

        AxisAlignedBB aabb = this.getAdjustedRange(true);

        if( this.processTicks++ % 10 == 0 ) {
            if( TARGET_BUS.post(new TargetingEvent.ProcessorTick(this)) ) {
                return;
            }
            if( this.entityToAttack == null ) {
                for( Entity entityObj : getValidTargetList(aabb) ) {
                    EntityLivingBase livingBase = (EntityLivingBase) entityObj;
                    if( this.checkTargetListeners(livingBase) ) {
                        this.entityToAttack = livingBase;
                        this.entityToAttackUUID = livingBase.getUniqueID();
                        changed = true;
                        break;
                    }
                }
            }
        }

        if( this.entityToAttack != null ) {
            if( this.isEntityValidTarget(this.entityToAttack, aabb) && this.checkTargetListeners(this.entityToAttack) ) {
                if( this.canShoot() ) {
                    this.setShot(shootProjectile());
                    changed = true;
                } else if( this.initShootTicks > 0 ) {
                    this.decrInitShootTicks();
                }
            } else {
                this.resetInitShootTicks();
                this.entityToAttack = null;
                this.entityToAttackUUID = null;
                changed = true;
            }
        }

        if( changed ) {
            this.turret.updateState();
        }
    }

    @Override
    public boolean isEntityBlacklist() {
        return this.isBlacklistEntity;
    }

    @Override
    public boolean isPlayerBlacklist() {
        return this.isBlacklistPlayer;
    }

    @Override
    public void setEntityBlacklist(boolean isBlacklist) {
        this.isBlacklistEntity = isBlacklist;
    }

    @Override
    public void setPlayerBlacklist(boolean isBlacklist) {
        this.isBlacklistPlayer = isBlacklist;
    }

    @Override
    public boolean isEntityValidTarget(Entity entity) {
        return this.isEntityValidTarget(entity, this.getAdjustedRange(true));
    }

    @Override
    public List<Entity> getValidTargetList() {
        return this.getValidTargetList(this.getAdjustedRange(true));
    }

    @Override
    public boolean isEntityTargeted(Entity entity) {
        Boolean creatureSetting = this.entityTargetList.get(entity.getClass());
        if( creatureSetting != null ) {
            return this.isBlacklistEntity ^ creatureSetting;
        } else if( entity instanceof EntityPlayer ) {
            boolean b = (Boolean.TRUE.equals(this.playerTargetList.get(entity.getUniqueID())) || (Boolean.TRUE.equals(this.playerTargetList.get(UuidUtils.EMPTY_UUID))));
            return this.isBlacklistPlayer ^ b;
        }

        return false;
    }

    private List<Entity> getValidTargetList(AxisAlignedBB aabb) {
        return turret.get().world.getEntitiesInAABBexcluding(turret.get(), aabb, entity -> this.isEntityValidTarget(entity, aabb));
    }

    private boolean isEntityValidTarget(Entity entity, AxisAlignedBB aabb) {
        return entity instanceof EntityLivingBase && isEntityTargeted(entity) && entity.isEntityAlive() && entity.getEntityBoundingBox().intersects(aabb)
               && (this.turret.getTurret().canSeeThroughBlocks() || this.turret.get().canEntityBeSeen(entity));
    }

    public static void initialize() {
        EntityList.getEntityNameList().stream().map(EntityList::getClass)
                .filter(cls -> cls != null && EntityLiving.class.isAssignableFrom(cls) && !ITurretInst.class.isAssignableFrom(cls) && !EntityLiving.class.equals(cls))
                .forEach(cls -> ENTITY_TARGET_LIST_STD.put(cls, IMob.class.isAssignableFrom(cls)));
    }

    @Override
    public ITurretInst getTurret() {
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

        nbt.setBoolean("entityBlacklist", this.isBlacklistEntity);
        nbt.setBoolean("playerBlacklist", this.isBlacklistPlayer);

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
        if( nbt == null ) {
            return;
        }
        this.ammoCount = nbt.getInteger("ammoCount");
        if( nbt.hasKey("ammoStack") ) {
            this.ammoStack = new ItemStack(nbt.getCompoundTag("ammoStack"));
        }
        if( nbt.hasKey("targetUUID") ) {
            this.entityToAttackUUID = UUID.fromString(nbt.getString("targetUUID"));
        }

        this.isBlacklistEntity = nbt.getBoolean("entityBlacklist");
        this.isBlacklistPlayer = nbt.getBoolean("playerBlacklist");

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
                UUID id = UUID.fromString(list.getStringTagAt(i));
                if( id.equals(UuidUtils.EMPTY_UUID) ) {
                    this.isBlacklistPlayer = true;
                } else {
                    playerTgt.add(id);
                }
            } catch( IllegalArgumentException ignored ) { }
        }
        this.updatePlayerTargets(playerTgt.toArray(new UUID[0]));
    }

    @Override
    public List<Class<? extends Entity>> getEnabledEntityTargets() {
        Collection<Class<? extends Entity>> enabledClasses = Maps.filterEntries(this.entityTargetList, input -> input != null && input.getValue()).keySet();

        return new ArrayList<>(enabledClasses);
    }

    @Override
    public UUID[] getEnabledPlayerTargets() {
        Collection<UUID> enabledUUIDs = Maps.filterEntries(this.playerTargetList, input -> input != null && input.getValue()).keySet();

        return enabledUUIDs.toArray(new UUID[0]);
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

    public void updateClientState(int targetId, int ammoCount, @Nonnull ItemStack ammoStack, boolean isShooting) {
        EntityLiving turretL = this.turret.get();
        if( turretL.world.isRemote ) {
            this.entityToAttack = targetId < 0 ? null : turretL.world.getEntityByID(targetId);
            this.ammoCount = ammoCount;
            this.ammoStack = ammoStack;
            this.isShootingClt = isShooting;
        }
    }

    @Override
    public String getTargetName() {
        return this.hasTarget() ? EntityList.getEntityString(this.entityToAttack) : "";
    }
}
