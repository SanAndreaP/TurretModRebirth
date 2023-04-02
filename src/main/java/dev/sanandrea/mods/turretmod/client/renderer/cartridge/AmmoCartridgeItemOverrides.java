/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.cartridge;

import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.inventory.AmmoCartridgeInventory;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoCartridgeItem;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AmmoCartridgeItemOverrides
        extends ItemOverrideList
{
    public static final Map<IAmmunition, IBakedModel> AMMO_MODELS = new HashMap<>();

    @Nullable
    @Override
    public IBakedModel resolve(@Nonnull IBakedModel original, @Nonnull ItemStack stack, @Nullable ClientWorld level, @Nullable LivingEntity entity) {
        AmmoCartridgeInventory inv = AmmoCartridgeItem.getInventory(stack);
        if( inv != null ) {
            IAmmunition ammo = inv.getAmmoType();
            if( ammo.isValid() && AMMO_MODELS.containsKey(ammo) ) {
                return AMMO_MODELS.get(ammo);
            }
        }
        return super.resolve(original, stack, level, entity);
    }
}
