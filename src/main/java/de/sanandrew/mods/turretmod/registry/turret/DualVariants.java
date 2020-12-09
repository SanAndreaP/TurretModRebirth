package de.sanandrew.mods.turretmod.registry.turret;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class DualVariants
{
    private final Table<Integer, Integer, Entry> variants = TreeBasedTable.create();

    private final String texture;

    DualVariants(String texture) {
        this.texture = texture;
    }

    public void put(int baseId, int frameId, String baseName, String frameName) {
        variants.put(baseId,frameId, new Entry(this.texture, baseId, frameId, baseName, frameName));
    }

    public Entry get(ItemStack baseStack, ItemStack frameStack) {
        return get(checkBase(baseStack), checkFrame(frameStack));
    }

    public Entry get(IInventory inv) {
        int base = -2;
        int frame = -2;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if( base != -1 ) {
                base = checkType(base, checkBase(slotStack));
            }

            if( frame != -1 ) {
                frame = checkType(frame, checkFrame(slotStack));
            }

            if( base == -1 && frame == -1 ) {
                break;
            }
        }

        return get(Math.max(base, 0), Math.max(frame, 0));
    }

    public Entry get(int baseId, int frameId) {
        return variants.contains(baseId, frameId) ? variants.get(baseId, frameId) : variants.get(0, 0);
    }

    public Entry get(int id) {
        return get((id >> 4) & 0b1111, id & 0b1111);
    }

    public abstract int checkBase(ItemStack stack);

    public abstract int checkFrame(ItemStack stack);

    public int checkType(int currType, int newType) {
        if( currType >= 0 && newType >= 0 && currType != newType ) {
            return -1;
        }

        return newType >= 0 ? newType : currType;
    }

    public static class Entry
    {
        public final int              id;
        public final String           suffix;
        public final ResourceLocation texture;

        Entry(String texture, int baseId, int frameId, String baseName, String frameName) {
            this.id = ((baseId << 4) & 0b11110000) | (frameId & 0b1111);
            this.suffix = String.format("%s_%s", baseName, frameName);
            this.texture = new ResourceLocation(String.format(texture, baseName, frameName));
        }
    }
}
