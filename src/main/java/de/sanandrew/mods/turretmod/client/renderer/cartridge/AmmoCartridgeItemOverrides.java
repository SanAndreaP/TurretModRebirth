package de.sanandrew.mods.turretmod.client.renderer.cartridge;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
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
