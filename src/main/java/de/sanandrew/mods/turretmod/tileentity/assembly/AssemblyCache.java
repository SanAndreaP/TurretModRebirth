package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class AssemblyCache
        implements IInventory
{
    private NonNullList<ItemStack> stacks;
    private final TurretAssemblyEntity assembly;
    private final IInventory assemblyInventory;

    public AssemblyCache(TurretAssemblyEntity assembly, IInventory assemblyInventory) {
        this.assembly = assembly;
        this.assemblyInventory = assemblyInventory;
        this.clearContent();
    }

    @Override
    public int getContainerSize() {
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
                MiscUtils.accept(this.assembly.getLevel(), level -> {
                    BlockPos   pos  = this.assembly.getBlockPos();
                    ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, remains);
                    level.addFreshEntity(item);
                });
            }
        });

        this.clearContent();
    }

    public void insert(ItemStack[] stacks) {
        this.stacks = NonNullList.of(ItemStack.EMPTY, stacks);
    }

    public void insert(List<ItemStack> stacks) {
        if( stacks != null && !stacks.isEmpty() ) {
            this.insert(stacks.toArray(new ItemStack[0]));
        }
    }

    @Override
    public void clearContent() {
        this.stacks = NonNullList.withSize(0, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) { }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("Items", ItemStackUtils.writeItemStacksToTag(this.stacks, 64));

        return tag;
    }

    void deserializeNBT(CompoundNBT tag) {
        ItemStackUtils.readItemStacksFromTag(this.stacks, tag.getList("Items", Constants.NBT.TAG_COMPOUND));
    }
}
