/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.inventory;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoCartridgeItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AmmoCartridgeInventory
        implements IInventory, ICapabilitySerializable<INBT>
{
    @Nonnull
    private final ItemStack holder;
    private long invId;
    private final Integer slotId;
    private static final int SIZE = 27;
    private final NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private boolean loaded = false;

    private AmmoCartridgeInventory(@Nonnull ItemStack holder, Integer slotId) {
        this.holder = holder;
        this.invId = MiscUtils.RNG.randomLong();
        this.slotId = slotId;
    }

    public AmmoCartridgeInventory(@Nonnull ItemStack holder) {
        this(holder, null);
    }

    public AmmoCartridgeInventory(PlayerInventory player, int slotId) {
        this(player.getItem(slotId), slotId);
    }

    @Override
    public int getContainerSize() {
        this.load();
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        this.load();
        return this.stacks.stream().filter(i -> AmmunitionRegistry.INSTANCE.get(i).isValid()).noneMatch(ItemStackUtils::isValid);
    }

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        this.load();
        return this.stacks.get(index);
    }


    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        this.load();
        ItemStack removed = ItemStackHelper.removeItem(this.stacks, index, count);
        this.save();
        return removed;
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        this.load();
        ItemStack orig = this.stacks.get(index);
        this.stacks.set(index, ItemStack.EMPTY);
        this.save();
        return ItemStackUtils.isValid(orig) ? orig : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        this.load();
        this.stacks.set(index, stack);
        this.save();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
        this.save();
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        ItemStack heldItem = MiscUtils.apply(this.slotId, player.inventory::getItem, this.holder);

        return ItemStackUtils.isItem(heldItem, ItemRegistry.AMMO_CARTRIDGE)
               && MiscUtils.apply(AmmoCartridgeItem.getInventory(heldItem), i -> i.invId, ~this.invId) == this.invId;
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        this.load();
        return ItemStackUtils.isValid(stack) && stack.getItem() instanceof AmmoItem && (this.isEmpty() || isTypeEqual(stack));
    }
    
    private boolean isTypeEqual(ItemStack stack) {
        return AmmunitionRegistry.INSTANCE.isEqual(this.getAmmoType(), AmmunitionRegistry.INSTANCE.get(stack))
               && MiscUtils.get(this.getAmmoSubtype(), "").equals(MiscUtils.get(AmmunitionRegistry.INSTANCE.getSubtype(stack), ""));
    }

    @Override
    public void clearContent() {
        this.load();
        this.stacks.clear();
        this.save();
    }

    public int getTotalAmmoCount() {
        this.load();
        return this.stacks.stream().map(ItemStack::getCount).reduce(Integer::sum).orElse(0);
    }

    public IAmmunition getAmmoType() {
        this.load();
        return this.stacks.stream().map(AmmunitionRegistry.INSTANCE::get).filter(IAmmunition::isValid).findFirst()
                          .orElse(AmmunitionRegistry.INSTANCE.getDefault());
    }

    public String getAmmoSubtype() {
        this.load();
        return this.stacks.stream().map(AmmunitionRegistry.INSTANCE::getSubtype).filter(s -> !Strings.isNullOrEmpty(s)).findFirst()
                          .orElse(null);
    }

    public ItemStack getAmmoTypeItem() {
        this.load();
        return this.stacks.stream().filter(i -> AmmunitionRegistry.INSTANCE.get(i).isValid()).findFirst().orElse(ItemStack.EMPTY).copy();
    }

    private void save() {
        if( ItemStackUtils.isValid(this.holder) ) {
            CompoundNBT nbt = this.holder.getOrCreateTagElement("Inventory");
            nbt.put("Items", ItemStackUtils.writeItemStacksToTag(this.stacks, this.getMaxStackSize()));
            nbt.putLong("Id", this.invId);
        }
    }

    private void load() {
        if( !this.loaded && ItemStackUtils.isValid(this.holder) ) {
            this.loaded = true;
            CompoundNBT nbt = this.holder.getTagElement("Inventory");
            if( nbt != null ) {
                if( nbt.contains("Items", Constants.NBT.TAG_LIST) ) {
                    ItemStackUtils.readItemStacksFromTag(this.stacks, nbt.getList("Items", Constants.NBT.TAG_COMPOUND));
                }
                if( nbt.contains("Id", Constants.NBT.TAG_LONG) ) {
                    this.invId = nbt.getLong("Id");
                }
            }
        }
    }

    IItemHandler               handlerCache = null;
    LazyOptional<IItemHandler> lo           = LazyOptional.of(this::getItemHandler);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? ReflectionUtils.getCasted(this.lo) : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(this.getItemHandler(), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(this.getItemHandler(), null, nbt);
    }

    @Nonnull
    public IItemHandler getItemHandler() {
        if( this.handlerCache == null ) {
            this.handlerCache = new InvWrapper(this);
        }

        return this.handlerCache;
    }
}
