/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        this.ammo.addInformation(stack, worldIn, tooltip, flagIn);

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
