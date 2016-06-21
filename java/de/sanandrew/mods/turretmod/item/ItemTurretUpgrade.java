/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
//import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemTurretUpgrade
        extends Item
{
//    private Map<UUID, IIcon> upgIcons;
//    private IIcon backg;

    public ItemTurretUpgrade() {
        super();
        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_upgrade");
//        this.setTextureName(TurretModRebirth.ID + ":upgrades/empty");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        TurretUpgrade upg = UpgradeRegistry.INSTANCE.getUpgrade(stack);
        lines.add(Lang.translate("%s.%s.name", this.getUnlocalizedName(stack), upg.getName()));
        super.addInformation(stack, player, lines, advInfo);
    }

//    @Override
//    public boolean requiresMultipleRenderPasses() {
//        return true;
//    }
//
//    @Override
//    public int getRenderPasses(int metadata) {
//        return 2;
//    }
//
//    @Override
//    public IIcon getIcon(ItemStack stack, int pass) {
//        UUID upgId = UpgradeRegistry.INSTANCE.getUpgradeUUID(UpgradeRegistry.INSTANCE.getUpgrade(stack));
//
//        if( pass == 0 && !upgId.equals(UpgradeRegistry.EMPTY) ) {
//            return this.backg;
//        } else {
//            return this.upgIcons.get(upgId);
//        }
//    }
//
//    @Override
//    public void registerIcons(IIconRegister iconRegister) {
//        super.registerIcons(iconRegister);
//
//        this.upgIcons = new HashMap<>();
//
//        this.backg = iconRegister.registerIcon(TurretModRebirth.ID + ":upgrades/backg");
//        for( TurretUpgrade upg : UpgradeRegistry.INSTANCE.getRegisteredUpgrades() ) {
//            this.upgIcons.put(UpgradeRegistry.INSTANCE.getUpgradeUUID(upg), iconRegister.registerIcon(upg.getIconTexture()));
//        }
//    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List itmList) {
        for( TurretUpgrade upgrade : UpgradeRegistry.INSTANCE.getRegisteredUpgrades() ) {
            itmList.add(UpgradeRegistry.INSTANCE.getUpgradeItem(upgrade));
        }
    }
}
