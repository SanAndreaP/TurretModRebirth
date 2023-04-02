/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.datagenerator;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.Ammunitions;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class TmrItemModelProvider
        extends ItemModelProvider
{
    private static final String LAYER_0 = "layer0";
    private static final String LAYER_1 = "layer1";
    private static final String LAYER_2 = "layer2";

    public TmrItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, TmrConstants.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        final ResourceLocation stdItemParent = new ResourceLocation("item/generated");

        this.registerAmmo(stdItemParent);
        this.registerUpgrades(stdItemParent);
    }

    private void registerAmmo(ResourceLocation stdItemParent) {
        final String basePath = "item/ammo/";
        final String cartridgePath = basePath + "cartridge/";
        final String cartridgeName = Objects.requireNonNull(ItemRegistry.AMMO_CARTRIDGE.getRegistryName()).getPath();

        ResourceLocation cartridgeTexture = new ResourceLocation(TmrConstants.ID, cartridgePath + "cartridge");
        this.singleTexture(cartridgeName, stdItemParent, LAYER_0, cartridgeTexture);

        AmmunitionRegistry.INSTANCE.getAll().stream().filter(a -> a.getId().getNamespace().equals(TmrConstants.ID)).forEach(a -> {
            String name = a.getId().getPath();
            if( a == Ammunitions.TIPPED_BOLT ) {
                this.withExistingParent(name, stdItemParent)
                    .texture(LAYER_0, new ResourceLocation(TmrConstants.ID, basePath + name + "_head"))
                    .texture(LAYER_1, new ResourceLocation(TmrConstants.ID, basePath + name + "_base"));

                this.withExistingParent(cartridgeName + "_" + name, stdItemParent)
                    .texture(LAYER_0, cartridgeTexture)
                    .texture(LAYER_1, new ResourceLocation(TmrConstants.ID, cartridgePath + name + "_head"))
                    .texture(LAYER_2, new ResourceLocation(TmrConstants.ID, cartridgePath + name + "_base"));
            } else {
                this.singleTexture(name, stdItemParent, LAYER_0, new ResourceLocation(TmrConstants.ID, basePath + name));

                this.withExistingParent(cartridgeName + "_" + name, stdItemParent)
                    .texture(LAYER_0, cartridgeTexture)
                    .texture(LAYER_1, new ResourceLocation(TmrConstants.ID, basePath + "cartridge/" + name));
            }
        });
    }

    private void registerUpgrades(ResourceLocation stdItemParent) {
        this.singleTexture("upgrade_base", stdItemParent, LAYER_0, new ResourceLocation(TmrConstants.ID, "item/upgrades/background"));

        ResourceLocation baseModel = new ResourceLocation(TmrConstants.ID, "item/upgrade_base");
        UpgradeRegistry.INSTANCE.getAll().stream().filter(u -> u.getId().getNamespace().equals(TmrConstants.ID) && !u.getId().equals(Upgrades.CREATIVE.getId())).forEach(u -> {
            String name = u.getId().getPath();
            this.singleTexture(name, baseModel, LAYER_1, new ResourceLocation(TmrConstants.ID, "item/upgrades/" + name));
        });
    }
}
