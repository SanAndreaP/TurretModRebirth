package de.sanandrew.mods.turretmod.registry.turret;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class DualItemVariants
        extends VariantHolder
{
    private final Table<Long, Long, IVariant> variants = TreeBasedTable.create();

    public void register(ItemStack base, ItemStack frame, IVariant variant) {
        super.register(variant);
        this.variants.put(getIdFromStack(base), getIdFromStack(frame), variant);
    }

    public IVariant get(ItemStack base, ItemStack frame) {
        return get(getIdFromStack(base), getIdFromStack(frame));
    }

    public IVariant get(IInventory inv) {
        long base = -2;
        long frame = -2;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if( base != -1 ) {
                base = checkType(base, getBaseId(slotStack));
            }

            if( frame != -1 ) {
                frame = checkType(frame, getFrameId(slotStack));
            }

            if( base == -1 && frame == -1 ) {
                break;
            }
        }

        return get(Math.max(base, 0), Math.max(frame, 0));
    }

    public IVariant get(long baseHash, long frameHash) {
        return this.variants.get(baseHash, frameHash);
    }

    public long checkType(long currType, long newType) {
        if( currType >= 0L && newType >= 0L && currType != newType ) {
            return -1L;
        }

        return newType >= 0L ? newType : currType;
    }

    protected long getBaseId(ItemStack stack) {
        long id = getIdFromStack(stack);
        if( this.variants.containsRow(id) ) {
            return id;
        }

        return -1L;
    }

    protected long getFrameId(ItemStack stack) {
        long id = getIdFromStack(stack);
        if( this.variants.containsColumn(id) ) {
            return id;
        }

        return -1L;
    }

    protected long getIdFromStack(ItemStack stack) {
        return ((long) (stack.getMetadata() & Integer.MAX_VALUE) << 32) | Objects.hashCode(stack.getItem());
    }

    public IVariant buildVariant(String modId, String textureBase, String baseName, String frameName) {
        ResourceLocation id = new ResourceLocation(modId, String.format("%s_%s", baseName, frameName));
        ResourceLocation texture = new ResourceLocation(modId, String.format(textureBase, baseName, frameName));

        return new Variant(id, texture);
    }
}
