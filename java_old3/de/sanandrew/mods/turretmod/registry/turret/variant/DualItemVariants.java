package de.sanandrew.mods.turretmod.registry.turret.variant;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DualItemVariants
        extends VariantContainer.ItemVariants<Table<Long, Long, IVariant>>
{
    @Override
    public Table<Long, Long, IVariant> buildVariantMap() {
        return TreeBasedTable.create();
    }

    public void register(ItemStack base, ItemStack frame, String textureBase, String baseName, String frameName) {
        IVariant variant = buildVariant(base, frame, TmrConstants.ID, textureBase, baseName, frameName);

        super.register(variant);

        this.variantMap.put(getIdFromStack(base), getIdFromStack(frame), variant);
    }

    public IVariant get(ItemStack base, ItemStack frame) {
        return get(getIdFromStack(base), getIdFromStack(frame));
    }

    @Override
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
        return this.variantMap.get(baseHash, frameHash);
    }

    public long checkType(long currType, long newType) {
        if( currType >= 0L && newType >= 0L && currType != newType ) {
            return -1L;
        }

        return newType >= 0L ? newType : currType;
    }

    protected long getBaseId(ItemStack stack) {
        long id = getIdFromStack(stack);
        if( this.variantMap.containsRow(id) ) {
            return id;
        }

        return -1L;
    }

    protected long getFrameId(ItemStack stack) {
        long id = getIdFromStack(stack);
        if( this.variantMap.containsColumn(id) ) {
            return id;
        }

        return -1L;
    }

    public IVariant buildVariant(ItemStack base, ItemStack frame, String modId, String textureBase, String baseName, String frameName) {
        ResourceLocation id = new ResourceLocation(modId, String.format("%s_%s", baseName, frameName));
        ResourceLocation texture = new ResourceLocation(modId, String.format(textureBase, baseName, frameName));

        return new Variant(id, texture) {
            private final String langKeyBase = base.getTranslationKey() + ".name";
            private final String langKeyFrame = frame.getTranslationKey() + ".name";

            @Override
            public String getTranslatedName() {
                return String.format("%s / %s", LangUtils.translate(langKeyBase), LangUtils.translate(this.langKeyFrame));
            }
        };
    }
}
