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
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretInfo;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.TurretRegistry;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTurretUpgrade
        extends Item
{
    @SideOnly(Side.CLIENT)
    public Map<TurretUpgrade, IIcon> upgIcons;

    public ItemTurretUpgrade() {
        super();
        this.setUnlocalizedName(TurretMod.MOD_ID + ":turretUpgrade");
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        List<TurretUpgrade> upgrades = TurretUpgradeRegistry.getAllUpgradesSorted();
        this.upgIcons = new HashMap<>(upgrades.size());

        this.itemIcon = iconRegister.registerIcon(TurretMod.MOD_ID + ":upgrades/empty");
        for( TurretUpgrade upgrade : upgrades ) {
            this.upgIcons.put(upgrade, iconRegister.registerIcon(TurretUpgradeRegistry.getItemTextureLoc(upgrade)));
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        TurretUpgrade upgrade = this.getUpgradeFromStack(stack);
        return pass == 0 || upgrade == null ? this.itemIcon : this.upgIcons.get(upgrade);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        List<TurretUpgrade> upgrades = TurretUpgradeRegistry.getAllUpgradesSorted();
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

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advTooltip) {
        TurretUpgrade stackUpgrade = this.getUpgradeFromStack(stack);
        String unlocName = this.getUnlocalizedName(stack);
        String upgName = stackUpgrade == null ? "empty" : stackUpgrade.getName();

        lines.add(EnumChatFormatting.AQUA + SAPUtils.translatePreFormat("%s.%s.name", unlocName, upgName));

        if( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ) {
            for( Object line : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(SAPUtils.translatePreFormat("%s.%s.desc", unlocName, upgName), 200) ) {
                lines.add(line);
            }

            if( stackUpgrade != null ) {
                if( stackUpgrade.getDependantOn() != null ) {
                    lines.add(EnumChatFormatting.YELLOW + SAPUtils.translatePreFormat("%s.requires", unlocName));
                    lines.add("  " + SAPUtils.translatePreFormat("%s.%s.name", unlocName, stackUpgrade.getDependantOn().getName()));
                }

                List<Class<? extends Turret>> applicables = stackUpgrade.getApplicableTurrets();
                if( applicables.size() > 0 ) {
                    lines.add(EnumChatFormatting.RED + SAPUtils.translatePreFormat("%s.applicableTo", unlocName));
                    for( Class<? extends Turret> cls : applicables ) {
                        TurretInfo<?> info = TurretRegistry.getTurretInfo(cls);
                        if( info != null ) {
                            String entityName = cls != null ? (String) EntityList.classToStringMapping.get(cls) : "UNKNOWN";
                            lines.add("  " + SAPUtils.translatePreFormat("entity.%s.name", entityName));
                        }
                    }
                }
            }
        } else {
            lines.add(EnumChatFormatting.ITALIC + SAPUtils.translatePreFormat("%s.shdetails", unlocName));
        }
    }

    public ItemStack getStackWithUpgrade(TurretUpgrade upgrade, int stackSize) {
        ItemStack stack = new ItemStack(this, stackSize);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("turretUpgrade", TurretUpgradeRegistry.getRegistrationName(upgrade));
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
