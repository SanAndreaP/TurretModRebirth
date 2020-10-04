package de.sanandrew.mods.turretmod.registry.ammo;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TippedBolt
        extends Bolt
{
    private static final String[] SUBTYPES;
    static {
        final List<String> potionTypes = new ArrayList<>();
        PotionType.REGISTRY.forEach(p -> {
            ResourceLocation id = p.getRegistryName();
            if( id != null && !p.getEffects().isEmpty() ) {
                potionTypes.add(id.toString());
            }
        });
        SUBTYPES = potionTypes.toArray(new String[0]);
    }

    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo.tipped_bolt");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String[] getSubtypes() {
        return SUBTYPES;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String potionTypeId = AmmunitionRegistry.INSTANCE.getSubtype(stack);
        if( !Strings.isNullOrEmpty(potionTypeId) ) {
            PotionType potionType = PotionType.REGISTRY.getObject(new ResourceLocation(potionTypeId));
            ItemStack dummyStack = stack.copy();
            PotionUtils.addPotionToItemStack(dummyStack, potionType);
            PotionUtils.addPotionTooltip(dummyStack, tooltip, 0.125F);
        }
    }
}
