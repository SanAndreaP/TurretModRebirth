/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ItemAmmo
        extends Item
{
    public final IAmmunition ammo;

    public ItemAmmo(IAmmunition ammo) {
        super();

        this.ammo = ammo;

        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName(ammo.getId());
        this.setTranslationKey(ammo.getId().toString());
    }

    @Override
    public String getTranslationKey() {
        return MiscUtils.defIfNull(this.ammo.getItemTranslationKey(), super::getTranslationKey);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return MiscUtils.defIfNull(this.ammo.getDisplayName(stack), () -> super.getItemStackDisplayName(stack));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        this.ammo.addInformation(stack, world, tooltip, flag);

        if( flag.isAdvanced() ) {
            tooltip.add(TextFormatting.DARK_GRAY + "aid: " + this.ammo.getId().toString());
            String subtype = AmmunitionRegistry.INSTANCE.getSubtype(stack);
            if( !Strings.isNullOrEmpty(subtype) ) {
                tooltip.add(TextFormatting.DARK_GRAY + "subtype: " + subtype);
            }
        }

        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if( this.isInCreativeTab(tab) ) {
            String[] subtypes = this.ammo.getSubtypes();

            if( subtypes != null && subtypes.length > 0 ) {
                Arrays.stream(subtypes).forEach(s -> items.add(AmmunitionRegistry.INSTANCE.setSubtype(new ItemStack(this, 1), s)));
            } else {
                super.getSubItems(tab, items);
            }
        }
    }
}
