package de.sanandrew.mods.turretmod.client.renderer.color;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class TippedBoltColor
        implements IItemColor
{
    @Override
    public int getColor(@Nonnull ItemStack stack, int tintIndex) {
        if( tintIndex == 0 ) {
            String potionTypeId = AmmunitionRegistry.INSTANCE.getSubtype(stack);
            if( !Strings.isNullOrEmpty(potionTypeId) ) {
                ResourceLocation potionId = new ResourceLocation(potionTypeId);

                return PotionUtils.getColor(MiscUtils.defIfNull(ForgeRegistries.POTION_TYPES.getValue(potionId), Potions.EMPTY));
            }
        }

        return -1;
    }
}
