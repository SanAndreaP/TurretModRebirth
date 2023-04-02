/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.List;

public class ElectrolyteProcessList
        extends NonNullList<ElectrolyteProcess>
{
    public static final String NBT_PROCESS_STACKS = "ProcessStacks";
    public static final String NBT_PROCESS_SLOT = "ProcessSlot";

    public ElectrolyteProcessList() {
        super(build(), ElectrolyteProcess.EMPTY);
    }

    private static List<ElectrolyteProcess> build() {
        ElectrolyteProcess[] s = new ElectrolyteProcess[ElectrolyteInventory.INPUT_SLOT_COUNT];
        Arrays.fill(s, ElectrolyteProcess.EMPTY);

        return Arrays.asList(s);
    }

    public CompoundNBT serializeProcessStacks(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for( int i = 0; i < ElectrolyteInventory.INPUT_SLOT_COUNT; i++ ) {
            CompoundNBT snbt = new CompoundNBT();
            this.get(i).processStack.save(snbt);
            snbt.putInt(NBT_PROCESS_SLOT, i);

            list.add(snbt);
        }

        nbt.put(NBT_PROCESS_STACKS, list);

        return nbt;
    }

    public void deserializeProcessStacks(CompoundNBT nbt) {
        if( nbt.contains(NBT_PROCESS_STACKS, Constants.NBT.TAG_LIST) ) {
            ListNBT list = nbt.getList(NBT_PROCESS_STACKS, Constants.NBT.TAG_COMPOUND);
            for( int i = 0, sz = list.size(); i < sz; i++ ) {
                CompoundNBT snbt = list.getCompound(i);
                this.set(snbt.getInt(NBT_PROCESS_SLOT), new ElectrolyteProcess(ItemStack.of(snbt)));
            }
        }
    }
}
