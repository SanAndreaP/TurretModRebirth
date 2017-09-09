/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemRepairKit
        extends Item
{
    public ItemRepairKit() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_repair_kit");
        this.setRegistryName(TmrConstants.ID, "repair_kit");
    }

    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        TurretRepairKit type = RepairKitRegistry.INSTANCE.getType(stack);
        return String.format("%s.%s", this.getUnlocalizedName(), type.getName());
    }

    @Nonnull
    public ItemStack getRepKitItem(int stackSize, UUID typeId) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("repKitType", typeId.toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Nonnull
    public ItemStack getRepKitItem(int stackSize, TurretRepairKit type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret_ammo item with NULL type!");
        }

        return this.getRepKitItem(stackSize, type.getUUID());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if( this.isInCreativeTab(tab) ) {
            items.addAll(RepairKitRegistry.INSTANCE.getRegisteredTypes().stream().map(type -> this.getRepKitItem(1, type)).collect(Collectors.toList()));
        }
    }
}
