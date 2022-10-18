package de.sanandrew.mods.turretmod.api.assembly;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public interface ICountedIngredient
{
    Ingredient getIngredient();

    ItemStack[] getItems();

    JsonElement toJson();

    void toNetwork(PacketBuffer buffer);
}
