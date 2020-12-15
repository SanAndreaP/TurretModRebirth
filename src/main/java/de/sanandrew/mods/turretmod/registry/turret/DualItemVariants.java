package de.sanandrew.mods.turretmod.registry.turret;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import de.sanandrew.mods.turretmod.api.turret.ITurretVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class DualItemVariants
        extends VariantHolder
{
    private final Table<Long, Long, ITurretVariant> variants = TreeBasedTable.create();

    public void register(ItemStack base, ItemStack frame, ITurretVariant variant) {
        super.register(variant);
        this.variants.put(getIdFromStack(base), getIdFromStack(frame), variant);
    }

    public ITurretVariant get(ItemStack baseStack, ItemStack frameStack) {
        return get(getIdFromStack(baseStack), getIdFromStack(frameStack));
    }

    public ITurretVariant get(IInventory inv) {
        long base = -2;
        long frame = -2;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if( base != -1 ) {
                base = checkType(base, getIdFromStack(slotStack));
            }

            if( frame != -1 ) {
                frame = checkType(frame, getIdFromStack(slotStack));
            }

            if( base == -1 && frame == -1 ) {
                break;
            }
        }

        return get(Math.max(base, 0), Math.max(frame, 0));
    }

    public ITurretVariant get(long baseHash, long frameHash) {
        return this.variants.get(baseHash, frameHash);
    }

    public long checkType(long currType, long newType) {
        if( currType >= 0 && newType >= 0 && currType != newType ) {
            return -1;
        }

        return newType >= 0 ? newType : currType;
    }

    protected long getIdFromStack(ItemStack stack) {
        return ((long) (stack.getMetadata() & Integer.MAX_VALUE) << 32) & Objects.hashCode(stack.getItem());
    }

    public ITurretVariant buildVariant(String modId, String textureBase, String baseName, String frameName) {
        ResourceLocation id = new ResourceLocation(modId, String.format("%s_%s", baseName, frameName));
        ResourceLocation texture = new ResourceLocation(modId, String.format(textureBase, baseName, frameName));

        return new Variant(id, texture);
    }
}
