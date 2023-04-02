/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.datagenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.api.assembly.ICountedIngredient;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.block.BlockRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.TurretRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.Turrets;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.Ammunitions;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKitRegistry;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKits;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AssemblyProvider
        extends RecipeProvider
{
    public AssemblyProvider(DataGenerator generator) {
        super(generator);
    }

    private static ItemStack withCount(ItemStack stack, int count) {
        stack.setCount(count);
        return stack;
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        buildTurrets(consumer);
        buildAmmo(consumer);
        buildUpgrades(consumer);
        buildMisc(consumer);
        buildRepairKits(consumer);
    }

    private static void buildTurrets(@Nonnull Consumer<IFinishedRecipe> consumer) {
        AssemblyBuilder.newAssembly("turrets", TurretRegistry.INSTANCE.getItem(Turrets.CROSSBOW)).energyConsumption(10).processTime(100)
                       .customType("turret_assembly_special_crossbow_turret")
                       .ingredients(new AssemblyBuilder.CompoundIngredientBuilder(12).tag(Tags.Items.COBBLESTONE)
                                                                                     .item(Items.GRANITE)
                                                                                     .item(Items.DIORITE)
                                                                                     .item(Items.ANDESITE).build())
                       .ingredient(1, Items.BOW)
                       .ingredient(4, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(4, ItemTags.PLANKS)
                       .build(consumer);
    }

    private static void buildAmmo(@Nonnull Consumer<IFinishedRecipe> consumer) {
        AssemblyBuilder.newAssembly("ammo", new ItemStack(ItemRegistry.AMMO_CARTRIDGE)).energyConsumption(25).processTime(100)
                       .ingredient(5, Tags.Items.INGOTS_IRON)
                       .ingredient(2, Tags.Items.LEATHER)
                       .ingredient(1, Tags.Items.DUSTS_REDSTONE)
                       .build(consumer);

        newAmmo(consumer, Ammunitions.BOLT, 16, b -> b.ingredient(1, Items.ARROW));
    }

    private static void buildUpgrades(@Nonnull Consumer<IFinishedRecipe> consumer) {
        AssemblyBuilder.newAssembly("upgrades", UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.EMPTY_UPGRADE.getId(), 3))
                       .energyConsumption(80).processTime(400)
                       .ingredient(1, Tags.Items.GLASS_PANES)
                       .ingredient(2, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(1, Tags.Items.INGOTS_GOLD)
                       .ingredient(1, Items.STONE_SLAB)
                       .build(consumer);

        newUpgrade(consumer, Upgrades.AMMO_STORAGE,
                   b -> b.ingredient(1, Items.HOPPER));
        newUpgrade(consumer, Upgrades.ECONOMY_I,
                   b -> b.ingredient(1, Tags.Items.GEMS_EMERALD));
        newUpgrade(consumer, Upgrades.ECONOMY_II,
                   b -> b.ingredient(1, Tags.Items.STORAGE_BLOCKS_GOLD)
                         .ingredient(1, Tags.Items.GEMS_DIAMOND));
        newUpgrade(consumer, Upgrades.ECONOMY_INF,
                   b -> b.ingredients(getInfinityItems()));
//        newUpgrade(consumer, Upgrades.ENDER_MEDIUM,               b -> b.ingredient(1, Tags.Items.ENDER_PEARLS));
        newUpgrade(consumer, Upgrades.ENDER_TOXIN_I,
                   b -> b.ingredients(getWaterItems())
                         .ingredient(1, Tags.Items.SLIMEBALLS));
        newUpgrade(consumer, Upgrades.ENDER_TOXIN_II,
                   b -> b.ingredient(1, new ItemStack(Items.TNT)));
//        newUpgrade(consumer, Upgrades.FUEL_PURIFIER,              b -> b.ingredient(1, ItemTags.SOUL_FIRE_BASE_BLOCKS)
//                                                                        .ingredient(1, Items.MAGMA_CREAM));
        newUpgrade(consumer, Upgrades.HEALTH_I,
                   b -> b.ingredient(8, Items.GLISTERING_MELON_SLICE));
        newUpgrade(consumer, Upgrades.HEALTH_II,
                   b -> b.ingredient(8, Items.GLISTERING_MELON_SLICE)
                         .ingredient(1, Items.GOLDEN_APPLE));
        newUpgrade(consumer, Upgrades.HEALTH_III,
                   b -> b.ingredient(16, Items.GLISTERING_MELON_SLICE)
                         .ingredient(2, Items.GOLDEN_APPLE));
        newUpgrade(consumer, Upgrades.HEALTH_IV,
                   b -> b.ingredient(16, Items.GLISTERING_MELON_SLICE)
                         .ingredient(4, Items.GOLDEN_APPLE));
        newUpgrade(consumer, Upgrades.LEVELING,
                   b -> b.ingredient(1, Items.EXPERIENCE_BOTTLE));
        newUpgrade(consumer, Upgrades.RELOAD_I,
                   b -> b.ingredient(1, Items.ICE));
        newUpgrade(consumer, Upgrades.RELOAD_II,
                   b -> b.ingredient(1, Items.PACKED_ICE));
        newUpgrade(consumer, Upgrades.REMOTE_ACCESS,
                   b -> b.ingredient(1, Items.POPPED_CHORUS_FRUIT));
//        newUpgrade(consumer, Upgrades.SHIELD_COLORIZER,           b -> b.ingredient(1, Tags.Items.DYES_RED)
//                                                                        .ingredient(1, Tags.Items.DYES_GREEN)
//                                                                        .ingredient(1, Tags.Items.DYES_BLUE)
//                                                                        .ingredient(1, Tags.Items.DUSTS_PRISMARINE));
//        newUpgrade(consumer, Upgrades.SHIELD_EXPLOSIVE,           b -> b.ingredients(getResistantItems(Enchantments.BLAST_PROTECTION, 0)));
        newUpgrade(consumer, Upgrades.SHIELD_PERSONAL,
                   b -> b.ingredient(1, Tags.Items.GEMS_QUARTZ)
                         .ingredient(1, Tags.Items.ENDER_PEARLS)
                         .ingredient(1, Tags.Items.DUSTS_REDSTONE));
//        newUpgrade(consumer, Upgrades.SHIELD_PROJECTILE, 20, 600, b -> b.ingredients(getResistantItems(Enchantments.PROJECTILE_PROTECTION)));
//        newUpgrade(consumer, Upgrades.SHIELD_STRENGTH_I,          b -> b.ingredients(getResistantItems(Enchantments.ALL_DAMAGE_PROTECTION, 0)));
//        newUpgrade(consumer, Upgrades.SHIELD_STRENGTH_II,         b -> b.ingredients(getResistantItems(Enchantments.ALL_DAMAGE_PROTECTION, 1)));
        newUpgrade(consumer, Upgrades.SMART_TGT,
                   b -> b.ingredient(1, Items.SPIDER_EYE)
                         .ingredient(1, Tags.Items.ENDER_PEARLS));
        newUpgrade(consumer, Upgrades.UPG_STORAGE_I,
                   b -> b.ingredient(1, Items.TRAPPED_CHEST));
        newUpgrade(consumer, Upgrades.UPG_STORAGE_II,
                   b -> b.ingredient(1, Items.TRAPPED_CHEST)
                         .ingredient(1, Tags.Items.INGOTS_GOLD));
        newUpgrade(consumer, Upgrades.UPG_STORAGE_III,
                   b -> b.ingredient(1, Items.TRAPPED_CHEST)
                         .ingredient(1, Tags.Items.GEMS_DIAMOND));
        newUpgrade(consumer, Upgrades.TURRET_SAFE,
                   b -> b.ingredient(1, BlockRegistry.TURRET_CRATE)
                         .ingredient(2, Tags.Items.DUSTS_REDSTONE));
    }

    private static void buildMisc(@Nonnull Consumer<IFinishedRecipe> consumer) {
        final String cat = "misc";

        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT)).energyConsumption(10).processTime(180)
                       .ingredient(5, Tags.Items.INGOTS_IRON)
                       .ingredient(2, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(1, Tags.Items.GLASS_PANES)
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(BlockRegistry.TURRET_CRATE)).energyConsumption(5).processTime(60)
                       .ingredient(2, Tags.Items.INGOTS_IRON)
                       .ingredient(2, Tags.Items.GEMS_QUARTZ)
                       .ingredient(1, Tags.Items.CHESTS_WOODEN)
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.TURRET_LEXICON)).energyConsumption(7500).processTime(10)
                       .ingredient(1, Items.BOOK)
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.ASSEMBLY_UPG_SPEED)).energyConsumption(60).processTime(300)
                       .ingredient(1, Tags.Items.GEMS_EMERALD)
                       .ingredient(1, Tags.Items.NUGGETS_GOLD)
                       .ingredient(1, Tags.Items.DUSTS_REDSTONE)
                       .ingredients(getPotion(Potions.SWIFTNESS))
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.ASSEMBLY_UPG_AUTO)).energyConsumption(60).processTime(300)
                       .ingredient(1, Tags.Items.GEMS_EMERALD)
                       .ingredient(1, Tags.Items.NUGGETS_GOLD)
                       .ingredient(1, Items.COMPARATOR)
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.ASSEMBLY_UPG_REDSTONE)).energyConsumption(60).processTime(300)
                       .ingredient(1, Tags.Items.GEMS_EMERALD)
                       .ingredient(1, Tags.Items.NUGGETS_GOLD)
                       .ingredient(1, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(1, Items.LEVER)
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, new ItemStack(ItemRegistry.ASSEMBLY_UPG_FILTER)).energyConsumption(60).processTime(300)
                       .ingredient(1, Tags.Items.GEMS_EMERALD)
                       .ingredient(1, Tags.Items.NUGGETS_GOLD)
                       .ingredient(1, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(1, Items.HOPPER)
                       .build(consumer);
    }

    private static void buildRepairKits(@Nonnull Consumer<IFinishedRecipe> consumer) {
        final String cat = "repair_kits";

        AssemblyBuilder.newAssembly(cat, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_1, 3)).energyConsumption(25).processTime(600)
                       .ingredient(2, Tags.Items.LEATHER)
                       .ingredients(getPotion(Potions.HEALING))
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_2)).energyConsumption(25).processTime(600)
                       .ingredient(1, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_1))
                       .ingredients(getPotion(Potions.HEALING))
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_3)).energyConsumption(25).processTime(600)
                       .ingredient(1, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_2))
                       .ingredients(getPotion(Potions.HEALING))
                       .build(consumer);
        AssemblyBuilder.newAssembly(cat, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_4)).energyConsumption(25).processTime(600)
                       .ingredient(1, RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_3))
                       .ingredients(getPotion(Potions.HEALING))
                       .build(consumer);

        AssemblyBuilder.newAssembly(cat, RepairKitRegistry.INSTANCE.getItem(RepairKits.REG_MK_1, 6)).energyConsumption(25).processTime(600)
                       .ingredient(2, Tags.Items.LEATHER)
                       .ingredients(getPotion(Potions.REGENERATION))
                       .build(consumer);
    }

    private static void newAmmo(@Nonnull Consumer<IFinishedRecipe> consumer, IAmmunition ammo, int count, Consumer<AssemblyBuilder> addtBuild) {
        AssemblyBuilder b = AssemblyBuilder.newAssembly("ammo", withCount(AmmunitionRegistry.INSTANCE.getItem(ammo), count)).energyConsumption(5).processTime(60);

        addtBuild.accept(b);
        b.build(consumer);
    }

    private static void newUpgrade(@Nonnull Consumer<IFinishedRecipe> consumer, IUpgrade upgrade, Consumer<AssemblyBuilder> addtBuild) {
        AssemblyBuilder b = AssemblyBuilder.newAssembly("upgrades", UpgradeRegistry.INSTANCE.getItem(upgrade))
                                           .energyConsumption(20).processTime(600)
                                           .ingredient(1, UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.EMPTY_UPGRADE).getItem());
        addtBuild.accept(b);
        b.build(consumer);
    }

    private static ICountedIngredient getInfinityItems() {
        Map<Enchantment, Integer> enchantments = Maps.asMap(Sets.newHashSet(Enchantments.INFINITY_ARROWS), k -> 1);
        ItemStack                 bow          = new ItemStack(Items.BOW);
        ItemStack                 enchBook     = new ItemStack(Items.ENCHANTED_BOOK);

        EnchantmentHelper.setEnchantments(enchantments, bow);
        EnchantmentHelper.setEnchantments(enchantments, enchBook);

        return new AssemblyBuilder.CompoundIngredientBuilder(1).itemNbt(bow).itemNbt(enchBook).build();
    }

    private static ICountedIngredient getWaterItems() {
        return getPotion(Potions.WATER, b -> b.item(Items.WATER_BUCKET));
    }

    private static ICountedIngredient getResistantItems(Enchantment enchantment, int level) {
        List<Item> enchantableItems = new ArrayList<>();
        enchantableItems.addAll(Lists.newArrayList(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS));
        enchantableItems.addAll(Lists.newArrayList(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS));
        enchantableItems.addAll(Lists.newArrayList(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS));
        enchantableItems.addAll(Lists.newArrayList(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS));
        enchantableItems.addAll(Lists.newArrayList(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS));
        enchantableItems.addAll(Lists.newArrayList(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS));
        enchantableItems.add(Items.ENCHANTED_BOOK);

        AssemblyBuilder.CompoundIngredientBuilder b = new AssemblyBuilder.CompoundIngredientBuilder(1);
        for( Item i : enchantableItems ) {
            ItemStack stack = new ItemStack(i);
            EnchantmentHelper.setEnchantments(Maps.asMap(Sets.newHashSet(enchantment), k -> level), stack);
            b.itemNbt(stack);
        }

        return b.build();
    }

    private static ICountedIngredient getPotion(Potion potion) {
        return getPotion(potion, b -> {});
    }

    private static ICountedIngredient getPotion(Potion potion, Consumer<AssemblyBuilder.CompoundIngredientBuilder> whileBuild) {
        ItemStack bottle = new ItemStack(Items.POTION);
        ItemStack linger = new ItemStack(Items.LINGERING_POTION);
        ItemStack splash = new ItemStack(Items.SPLASH_POTION);

        PotionUtils.setPotion(bottle, potion);
        PotionUtils.setPotion(linger, potion);
        PotionUtils.setPotion(splash, potion);

        AssemblyBuilder.CompoundIngredientBuilder builder = new AssemblyBuilder.CompoundIngredientBuilder(1);
        whileBuild.accept(builder);
        return builder.itemNbt(bottle).itemNbt(linger).itemNbt(splash).build();
    }
}
