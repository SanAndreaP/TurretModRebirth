/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.model.item;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.client.model.IListedItemMeshDefinition;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MeshDefAmmoCartridge
        implements IListedItemMeshDefinition
{
    private final Map<ResourceLocation, ModelResourceLocation> ammoToModel = new HashMap<>();

    public MeshDefAmmoCartridge() {
        this.ammoToModel.put(null, new ModelResourceLocation(new ResourceLocation(TmrConstants.ID, "ammo/cartridge"), "inventory"));

        ItemRegistry.TURRET_AMMO.forEach((key, value) -> {
            if( value.ammo.isValid() ) {
                this.ammoToModel.put(key, new ModelResourceLocation(new ResourceLocation(key.getResourceDomain(), "ammo/cartridge." + key.getResourcePath()), "inventory"));
            }
        });
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemAmmoCartridge ) {
            ItemAmmoCartridge.Inventory inv = ItemAmmoCartridge.getInventory(stack);
            IAmmunition storedType = inv != null ? inv.getAmmoType() : AmmunitionRegistry.NULL_TYPE;
            return this.ammoToModel.get(storedType.isValid() ? storedType.getId() : null);
        }

        return this.ammoToModel.get(null);
    }

    @Override
    public ResourceLocation[] getDefinedResources() {
        return this.ammoToModel.values().toArray(new ModelResourceLocation[0]);
    }
}
