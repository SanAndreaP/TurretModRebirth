/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemTurretUpgrade
        extends Item
{
    private IIcon[] upgIcons;
    private IIcon backg;

    public ItemTurretUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_upgrade");
        this.setTextureName(TurretModRebirth.ID + ":upgrades/empty");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int dmg = stack.getItemDamage();
        if( dmg > 0 && dmg <= UpgradeRegistry.INSTANCE.getRegisteredCount() ) {
            return "item." + TurretModRebirth.ID + ":turret_upgrade." + UpgradeRegistry.INSTANCE.getUpgrade(dmg - 1).getName();
        }

        return "item." + TurretModRebirth.ID + ":turret_upgrade.empty";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        lines.add(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".desc"));
        super.addInformation(stack, player, lines, advInfo);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return metadata > 0 ? 2 : 1;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        int dmg = stack.getItemDamage();
        if( dmg == 0 ) {
            return this.upgIcons[0];
        }
        if( pass == 0 ) {
            return this.backg;
        } else {
            if( dmg > 0 && dmg < this.upgIcons.length ) {
                return this.upgIcons[dmg];
            } else {
                return this.upgIcons[0];
            }
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);

        int regCount = UpgradeRegistry.INSTANCE.getRegisteredCount();
        this.upgIcons = new IIcon[regCount + 1];

        this.backg = iconRegister.registerIcon(TurretModRebirth.ID + ":upgrades/backg");
        this.upgIcons[0] = iconRegister.registerIcon(TurretModRebirth.ID + ":upgrades/empty");
        for( int i = 1; i <= regCount; i++ ) {
            this.upgIcons[i] = iconRegister.registerIcon(UpgradeRegistry.INSTANCE.getUpgrade(i - 1).getIconTexture());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List itmList) {
        int regCount = UpgradeRegistry.INSTANCE.getRegisteredCount();
        for( int i = 0; i <= regCount; i++ ) {
            itmList.add(new ItemStack(this, 1, i));
        }
    }
}
