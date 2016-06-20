package de.sanandrew.mods.turretmod.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.medpack.TurretRepairKit;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public class ItemRepairKit
        extends Item
{
    @SideOnly(Side.CLIENT)
    private Map<UUID, IIcon> iconMap;

    public ItemRepairKit() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_repair_kit");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }


    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TurretRepairKit type = this.getRepKitType(stack);
        return String.format("%s.%s", super.getUnlocalizedName(stack), type == null ? "unknown" : type.getName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        TurretRepairKit type = this.getRepKitType(stack);
        if( type != null ) {
            return iconMap.get(type.getUUID());
        }
        return super.getIcon(stack, pass);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        List<TurretRepairKit> types = RepairKitRegistry.INSTANCE.getRegisteredTypes();
        this.iconMap = new HashMap<>(types.size());
        for( TurretRepairKit type : types ) {
            this.iconMap.put(type.getUUID(), iconRegister.registerIcon(String.format("%s:repair_kits/%s", TurretModRebirth.ID, type.getIcon())));
        }
    }

    public TurretRepairKit getRepKitType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null ) {
            if( nbt.hasKey("repKitType") ) {
                String typeUUID = nbt.getString("repKitType");
                try {
                    return RepairKitRegistry.INSTANCE.getRepairKit(UUID.fromString(typeUUID));
                } catch( IllegalArgumentException ex ) {
                    return null;
                }
            }
        }

        return null;
    }

    public ItemStack getRepKitItem(int stackSize, TurretRepairKit type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get ammo item with NULL type!");
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("repKitType", type.getUUID().toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for( TurretRepairKit type : RepairKitRegistry.INSTANCE.getRegisteredTypes() ) {
            list.add(this.getRepKitItem(1, type));
        }
//        super.getSubItems(item, tab, list);
    }
}
