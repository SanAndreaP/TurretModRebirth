/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.item.Item;

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
}
