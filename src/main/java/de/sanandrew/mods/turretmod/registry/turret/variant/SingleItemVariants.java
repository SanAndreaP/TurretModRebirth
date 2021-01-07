package de.sanandrew.mods.turretmod.registry.turret.variant;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SingleItemVariants
        extends VariantContainer.ItemVariants<TLongObjectHashMap<IVariant>>
{
    @Override
    public TLongObjectHashMap<IVariant> buildVariantMap() {
        return new TLongObjectHashMap<>();
    }

    public void register(ItemStack base, String texturePath, String name) {
        IVariant v = buildVariant(base, TmrConstants.ID, texturePath, name);

        super.register(v);

        this.variantMap.put(getIdFromStack(base), v);
    }

    public IVariant get(ItemStack base) {
        return get(getIdFromStack(base));
    }

    @Override
    public IVariant get(IInventory inv) {
        long base = -2;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);

            base = checkType(base, getBaseId(slotStack));

            if( base == -1 ) {
                break;
            }
        }

        return get(Math.max(base, 0));
    }

    public IVariant get(long baseHash) {
        return this.variantMap.get(baseHash);
    }

    protected long getBaseId(ItemStack stack) {
        long id = getIdFromStack(stack);
        if( this.variantMap.containsKey(id) ) {
            return id;
        }

        return -1L;
    }

    public IVariant buildVariant(ItemStack stack, String modId, String texturePath, String name) {
        ResourceLocation id = new ResourceLocation(modId, name);
        ResourceLocation texture = new ResourceLocation(modId, String.format(texturePath, name));

        return new Variant(id, texture) {
            private final String itemLangKey = ItemStackUtils.isValid(stack) ? stack.getTranslationKey() : null;

            @Override
            public String getTranslatedName() {
                return this.itemLangKey != null ? LangUtils.translate(this.itemLangKey) : super.getTranslatedName();
            }
        };
    }
}
