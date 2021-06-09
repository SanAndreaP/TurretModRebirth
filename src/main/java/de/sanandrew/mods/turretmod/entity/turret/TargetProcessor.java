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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TargetingEvent;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.entity.projectile.TurretProjectileEntity;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TargetProcessor
        implements ITargetProcessor
{
    private final Map<ResourceLocation, Boolean> entityTargetList;
    private final Map<UUID, Boolean>             playerTargetList;
    private final ITurretInst                    turret;

    private int       ammoCount;
    @Nonnull
    private ItemStack ammoStack;
    private int       shootTicks;
    private int       initShootTicks;
    private Entity  entityToAttack;
    @Nonnull
    private UUID    entityToAttackID;
    private boolean isShootingClt;
    private boolean isEntityTargetListDenying = false;
    private boolean isPlayerTargetListDenying = false;

    private long processTicks = 0;

    TargetProcessor(ITurretInst turret) {
        this.entityTargetList = new HashMap<>();
        this.playerTargetList = new HashMap<>();
        this.turret = turret;
        this.initShootTicks = 20;
        this.ammoStack = ItemStack.EMPTY;
        this.entityToAttackID = UuidUtils.EMPTY_UUID;
    }

    public void init() {
        this.entityTargetList.putAll(TargetList.getStandardTargetList(this.turret.getAttackType()));
        this.playerTargetList.putAll(PlayerList.INSTANCE.getDefaultPlayerList());
    }

    @Override
    public boolean addAmmo(@Nonnull ItemStack stack) {
        return this.addAmmo(stack, null);
    }

    //TODO: reimplement ammo
    @Override
    public boolean addAmmo(@Nonnull ItemStack stack, ICapabilityProvider excessInv) {
        if( stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent() ) {
//            return ItemAmmoCartridge.extractAmmoStacks(stack, this, true);
        } else if( this.isAmmoApplicable(stack) ) {
            IAmmunition type    = AmmunitionRegistry.INSTANCE.get(stack);
            String      subtype = MiscUtils.defIfNull(AmmunitionRegistry.INSTANCE.getSubtype(stack), "");

            if( !this.isAmmoTypeEqual(type, subtype) ) {
                if( excessInv != null ) {
                    this.putAmmoInInventory(excessInv);
                } else {
                    this.dropAmmo();
                }
            }

            int maxCapacity = this.getMaxAmmoCapacity() - this.ammoCount;
            if( maxCapacity > 0 ) {
                if( !this.hasAmmo() ) {
                    this.ammoStack = AmmunitionRegistry.INSTANCE.getItem(type.getId(), subtype);
                } else if( !AmmunitionRegistry.INSTANCE.isEqual(stack, this.ammoStack) ) {
                    return false;
                }

                int provided = type.getAmmoCapacity();
                int providedStack = stack.getCount() * provided; //provides 4*16=64, needs 56 = 56 / 64 * 4
                if( providedStack - maxCapacity > 0 ) {
                    int stackSub = MathHelper.floor(maxCapacity / (double) providedStack * stack.getCount());
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

    public void setAmmoStackInternal(ItemStack stack) {
        this.ammoStack = stack;
    }

    public void setAmmoStackInternal(ItemStack stack, int count) {
        this.ammoStack = stack;
        this.ammoCount = count;
    }

    @Override
    public boolean hasAmmo() {
        return ItemStackUtils.isValid(this.ammoStack) && this.ammoCount > 0;
    }

    //TODO: reimplement ammo
    @Override
    public void dropExcessAmmo() {
        if( this.hasAmmo() ) {
            int decrAmmo = this.ammoCount - this.getMaxAmmoCapacity();
            if( decrAmmo > 0 ) {
                NonNullList<ItemStack> items = NonNullList.create();
                IAmmunition type = AmmunitionRegistry.INSTANCE.get(this.ammoStack);
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

    //TODO: reimplement ammo
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

    //TODO: reimplement ammo
    public NonNullList<ItemStack> extractAmmoItems() {
        NonNullList<ItemStack> items = NonNullList.create();
        int maxStackSize = this.ammoStack.getMaxStackSize();
        IAmmunition type = AmmunitionRegistry.INSTANCE.get(this.ammoStack);

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
            LivingEntity turretL = this.turret.get();
            stacks.forEach(stack -> {
                ItemEntity item = new ItemEntity(turretL.level, turretL.getX(), turretL.getY(), turretL.getZ(), stack);
                turretL.level.addFreshEntity(item);
            });
        }
    }

    void dropAmmo() {
        if( this.hasAmmo() ) {
            NonNullList<ItemStack> items = this.extractAmmoItems();
            this.ammoStack = ItemStack.EMPTY;
            this.spawnItemEntities(items);
        }
    }

    @Override
    public void putAmmoInInventory(ICapabilityProvider inventory) {
        if( this.hasAmmo() ) {
            NonNullList<ItemStack> items = this.extractAmmoItems();

            this.ammoStack = ItemStack.EMPTY;

            if( !items.isEmpty() ) {
                LivingEntity turretL = this.turret.get();
                for( ItemStack stack : items ) {
                    stack = InventoryUtils.addStackToCapability(stack, inventory, Direction.UP, false);
                    if( ItemStackUtils.isValid(stack) ) {
                        ItemEntity item = new ItemEntity(turretL.level, turretL.getX(), turretL.getY(), turretL.getZ(), stack);
                        turretL.level.addFreshEntity(item);
                    }
                }
            }
        }
    }

    @Override
    public boolean isAmmoApplicable(@Nonnull ItemStack stack) {
        return getAmmoApplyType(stack) != ApplyType.NOT_COMPATIBLE;
    }

    //TODO: reimplement ammo
    @Override
    public ApplyType getAmmoApplyType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) ) {
            IAmmunition stackType = AmmunitionRegistry.INSTANCE.get(stack);
            if( stackType.isValid() ) {
                if( this.isAmmoTypeEqual(stackType, AmmunitionRegistry.INSTANCE.getSubtype(stack)) ) {
                    return this.ammoCount < this.getMaxAmmoCapacity() ? ApplyType.ADD : ApplyType.NOT_COMPATIBLE;
                } else {
                    Collection<IAmmunition> types = AmmunitionRegistry.INSTANCE.getAll(this.turret.getTurret());
                    return types.contains(stackType) ? ApplyType.REPLACE : ApplyType.NOT_COMPATIBLE;
                }
            }
        }

        return ApplyType.NOT_COMPATIBLE;
    }

    //TODO: reimplement ammo
    private boolean isAmmoTypeEqual(IAmmunition ammo, String subtype) {
        subtype = subtype != null ? subtype : "";
        IAmmunition currType = AmmunitionRegistry.INSTANCE.get(this.ammoStack);
        String currSubtype = MiscUtils.defIfNull(AmmunitionRegistry.INSTANCE.getSubtype(this.ammoStack), "");

        return currType.getId().equals(ammo.getId()) && subtype.equals(currSubtype);
    }

    @Override
    public final int getMaxAmmoCapacity() {
        return MathHelper.ceil(this.turret.get().getAttributeValue(TurretAttributes.MAX_AMMO_CAPACITY));
    }

    @Override
    public final int getMaxShootTicks() {
        return MathHelper.ceil(this.turret.get().getAttributeValue(TurretAttributes.MAX_RELOAD_TICKS));
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
        return (int) Math.round(this.turret.get().getAttributeValue(TurretAttributes.MAX_INIT_SHOOT_TICKS));
    }

    //TODO: reimplement ammo & projectiles
    @Override
    public Entity getProjectile() {
        if( this.hasAmmo() ) {
            IAmmunition ammo = AmmunitionRegistry.INSTANCE.get(this.ammoStack);
            String      ammoSubtype = AmmunitionRegistry.INSTANCE.getSubtype(this.ammoStack);
            IProjectile proj        = ammo.getProjectile(this.turret);
            if( proj != null ) {
                float attackModifier = (float) this.turret.get().getAttributeValue(Attributes.ATTACK_DAMAGE);
                TurretProjectileEntity projectileEntity = new TurretProjectileEntity(this.turret.get().level, proj, ammo, ammoSubtype, attackModifier);
                if( this.entityToAttack != null ) {
                    projectileEntity.shoot(this.turret.get(), this.entityToAttack);
                } else {
                    projectileEntity.shoot(this.turret.get(), this.turret.get().getLookAngle());
                }

                return projectileEntity;
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
        LivingEntity turretL = this.turret.get();
        return doOffset ? aabb.move(turretL.getX(), turretL.getY(), turretL.getZ()) : aabb;
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
            this.turret.get().level.addFreshEntity(projectile);
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
        LivingEntity turretL = this.turret.get();
        final float pitch = 1.0F / (turretL.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F;
        turretL.level.playSound(null, turretL.getX(), turretL.getY(), turretL.getZ(), sound, SoundCategory.NEUTRAL, volume, pitch);
    }

    @Override
    public void onTick() {
        boolean changed = false;
        LivingEntity turretL = this.turret.get();

        if( !this.turret.isActive() ) {
            if( this.entityToAttack != null || this.entityToAttackID.equals(UuidUtils.EMPTY_UUID) ) {
                this.resetInitShootTicks();
                this.entityToAttack = null;
                this.entityToAttackID = UuidUtils.EMPTY_UUID;
                this.turret.updateState();
            }
            return;
        }

        if( this.shootTicks > 0 ) {
            this.shootTicks--;
        }

        if( this.entityToAttack == null && !this.entityToAttackID.equals(UuidUtils.EMPTY_UUID) ) {
            this.entityToAttack = EntityUtils.getEntityByUUID(turretL.level, this.entityToAttackID);
        }

        AxisAlignedBB aabb = this.getAdjustedRange(true);

        if( TARGET_BUS.post(new TargetingEvent.ProcessorTick(this, this.processTicks)) ) {
            return;
        }

        if( this.processTicks++ % 10 == 0 ) {
            if( this.entityToAttack == null ) {
                for( Entity entityObj : getValidTargetList(aabb) ) {
                    if( this.checkTargetListeners(entityObj) ) {
                        this.entityToAttack = entityObj;
                        this.entityToAttackID = entityObj.getUUID();
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
                this.entityToAttackID = UuidUtils.EMPTY_UUID;
                changed = true;
            }
        }

        if( changed ) {
            this.turret.updateState();
        }
    }

    @Override
    public void onTickClient() {
        if( this.entityToAttack != null && !this.entityToAttack.isAlive() ) {
            this.entityToAttackID = UuidUtils.EMPTY_UUID;
            this.entityToAttack = null;
        }
    }

    @Override
    public boolean isEntityBlacklist() {
        return this.isEntityTargetListDenying;
    }

    @Override
    public boolean isPlayerBlacklist() {
        return this.isPlayerTargetListDenying;
    }

    @Override
    public void setEntityBlacklist(boolean isBlacklist) {
        this.isEntityTargetListDenying = isBlacklist;
    }

    @Override
    public void setPlayerBlacklist(boolean isBlacklist) {
        this.isPlayerTargetListDenying = isBlacklist;
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
        if( entity instanceof PlayerEntity ) {
            UUID id = entity.getUUID();
            return this.playerTargetList.containsKey(id) && (this.isPlayerTargetListDenying ^ this.isPlayerTargeted(id));
        } else {
            ResourceLocation id = entity.getType().getRegistryName();
            return this.entityTargetList.containsKey(id) && (this.isEntityTargetListDenying ^ this.isEntityTargeted(id));
        }
    }

    private List<Entity> getValidTargetList(AxisAlignedBB aabb) {
        return turret.get().level.getEntities(turret.get(), aabb, entity -> this.isEntityValidTarget(entity, aabb));
    }

    private boolean isEntityValidTarget(Entity entity, AxisAlignedBB aabb) {
        return isEntityTargeted(entity) && entity.isAlive() && entity.getBoundingBox().intersects(aabb)
               && (this.turret.getTurret().canSeeThroughBlocks() || this.turret.get().canSee(entity));
    }

    @Override
    public boolean isPlayerTargeted(UUID id) {
        return Boolean.TRUE.equals(this.playerTargetList.get(id));
    }

    @Override
    public boolean isEntityTargeted(ResourceLocation id) {
        return Boolean.TRUE.equals(this.entityTargetList.get(id));
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

    public static final String NBT_AMMO_COUNT         = "AmmoCount";
    public static final String NBT_AMMO_STACK         = "AmmoStack";
    public static final String NBT_TARGET_ID          = "TargetID";
    public static final String NBT_ENTITYTGTLIST_DENY = "EntityTargetListDeny";
    public static final String NBT_PLAYERTGTLIST_DENY = "PlayerTargetListDeny";
    public static final String NBT_ENTITYTGTLIST      = "EntityTargetList";
    public static final String NBT_PLAYERTGTLIST      = "PlayerTargetList";
    public static final String NBT_TGTLIST_ID         = "ID";
    public static final String NBT_TGTLIST_ENABLED    = "Enabled";

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt(NBT_AMMO_COUNT, this.ammoCount);
        nbt.put(NBT_AMMO_STACK, this.ammoStack.save(new CompoundNBT()));
        nbt.putUUID(NBT_TARGET_ID, this.entityToAttackID);

        nbt.putBoolean(NBT_ENTITYTGTLIST_DENY, this.isEntityTargetListDenying);
        nbt.put(NBT_ENTITYTGTLIST, new ListNBT() {{
            TargetProcessor.this.getEntityTargets().forEach((id, enabled) -> this.add(new CompoundNBT() {{
                this.putString(NBT_TGTLIST_ID, id.toString());
                this.putBoolean(NBT_TGTLIST_ENABLED, enabled);
            }}));
        }});

        nbt.putBoolean(NBT_PLAYERTGTLIST_DENY, this.isPlayerTargetListDenying);
        nbt.put(NBT_PLAYERTGTLIST, new ListNBT() {{
            TargetProcessor.this.getPlayerTargets().forEach((id, enabled) -> this.add(new CompoundNBT() {{
                this.putUUID(NBT_TGTLIST_ID, id);
                this.putBoolean(NBT_TGTLIST_ENABLED, enabled);
            }}));
        }});

        return nbt;
    }

    @Override
    public void load(CompoundNBT nbt) {
        if( nbt == null ) {
            return;
        }

        this.ammoCount = nbt.getInt(NBT_AMMO_COUNT);
        this.ammoStack = ItemStack.of(nbt.getCompound(NBT_AMMO_STACK));
        this.entityToAttackID = nbt.getUUID(NBT_TARGET_ID);

        this.isEntityTargetListDenying = nbt.getBoolean(NBT_ENTITYTGTLIST_DENY);
        ListNBT entityTargets = nbt.getList(NBT_ENTITYTGTLIST, Constants.NBT.TAG_COMPOUND);
        for( int i = 0, max = entityTargets.size(); i < max; i++ ) {
            CompoundNBT entry = entityTargets.getCompound(i);
            this.updateEntityTarget(new ResourceLocation(entry.getString(NBT_TGTLIST_ID)), entry.getBoolean(NBT_TGTLIST_ENABLED));
        }

        this.isPlayerTargetListDenying = nbt.getBoolean(NBT_PLAYERTGTLIST_DENY);
        ListNBT playerTargets = nbt.getList(NBT_PLAYERTGTLIST, Constants.NBT.TAG_COMPOUND);
        for( int i = 0, max = playerTargets.size(); i < max; i++ ) {
            CompoundNBT entry = playerTargets.getCompound(i);
            this.updatePlayerTarget(entry.getUUID(NBT_TGTLIST_ID), entry.getBoolean(NBT_TGTLIST_ENABLED));
        }
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
        LivingEntity turretL = this.turret.get();
        if( turretL.level.isClientSide ) {
            this.entityToAttack = targetId < 0 ? null : turretL.level.getEntity(targetId);
            this.ammoCount = ammoCount;
            this.ammoStack = ammoStack;
            this.isShootingClt = isShooting;
        }
    }

    @Override
    public ITextComponent getTargetName() {
        return this.hasTarget() ? this.entityToAttack.getName() : StringTextComponent.EMPTY;
    }
}
