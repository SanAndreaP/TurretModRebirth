package de.sanandrew.mods.turretmod.entity.turret.variant;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DualItemVariants
        extends VariantContainer
{
    private final String texture;

    private final List<ResourceLocation> registeredBases = new ArrayList<>();
    private final List<ResourceLocation> registeredFrames = new ArrayList<>();

    public DualItemVariants(String texture) {
        this.texture = texture;
    }

    public void register(ItemStack base, ItemStack frame, String name) {
        IVariant variant = buildVariant(base, frame, name);

        this.registeredBases.add(variant.<DualResourceLocation>getId().base);
        this.registeredFrames.add(variant.<DualResourceLocation>getId().frame);

        super.register(variant);
    }

    public IVariant get(ItemStack base, ItemStack frame) {
        return get(new DualResourceLocation(Objects.requireNonNull(base.getItem().getRegistryName()),
                                            Objects.requireNonNull(frame.getItem().getRegistryName())));
    }

    @Override
    public IVariant get(IInventory inv) {
        boolean checkBase = true;
        boolean checkFrame = true;
        ResourceLocation base = null;
        ResourceLocation frame = null;

        for( int i = 0, max = inv.getContainerSize(); i < max; i++ ) {
            ItemStack slotStack = inv.getItem(i);
            ResourceLocation slotStackId = ItemStackUtils.isValid(slotStack) ? slotStack.getItem().getRegistryName() : null;
            if( slotStackId == null ) {
                continue;
            }

            if( checkBase && this.registeredBases.contains(slotStackId) ) {
                if( base != null && !base.equals(slotStackId) ) {
                    checkBase = false;
                    base = null;
                } else {
                    base = slotStackId;
                }
            }

            if( checkFrame && this.registeredFrames.contains(slotStackId) ) {
                if( frame != null && !frame.equals(slotStackId) ) {
                    checkFrame = false;
                    frame = null;
                } else {
                    frame = slotStackId;
                }
            }

            if( !checkBase && !checkFrame ) {
                break;
            }
        }

        return get(base, frame);
    }

    public IVariant get(ResourceLocation baseId, ResourceLocation frameId) {
        if( baseId == null || frameId == null ) {
            DualResourceLocation def = this.getDefault().getId();

            baseId = baseId == null ? def.base : baseId;
            frameId = frameId == null ? def.frame : baseId;
        }

        return this.get(new DualResourceLocation(baseId, frameId));
    }

    public IVariant buildVariant(ItemStack base, ItemStack frame, String name) {
        ResourceLocation baseId = Objects.requireNonNull(base.getItem().getRegistryName());
        ResourceLocation frameId = Objects.requireNonNull(frame.getItem().getRegistryName());
        DualResourceLocation dualId = new DualResourceLocation(baseId, frameId);

        ResourceLocation textureLocation = new ResourceLocation(String.format(this.texture, name != null ? name : dualId.getForResourceLocation()));

        return new Variant(dualId, textureLocation) {
            private final String langKeyBase = base.getDescriptionId();
            private final String langKeyFrame = frame.getDescriptionId();

            @Override
            public String getTranslatedName() {
                return String.format("%s / %s", LangUtils.translate(langKeyBase), LangUtils.translate(this.langKeyFrame));
            }
        };
    }

    @Override
    public IVariant get(String s) {
        String[] rl = s.split("\\|");
        return rl.length == 2 ? this.get(new ResourceLocation(rl[0]), new ResourceLocation(rl[1])) : this.getDefault();
    }

    public static final class DualResourceLocation
            implements Comparable<DualResourceLocation>
    {
        @Nonnull
        public final ResourceLocation base;
        @Nonnull
        public final ResourceLocation frame;

        public DualResourceLocation(@Nonnull ResourceLocation base, @Nonnull ResourceLocation frame) {
            this.base = base;
            this.frame = frame;
        }

        @Override
        public int compareTo(DualResourceLocation o) {
            return Integer.compare(this.base.compareTo(o.base), this.frame.compareTo(o.frame));
        }

        @Override
        public String toString() {
            return String.format("%s|%s", this.base, this.frame);
        }

        @Override
        public boolean equals(Object o) {
            if( this == o ) {
                return true;
            }

            if( o == null || getClass() != o.getClass() ) {
                return false;
            }

            DualResourceLocation that = (DualResourceLocation) o;

            return this.base.equals(that.base) && this.frame.equals(that.frame);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.base, this.frame);
        }

        public String getForResourceLocation() {
            return String.format("%s_%s_%s_%s", this.base.getNamespace(), this.base.getPath(), this.frame.getNamespace(), this.frame.getPath());
        }
    }
}
