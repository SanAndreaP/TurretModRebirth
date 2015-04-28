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
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgradeRegistry;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgrade;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTurretUpgrade
        extends Item
{
    @SideOnly(Side.CLIENT)
    public Map<TurretUpgrade, IIcon> upgIcons;
    @SideOnly(Side.CLIENT)
    public IIcon baseIcon;

    public ItemTurretUpgrade() {
        super();
        this.setUnlocalizedName(TurretMod.MOD_ID + ":turretUpgrade");
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack);
    }



    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        List<TurretUpgrade> upgrades = TurretUpgradeRegistry.getRegisteredUpgrades();
        this.upgIcons = new HashMap<>(upgrades.size());

        this.baseIcon = iconRegister.registerIcon(TurretMod.MOD_ID + ":upgrades/empty");
        for( TurretUpgrade upgrade : upgrades ) {
            this.upgIcons.put(upgrade, iconRegister.registerIcon(upgrade.getItemTextureLoc()));
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        TurretUpgrade upgrade = this.getUpgradeFromStack(stack);
        return pass == 0 || upgrade == null ? this.baseIcon : this.upgIcons.get(upgrade);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        List<TurretUpgrade> upgrades = TurretUpgradeRegistry.getRegisteredUpgrades();
        items.add(new ItemStack(this, 1));
        for( TurretUpgrade upgrade : upgrades ) {
            items.add(this.getStackWithUpgrade(upgrade, 1));
        }
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    public ItemStack getStackWithUpgrade(TurretUpgrade upgrade, int stackSize) {
        ItemStack stack = new ItemStack(this, stackSize);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("turretUpgrade", upgrade.getRegistrationName());
        stack.setTagCompound(nbt);
        return stack;
    }

    public TurretUpgrade getUpgradeFromStack(ItemStack stack) {
        if( stack.getItem() == this && stack.hasTagCompound() ) {
            String turretUpgName = stack.getTagCompound().getString("turretUpgrade");
            if( !turretUpgName.isEmpty() ) {
                return TurretUpgradeRegistry.getUpgrade(turretUpgName);
            }
        }

        return null;
    }
}
