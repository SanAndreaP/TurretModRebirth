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
            snbt.putInt("ProcessSlot", i);

            list.add(snbt);
        }

        nbt.put("ProcessStacks", list);

        return nbt;
    }

    public void deserializeProcessStacks(CompoundNBT nbt) {
        if( nbt.contains("ProcessStacks", Constants.NBT.TAG_LIST) ) {
            ListNBT list = nbt.getList("ProcessStacks", Constants.NBT.TAG_COMPOUND);
            for( int i = 0, sz = list.size(); i < sz; i++ ) {
                CompoundNBT snbt = list.getCompound(i);
                this.set(snbt.getInt("ProcessSlot"), new ElectrolyteProcess(ItemStack.of(snbt)));
            }
        }
    }
}
