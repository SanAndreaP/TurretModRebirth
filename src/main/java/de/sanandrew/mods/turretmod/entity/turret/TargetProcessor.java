/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.event.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

public final class TargetProcessor
        implements ITargetProcessor
{
    private final Map<ResourceLocation, Boolean> entityTargetList;
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
        this.entityTargetList = new HashMap<>();
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
        this.initShootTicks = 20;
        this.ammoStack = ItemStackUtils.getEmpty();
    }

    public void init() {
        this.entityTargetList.putAll(TargetList.getStandardTargetList(this.turret.getAttackType()));
    }

    @Override
    public boolean addAmmo(@Nonnull ItemStack stack) {
        return this.addAmmo(stack, null);
    }

    @Override
    public boolean addAmmo(@Nonnull ItemStack stack, ICapabilityProvider excessInv) {
        if( stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN) ) {
            return ItemAmmoCartridge.extractAmmoStacks(stack, this);
        } else if( this.isAmmoApplicable(stack) ) {
            IAmmunition type = AmmunitionRegistry.INSTANCE.getObject(stack);
            IAmmunition currType = AmmunitionRegistry.INSTANCE.getObject(this.ammoStack);

            if( currType.isValid() && !currType.getId().equals(type.getId()) ) {
                if( excessInv != null ) {
                    this.putAmmoInInventory(excessInv);
                } else {
                    this.dropAmmo();
                }
            }

            int maxCapacity = this.getMaxAmmoCapacity() - this.ammoCount;
            if( maxCapacity > 0 ) {
                if( !this.hasAmmo() ) {
                    this.ammoStack = AmmunitionRegistry.INSTANCE.getItem(type.getId());
                } else if( !AmmunitionRegistry.INSTANCE.isEqual(stack, this.ammoStack) ) {
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

    public void setAmmoStackInternal(ItemStack stack) {
        this.ammoStack = stack;
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
                IAmmunition type = AmmunitionRegistry.INSTANCE.getObject(this.ammoStack);
                int maxStackSize = this.ammoStack.getMaxStackSize();

                while( decrAmmo > 0 && type.isValid() ) {
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

    public NonNullList<ItemStack> extractAmmoItems() {
        NonNullList<ItemStack> items = NonNullList.create();
        int maxStackSize = this.ammoStack.getMaxStackSize();
        IAmmunition type = AmmunitionRegistry.INSTANCE.getObject(this.ammoStack);

        while( this.ammoCount > 0 && type.isValid() ) {
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

    public void putAmmoInInventory(ICapabilityProvider inventory) {
        if( this.hasAmmo() ) {
            NonNullList<ItemStack> items = this.extractAmmoItems();

            this.ammoStack = ItemStackUtils.getEmpty();

            if( !items.isEmpty() ) {
                EntityLiving turretL = this.turret.get();
                for( ItemStack stack : items ) {
                    stack = InventoryUtils.addStackToCapability(stack, inventory, EnumFacing.UP, false);
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
            IAmmunition stackType = AmmunitionRegistry.INSTANCE.getObject(stack);
            if( stackType.isValid() ) {
                if( AmmunitionRegistry.INSTANCE.getObject(this.ammoStack).getId().equals(stackType.getId()) ) {
                    return this.ammoCount < this.getMaxAmmoCapacity();
                } else {
                    Collection<IAmmunition> types = AmmunitionRegistry.INSTANCE.getObjects(this.turret.getTurret());
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
            IProjectile proj = AmmunitionRegistry.INSTANCE.getObject(this.ammoStack).getProjectile(this.turret);
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
        if( this.turret.isBuoy() ) {
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
                    if( this.checkTargetListeners(entityObj) ) {
                        this.entityToAttack = entityObj;
                        this.entityToAttackUUID = entityObj.getUniqueID();
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
    public void onTickClient() {
        if( this.entityToAttack != null && !this.entityToAttack.isEntityAlive() ) {
            this.entityToAttackUUID = null;
            this.entityToAttack = null;
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
        Boolean creatureSetting = this.entityTargetList.get(EntityList.getKey(entity.getClass()));
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
        return isEntityTargeted(entity) && entity.isEntityAlive() && entity.getEntityBoundingBox().intersects(aabb)
               && (this.turret.getTurret().canSeeThroughBlocks() || this.turret.get().canEntityBeSeen(entity));
    }

    @Override
    public ITurretInst getTurretInst() {
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
        for( ResourceLocation res : this.getEnabledEntityTargets() ) {
            entityTargets.appendTag(new NBTTagString(res.toString()));
        }
        nbt.setTag("EntityTargetsRL", entityTargets);

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

        if( nbt.hasKey("entityTargets") ) { // @deprecated
            List<Class<? extends Entity>> entityTgt = new ArrayList<>();
            NBTTagList list = nbt.getTagList("entityTargets", Constants.NBT.TAG_STRING);
            for( int i = 0; i < list.tagCount(); i++ ) {
                Class<? extends Entity> cls = ReflectionUtils.getClass(list.getStringTagAt(i));
                if( cls != null ) {
                    entityTgt.add(cls);
                }
            }
            this.updateEntityTargets(entityTgt.stream().map(EntityList::getKey).toArray(ResourceLocation[]::new));
        } else {
            List<ResourceLocation> entityTgt = new ArrayList<>();
            NBTTagList list = nbt.getTagList("EntityTargetsRL", Constants.NBT.TAG_STRING);
            for( int i = 0; i < list.tagCount(); i++ ) {
                entityTgt.add(new ResourceLocation(list.getStringTagAt(i)));
            }
            this.updateEntityTargets(entityTgt.toArray(new ResourceLocation[0]));
        }

        List<UUID> playerTgt = new ArrayList<>();
        NBTTagList list = nbt.getTagList("playerTargets", Constants.NBT.TAG_STRING);
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
    public ResourceLocation[] getEnabledEntityTargets() {
        return this.entityTargetList.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toArray(ResourceLocation[]::new);
    }

    @Override
    public UUID[] getEnabledPlayerTargets() {
        return this.playerTargetList.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toArray(UUID[]::new);
    }

    @Override
    public Map<ResourceLocation, Boolean> getEntityTargets() {
        return new HashMap<>(this.entityTargetList);
    }

    @Override
    public Map<UUID, Boolean> getPlayerTargets() {
        return new HashMap<>(this.playerTargetList);
    }

    @Override
    public void updateEntityTarget(ResourceLocation res, boolean active) {
        if( TargetList.isEntityTargetable(res, this.turret.getAttackType()) ) {
            this.entityTargetList.put(res, active);
        }
    }

    @Override
    public void updatePlayerTarget(UUID uid, boolean active) {
        this.playerTargetList.put(uid, active);
    }

    @Override
    public void updateEntityTargets(ResourceLocation[] keys) {
        this.entityTargetList.entrySet().forEach(entry -> entry.setValue(false));

        Arrays.stream(keys).filter(r -> TargetList.isEntityTargetable(r, this.turret.getAttackType())).forEach(r -> this.entityTargetList.put(r, true));
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
