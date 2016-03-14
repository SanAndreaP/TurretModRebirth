/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
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

public class ItemAmmo
        extends Item
{
    @SideOnly(Side.CLIENT)
    private Map<UUID, IIcon> iconMap;

    public ItemAmmo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_ammo");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TurretAmmo type = this.getAmmoType(stack);
        return String.format("%s.%s", super.getUnlocalizedName(stack), type == null ? "unknown" : type.getName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        TurretAmmo type = this.getAmmoType(stack);
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
        List<TurretAmmo> types = AmmoRegistry.INSTANCE.getRegisteredTypes();
        this.iconMap = new HashMap<>(types.size());
        for( TurretAmmo type : types ) {
            this.iconMap.put(type.getUUID(), iconRegister.registerIcon(String.format("%s:ammo/%s", TurretModRebirth.ID, type.getIcon())));
        }
    }

    public TurretAmmo getAmmoType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null ) {
            if( nbt.hasKey("ammoType") ) {
                String typeUUID = nbt.getString("ammoType");
                try {
                    return AmmoRegistry.INSTANCE.getType(UUID.fromString(typeUUID));
                } catch( IllegalArgumentException ex ) {
                    return null;
                }
            }
        }

        return null;
    }

    public ItemStack getAmmoItem(int stackSize, TurretAmmo type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get ammo item with NULL type!");
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("ammoType", type.getUUID().toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for( TurretAmmo type : AmmoRegistry.INSTANCE.getRegisteredTypes() ) {
            list.add(this.getAmmoItem(1, type));
        }
//        super.getSubItems(item, tab, list);
    }
}
