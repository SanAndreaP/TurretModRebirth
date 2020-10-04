package de.sanandrew.mods.turretmod.client.model.item;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

public class ColorTippedBolt
        implements IItemColor
{
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if( tintIndex == 0 ) {
            String potionTypeId = AmmunitionRegistry.INSTANCE.getSubtype(stack);
            if( !Strings.isNullOrEmpty(potionTypeId) ) {
                return PotionUtils.getPotionColor(PotionType.REGISTRY.getObject(new ResourceLocation(potionTypeId)));
            }
        }

        return -1;
    }
}
