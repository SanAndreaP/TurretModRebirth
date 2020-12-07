package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class AssemblyCache
        implements IInventory
{
    private NonNullList<ItemStack> stacks;
    private final TileEntityTurretAssembly assembly;
    private final IInventory assemblyInventory;

    public AssemblyCache(TileEntityTurretAssembly assembly, IInventory assemblyInventory) {
        this.assembly = assembly;
        this.assemblyInventory = assemblyInventory;
        this.clear();
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stacks.isEmpty() || this.stacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    public void dropItems() {
        this.stacks.forEach(is -> {
            ItemStack remains = InventoryUtils.addStackToInventory(is, this.assemblyInventory);
            if( ItemStackUtils.isValid(remains) ) {
                World      world = this.assembly.getWorld();
                BlockPos pos = this.assembly.getPos();
                EntityItem item  = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, remains);
                world.spawnEntity(item);
            }
        });

        this.clear();
    }

    public void insert(ItemStack[] stacks) {
        this.stacks = NonNullList.from(ItemStack.EMPTY, stacks);
    }

    public void insert(List<ItemStack> stacks) {
        if( stacks != null && stacks.size() > 0 ) {
            this.insert(stacks.toArray(new ItemStack[0]));
        }
    }

    public NBTTagList getCompound() {
        return ItemStackUtils.writeItemStacksToTag(this.stacks, 64);
    }

    @Override
    public void clear() {
        this.stacks = NonNullList.withSize(0, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) { }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
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
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
