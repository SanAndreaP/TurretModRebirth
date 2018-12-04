/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemAmmoCartridge
        extends Item
{
    ItemAmmoCartridge() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName(TmrConstants.ID, "ammo.cartridge");
        this.setUnlocalizedName(TmrConstants.ID + ":ammo.cartridge");
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        super.getSubItems(tab, list);
        if( this.isInCreativeTab(tab) ) {
            AmmunitionRegistry.INSTANCE.getTypes().forEach(t -> {
                if( t.isValid() ) {
                    ItemStack typeStack = AmmunitionRegistry.INSTANCE.getItem(t.getId());
                    typeStack.setCount(typeStack.getMaxStackSize());
                    ItemStack filled = new ItemStack(this, 1);
                    IInventory inv = getInventory(filled);
                    if( inv != null ) {
                        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
                            inv.setInventorySlotContents(i, typeStack.copy());
                        }
                        list.add(filled);
                    }
                }
            });
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Inventory inv = getInventory(stack);
        if( inv != null && !inv.isEmpty() ) {
            tooltip.add("Stored: " + inv.getTotalAmmoCount() + "x " + AmmunitionRegistry.INSTANCE.getItem(inv.getAmmoType().getId()).getDisplayName());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static Inventory getInventory(ItemStack item) {
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if( itemHandler instanceof InvWrapper ) {
            IInventory inv = ((InvWrapper) itemHandler).getInv();
            if( inv instanceof Inventory ) {
                return (Inventory) inv;
            }
        }

        return null;
    }

    public static boolean extractAmmoStacks(ItemStack item, ITargetProcessor processor) {
        boolean success = false;
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if( itemHandler != null ) {
            for( int i = 0, max = itemHandler.getSlots(); i < max; i++ ) {
                ItemStack invStack = itemHandler.getStackInSlot(i);
                if( ItemStackUtils.isValid(invStack) && invStack.getItem() instanceof ItemAmmo ) {
                    if( processor.isAmmoApplicable(invStack) ) {
                        ItemStack copyInvStack = invStack.copy();
                        if( processor.addAmmo(copyInvStack, item) ) {
                            success = true;
                            itemHandler.extractItem(i, invStack.getCount() - copyInvStack.getCount(), false);
                        }
                    }
                }
            }
        }

        return success;
    }

    public static final class Inventory
            implements ICapabilityProvider, IInventory
    {
        private final ItemStack holder;
        private static final int SIZE = 27;
        private final NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        private boolean loaded = false;

        public Inventory(ItemStack holder) {
            this.holder = holder;
        }

        @Override
        public int getSizeInventory() {
            this.load();
            return SIZE;
        }

        @Override
        public boolean isEmpty() {
            this.load();
            return this.stacks.stream().filter(i -> AmmunitionRegistry.INSTANCE.getType(i).isValid()).noneMatch(ItemStackUtils::isValid);
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            this.load();
            return this.stacks.get(index);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            this.load();
            ItemStack removed = ItemStackHelper.getAndSplit(this.stacks, index, count);
            this.save();
            return removed;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            this.load();
            ItemStack orig = this.stacks.get(index);
            this.stacks.set(index, ItemStack.EMPTY);
            this.save();
            return ItemStackUtils.isValid(orig) ? orig : ItemStack.EMPTY;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            this.load();
            this.stacks.set(index, stack);
            this.save();
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {
            this.save();
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) { }

        @Override
        public void closeInventory(EntityPlayer player) { }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            this.load();
            return ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemAmmo
                   && (this.isEmpty() || AmmunitionRegistry.INSTANCE.isEqual(this.getAmmoType(), AmmunitionRegistry.INSTANCE.getType(stack)));
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) { }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {
            this.load();
            this.stacks.clear();
            this.save();
        }

        @Override
        public String getName() {
            return this.holder.getDisplayName();
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new TextComponentString(this.holder.getDisplayName());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) new InvWrapper(this) : null;
        }

        int getTotalAmmoCount() {
            this.load();
            return this.stacks.stream().map(ItemStack::getCount).reduce(Integer::sum).orElse(0);
        }

        public IAmmunition getAmmoType() {
            this.load();
            return this.stacks.stream().map(AmmunitionRegistry.INSTANCE::getType).filter(IAmmunition::isValid).reduce((t1, t2) -> t1).orElse(AmmunitionRegistry.NULL_TYPE);
        }

        private void save() {
            NBTTagCompound nbt = this.holder.getOrCreateSubCompound("Inventory");
            nbt.setTag("Items", ItemStackUtils.writeItemStacksToTag(this.stacks, this.getInventoryStackLimit()));
        }

        private void load() {
            if( !this.loaded ) {
                this.loaded = true;
                NBTTagCompound nbt = this.holder.getSubCompound("Inventory");
                if( nbt != null && nbt.hasKey("Items", Constants.NBT.TAG_LIST) ) {
                    ItemStackUtils.readItemStacksFromTag(this.stacks, nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND));
                }
            }
        }
    }
}
