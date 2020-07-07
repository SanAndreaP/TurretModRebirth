package de.sanandrew.mods.turretmod.crafting;

import com.google.gson.JsonObject;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeTippedBolt
        extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
        implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int w = inv.getWidth();
        int h = inv.getHeight();

        if( w == 3 && h == 3 ) {
            Item boltItem = AmmunitionRegistry.INSTANCE.getItem(Ammunitions.BOLT.getId()).getItem();
            // MCP thinks rows are columns and columns are rows...
            for( int row = 0; row < w; row++ ) {
                for( int col = 0; col < h; col++ ) {
                    ItemStack itemstack = inv.getStackInRowAndColumn(row, col);

                    if( itemstack.isEmpty() ) {
                        return false;
                    }

                    Item item = itemstack.getItem();

                    if( row == 1 && col == 1 ) {
                        if( item != Items.LINGERING_POTION ) {
                            return false;
                        }
                    } else if( item != boltItem ) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack stack = inv.getStackInRowAndColumn(1, 1);

        if( stack.getItem() == Items.LINGERING_POTION ) {
            ResourceLocation potionId = PotionUtils.getPotionFromItem(stack).getRegistryName();
            if( potionId != null ) {
                ItemStack ammoItem = AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT.getId(), potionId.toString());
                ammoItem.setCount(8);
                return ammoItem;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @SuppressWarnings("unused")
    public static class Factory
            implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            return new RecipeTippedBolt();
        }
    }
}
