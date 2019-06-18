package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class TurretCrateInventory
        implements IInventory, INBTSerializable<NBTTagCompound>
{
    private static final int SIZE_UPGRADE_STORAGE = 36;

    private final TileEntityTurretCrate tile;
    private final NonNullList<ItemStack> upgrades = NonNullList.withSize(SIZE_UPGRADE_STORAGE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> ammo = NonNullList.create();
    private ItemStack turretStack = ItemStack.EMPTY;

    public TurretCrateInventory(TileEntityTurretCrate tile) {
        this.tile = tile;
    }

    @Override
    public int getSizeInventory() {
        return SIZE_UPGRADE_STORAGE + 1 + ammo.size();
    }

    @Override
    public boolean isEmpty() {
        return !ItemStackUtils.isValid(this.turretStack) && (this.ammo.size() < 1 || this.ammo.stream().noneMatch(ItemStackUtils::isValid))
                    && this.upgrades.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if( index == 0 ) {
            return this.turretStack;
        } else if( index >= 1 && index <= SIZE_UPGRADE_STORAGE ) {
            return this.upgrades.get(index - 1);
        } else if( index > SIZE_UPGRADE_STORAGE && index - SIZE_UPGRADE_STORAGE <= this.ammo.size() ) {
            return this.ammo.get(index - SIZE_UPGRADE_STORAGE - 1);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.getStackInSlot(index);
        if( ItemStackUtils.isValid(stack) ) {
            ItemStack itemstack;

            if( stack.getCount() <= count ) {
                itemstack = stack;
                this.removeStackFromSlot(index);

                return itemstack;
            } else {
                itemstack = stack.splitStack(count);

                if( stack.getCount() == 0 ) {
                    this.removeStackFromSlot(index);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if( index == 0 ) {
            ItemStack stack = this.turretStack;
            this.turretStack = ItemStack.EMPTY;
            return stack;
        } else if( index >= 1 && index <= SIZE_UPGRADE_STORAGE ) {
            return this.upgrades.set(index - 1, ItemStack.EMPTY);
        } else if( index > SIZE_UPGRADE_STORAGE && index - SIZE_UPGRADE_STORAGE <= this.ammo.size() ) {
            return this.ammo.remove(index - SIZE_UPGRADE_STORAGE - 1);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            this.removeStackFromSlot(index);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        BlockPos tilePos = this.tile.getPos();
        return this.tile.getWorld().getTileEntity(tilePos) == this.tile && player.getDistanceSq(tilePos.getX() + 0.5D, tilePos.getY() + 0.5D, tilePos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) { }

    @Override
    public void closeInventory(EntityPlayer player) { }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
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
        this.turretStack = ItemStack.EMPTY;
        this.upgrades.clear();
        this.ammo.clear();
    }

    @Override
    public String getName() {
        return this.tile.getName();
    }

    @Override
    public boolean hasCustomName() {
        return this.tile.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.tile.getDisplayName();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        ItemStackUtils.writeStackToTag(this.turretStack, nbt, "TurretItem");
        nbt.setTag("InventoryUpgrades", ItemStackUtils.writeItemStacksToTag(this.upgrades, 64));
        nbt.setTag("InventoryAmmo", ItemStackUtils.writeItemStacksToTag(this.ammo, 64));

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.turretStack = new ItemStack(nbt.getCompoundTag("TurretItem"));
        ItemStackUtils.readItemStacksFromTag(this.upgrades, nbt.getTagList("InventoryUpgrades", Constants.NBT.TAG_COMPOUND));

        NBTTagList ammoTag = nbt.getTagList("InventoryAmmo", Constants.NBT.TAG_COMPOUND);
        if( ammoTag.tagCount() > 0 ) {
            this.ammo.addAll(NonNullList.withSize(ammoTag.tagCount(), ItemStack.EMPTY));
            ItemStackUtils.readItemStacksFromTag(this.ammo, ammoTag);
        }
    }
}
