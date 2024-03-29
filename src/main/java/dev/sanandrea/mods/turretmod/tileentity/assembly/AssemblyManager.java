/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.tileentity.assembly;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.ILeveledInventory;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.assembly.IAssemblyManager;
import dev.sanandrea.mods.turretmod.api.assembly.IAssemblyRecipe;
import dev.sanandrea.mods.turretmod.api.assembly.ICountedIngredient;
import dev.sanandrea.mods.turretmod.entity.turret.TurretRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.Turrets;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.Ammunitions;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKitRegistry;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKits;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import dev.sanandrea.mods.turretmod.recipe.AssemblyRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AssemblyManager
        implements IAssemblyManager
{
    public static final IRecipeType<IAssemblyRecipe> TYPE     = IRecipeType.register(TmrConstants.ID + ":turret_assembly");
    public static final AssemblyManager              INSTANCE = new AssemblyManager();

    private final Map<String, ItemStack>                 groupIcons  = new HashMap<>();
    private final Map<String, Integer>                   groupOrders = new HashMap<>();
    private String[] groupsCache;



    @Override
    public void setGroupIcon(String group, ItemStack icon) {
        this.groupIcons.put(group, icon);
    }

    @Override
    public ItemStack getGroupIcon(String group) {
        return this.groupIcons.getOrDefault(group, ItemStack.EMPTY);
    }

    @Override
    public void setGroupOrder(String group, int ordinal) {
        this.groupOrders.put(group, ordinal);
    }

    @Override
    public List<IAssemblyRecipe> getRecipes(World level) {
        return level.getRecipeManager().getAllRecipesFor(TYPE);
    }

    @Override
    public String[] getGroups(World level) {
        if( this.groupsCache == null ) {
            this.groupsCache = this.getRecipes(level).stream().map(IAssemblyRecipe::getGroup).distinct()
                                                              .filter(g -> !Strings.isNullOrEmpty(g))
                                                              .sorted(Comparator.comparingInt(g -> this.groupOrders.getOrDefault(g, 0)))
                                                              .toArray(String[]::new);
        }

        return this.groupsCache;
    }

    public String getPreviousGroup(World level, String currentGroup) {
        String[] groups = this.getGroups(level);
        return IntStream.range(0, groups.length).filter(i -> Objects.equals(groups[i], currentGroup))
                        .mapToObj(i -> i == 0 ? groups[groups.length - 1] : groups[i - 1]).findFirst().orElse("");
    }

    public String getNextGroup(World level, String currentGroup) {
        String[] groups = this.getGroups(level);
        return IntStream.range(0, groups.length).filter(i -> Objects.equals(groups[i], currentGroup))
                        .mapToObj(i -> i == groups.length - 1 ? groups[0] : groups[i + 1]).findFirst().orElse("");
    }

    @Override
    public List<IAssemblyRecipe> getRecipes(World level, String groupName) {
        return this.getRecipes(level).stream().filter(r -> r.getGroup().equals(groupName)).collect(Collectors.toList());
    }

    @Override
    public IAssemblyRecipe getRecipe(World level, ResourceLocation id) {
        return this.getRecipes(level).stream().filter(r -> r.getId().equals(id)).findFirst().orElse(EmptyRecipe.INSTANCE);
    }

    public static void registerGroupValues(IAssemblyManager manager) {
        manager.setGroupOrder("turrets", 0);
        manager.setGroupIcon("turrets", TurretRegistry.INSTANCE.getItem(Turrets.CROSSBOW));
        manager.setGroupOrder("ammo", 1);
        manager.setGroupIcon("ammo", AmmunitionRegistry.INSTANCE.getItem(Ammunitions.BOLT));
        manager.setGroupOrder("upgrades", 2);
        manager.setGroupIcon("upgrades", UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.EMPTY_UPGRADE));
        manager.setGroupOrder("misc", 3);
        manager.setGroupIcon("misc", new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT));
        manager.setGroupOrder("repair_kits", 4);
        manager.setGroupIcon("repair_kits", RepairKitRegistry.INSTANCE.getItem(RepairKits.STD_MK_1));
    }

    private static final class EmptyRecipe
            extends AssemblyRecipe
    {
        static final EmptyRecipe INSTANCE = new EmptyRecipe();

        private EmptyRecipe() {
            super(new ResourceLocation(TmrConstants.ID, "null"), "", NonNullList.create(), 0, Integer.MAX_VALUE, ItemStack.EMPTY);
        }

        @Nonnull @Override public String getGroup() { return ""; }
    }

    @Nullable
    public List<ItemStack> checkAndConsumeResources(ILeveledInventory inv, World world, IAssemblyRecipe recipe, int[] inputSlotIds) {
        if( recipe.canCraftInDimensions(9, 2) && recipe.matches(inv, world) ) {
            List<ItemStack> removedStacks = new ArrayList<>();
            Map<Integer, Integer> modifiedSlots    = new HashMap<>();

            for( ICountedIngredient ing : recipe.getCountedIngredients() ) {
                boolean isSatisfied = false;

                for( ItemStack ingStack : ing.getItems() ) {
                    List<ItemStack>       removedStacksIng = new ArrayList<>();
                    int                   totalAmt         = ingStack.getCount();

                    for( int i = inputSlotIds.length - 1; i >= 0; i-- ) {
                        ItemStack slotStack = inv.getItem(inputSlotIds[i]);
                        if( ItemStackUtils.areEqualNbtFit(slotStack, ingStack, false, false) ) {
                            ItemStack removedStack = slotStack.copy();
                            int       removedAmt   = Math.min(totalAmt, slotStack.getCount());

                            totalAmt -= removedAmt;
                            removedStack.setCount(removedAmt);

                            modifiedSlots.put(inputSlotIds[i], removedAmt);
                            removedStacksIng.add(removedStack);
                        }

                        if( totalAmt == 0 ) {
                            break;
                        }
                    }
                    if( totalAmt == 0 ) {
                        removedStacks.addAll(removedStacksIng);
                        isSatisfied = true;
                        break;
                    }
                }

                if( !isSatisfied ) {
                    return null;
                }
            }
            modifiedSlots.forEach((slotId, reduceAmt) -> inv.getItem(slotId).shrink(reduceAmt));

            return removedStacks;
        }

        return null;
    }
}
