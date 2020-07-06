package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TippedBoltRecipeWrapper
        implements IShapedCraftingRecipeWrapper
{
    private final List<ItemStack> inputs;
    private final ItemStack output;

    public TippedBoltRecipeWrapper(PotionType type) {
        ItemStack boltStack = AmmunitionRegistry.INSTANCE.getItem(Ammunitions.BOLT.getId());
        ItemStack lingeringPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), type);
        this.inputs = Arrays.asList(
                boltStack, boltStack, boltStack,
                boltStack, lingeringPotion, boltStack,
                boltStack, boltStack, boltStack
        );
        this.output = AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT.getId(), Objects.requireNonNull(type.getRegistryName()).toString());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, this.inputs);
        ingredients.setOutput(ItemStack.class, this.output);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    static List<TippedBoltRecipeWrapper> getRecipes() {
        return PotionType.REGISTRY.getKeys().stream().map(k -> {
            if( k != null ) {
                PotionType potion = PotionType.REGISTRY.getObject(k);
                if( !potion.getEffects().isEmpty() ) {
                    return new TippedBoltRecipeWrapper(potion);
                }
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
