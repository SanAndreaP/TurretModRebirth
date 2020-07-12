package de.sanandrew.mods.turretmod.inventory;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AmmoCartridgeInventory
        implements ICapabilityProvider, IInventory
{
    private final ItemStack holder;
    private static final int SIZE = 27;
    private final NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private boolean loaded = false;

    public AmmoCartridgeInventory(ItemStack holder) {
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
        return this.stacks.stream().filter(i -> AmmunitionRegistry.INSTANCE.getObject(i).isValid()).noneMatch(ItemStackUtils::isValid);
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
        return ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemAmmo && (this.isEmpty() || isTypeEqual(stack));
    }
    
    private boolean isTypeEqual(ItemStack stack) {
        return AmmunitionRegistry.INSTANCE.isEqual(this.getAmmoType(), AmmunitionRegistry.INSTANCE.getObject(stack))
               && MiscUtils.defIfNull(this.getAmmoSubtype(), "").equals(MiscUtils.defIfNull(AmmunitionRegistry.INSTANCE.getSubtype(stack), ""));
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

    public int getTotalAmmoCount() {
        this.load();
        return this.stacks.stream().map(ItemStack::getCount).reduce(Integer::sum).orElse(0);
    }

    public IAmmunition getAmmoType() {
        this.load();
        return this.stacks.stream().map(AmmunitionRegistry.INSTANCE::getObject).filter(IAmmunition::isValid).reduce((t1, t2) -> t1)
                          .orElse(AmmunitionRegistry.INSTANCE.getDefaultObject());
    }

    public String getAmmoSubtype() {
        this.load();
        return this.stacks.stream().map(AmmunitionRegistry.INSTANCE::getSubtype).filter(s -> !Strings.isNullOrEmpty(s)).reduce((t1, t2) -> t1)
                          .orElse(null);
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
