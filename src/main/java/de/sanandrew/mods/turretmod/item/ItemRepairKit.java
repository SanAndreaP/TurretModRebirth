/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.medpack.TurretRepairKit;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemRepairKit
        extends Item
{
    public ItemRepairKit() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_repair_kit");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TurretRepairKit type = RepairKitRegistry.INSTANCE.getType(stack);
        return String.format("%s.%s", this.getUnlocalizedName(), type == null ? "unknown" : type.getName());
    }

    public ItemStack getRepKitItem(int stackSize, UUID typeId) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("repKitType", typeId.toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    public ItemStack getRepKitItem(int stackSize, TurretRepairKit type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret_ammo item with NULL type!");
        }

        return this.getRepKitItem(stackSize, type.getUUID());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.addAll(RepairKitRegistry.INSTANCE.getRegisteredTypes().stream().map(type -> this.getRepKitItem(1, type)).collect(Collectors.toList()));
    }
}
