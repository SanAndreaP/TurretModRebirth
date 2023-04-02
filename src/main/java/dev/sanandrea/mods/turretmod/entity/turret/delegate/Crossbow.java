/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.entity.turret.delegate;

import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.IVariant;
import dev.sanandrea.mods.turretmod.api.turret.IVariantHolder;
import dev.sanandrea.mods.turretmod.api.turret.Turret;
import dev.sanandrea.mods.turretmod.entity.turret.variant.DualItemVariants;
import dev.sanandrea.mods.turretmod.init.config.TurretConfig;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class Crossbow
        extends Turret
        implements IVariantHolder
{
    private static final TurretConfig.TurretSpec CFG = TurretConfig.getConfig(Crossbow.class);

    private static final DualItemVariants VARIANT_CONTAINER = new DualItemVariants(Resources.TEXTURE_ENTITY_CROSSBOW_BASE);
    static {
        Map<String, ItemStack> bases = new LinkedHashMap<>();
        bases.put("cobblestone", new ItemStack(Blocks.COBBLESTONE));
        bases.put("mossy_cobblestone", new ItemStack(Blocks.MOSSY_COBBLESTONE));
        bases.put("granite", new ItemStack(Blocks.GRANITE));
        bases.put("diorite", new ItemStack(Blocks.DIORITE));
        bases.put("andesite", new ItemStack(Blocks.ANDESITE));

        Map<String, ItemStack> frames = new LinkedHashMap<>();
        frames.put("oak", new ItemStack(Blocks.OAK_PLANKS));
        frames.put("spruce", new ItemStack(Blocks.SPRUCE_PLANKS));
        frames.put("birch", new ItemStack(Blocks.BIRCH_PLANKS));
        frames.put("jungle", new ItemStack(Blocks.JUNGLE_PLANKS));
        frames.put("acacia", new ItemStack(Blocks.ACACIA_PLANKS));
        frames.put("dark_oak", new ItemStack(Blocks.DARK_OAK_PLANKS));

        for( Map.Entry<String, ItemStack> base : bases.entrySet() ) {
            for( Map.Entry<String, ItemStack> frame : frames.entrySet() ) {
                VARIANT_CONTAINER.register(base.getValue(), frame.getValue(), String.format("%s_%s", base.getKey(), frame.getKey()));
            }
        }
    }

    public Crossbow(ResourceLocation id) {
        super(id, 1, false, TargetType.GROUND, new SoundIds(Resources.SOUND_SHOOT_CROSSBOW).withDefaults());

        this.glowTexture = Resources.TEXTURE_ENTITY_CROSSBOW_GLOW;
    }

    @Override
    public void initializeFromConfig() {
        this.health = Crossbow.CFG.health.get().floatValue();
        this.ammoCapacity = Crossbow.CFG.ammoCapacity.get();
        this.reloadTicks = Crossbow.CFG.reloadTicks.get();
        this.range = Crossbow.CFG.getRange();
    }

    @Override
    public ResourceLocation getBaseTexture(ITurretEntity turret) {
        return turret.getVariant().getTexture();
    }

    @Override
    public IVariant getVariant(Object id) {
        return VARIANT_CONTAINER.get(id);
    }

    @Override
    public IVariant getVariant(IInventory inv) {
        return VARIANT_CONTAINER.get(inv);
    }

    @Override
    public void registerVariant(IVariant variant) {
        VARIANT_CONTAINER.register(variant);
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        return VARIANT_CONTAINER.isDefault(variant);
    }

    @Override
    public IVariant getVariant(String s) {
        return VARIANT_CONTAINER.get(s);
    }

    @Override
    public IVariant[] getVariants() {
        return VARIANT_CONTAINER.getAll();
    }
}
