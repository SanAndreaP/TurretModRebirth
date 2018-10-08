/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;

public class ElectrolyteProcess
{
    @Nonnull
    public final ItemStack processStack;
    @Nonnull
    public final ItemStack trashStack;
    @Nonnull
    public final ItemStack treasureStack;
    public final short maxProgress;
    public final float effectivenes;

    private short progress;

    public ElectrolyteProcess(ItemStack stack) {
        this.processStack = stack;

        ElectrolyteRegistry.Fuel fuel = ElectrolyteRegistry.getFuel(stack);
        this.maxProgress = fuel.ticksProc;
        this.effectivenes = fuel.effect;
        this.trashStack = fuel.trash.copy();
        this.treasureStack = fuel.treasure.copy();
    }

    public ElectrolyteProcess(ByteBuf buf) {
        this.processStack = ByteBufUtils.readItemStack(buf);
        this.progress = buf.readShort();
        this.maxProgress = buf.readShort();

        ElectrolyteRegistry.Fuel fuel = ElectrolyteRegistry.getFuel(this.processStack);
        this.effectivenes = fuel.effect;
        this.trashStack = fuel.trash.copy();
        this.treasureStack = fuel.treasure.copy();
    }

    public ElectrolyteProcess(NBTTagCompound nbt) {
        this.processStack = new ItemStack(nbt.getCompoundTag("progressItem"));
        this.progress = nbt.getShort("progress");
        this.maxProgress = nbt.getShort("progressMax");

        ElectrolyteRegistry.Fuel fuel = ElectrolyteRegistry.getFuel(this.processStack);
        this.effectivenes = fuel.effect;
        this.trashStack = fuel.trash.copy();
        this.treasureStack = fuel.treasure.copy();
    }

    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.processStack);
        buf.writeShort(this.progress);
        buf.writeShort(this.maxProgress);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        ItemStackUtils.writeStackToTag(this.processStack, nbt, "progressItem");
        nbt.setShort("progress", this.progress);
        nbt.setShort("progressMax", this.progress);
    }

    public boolean hasTrash() {
        return ItemStackUtils.isValid(this.trashStack);
    }

    public boolean hasTreasure() {
        return ItemStackUtils.isValid(this.treasureStack);
    }

    public int getProgress() {
        return this.progress;
    }

    public void incrProgress() {
        this.progress++;
    }

    public boolean hasFinished() {
        return this.progress >= this.maxProgress;
    }
}
