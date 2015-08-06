/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.turretmod.api.TurretAmmo;
import de.sanandrew.mods.turretmod.api.registry.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrItems;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import org.apache.logging.log4j.Level;

import java.util.*;

public class ItemTurretAmmo
        extends Item
{
    private static final List<TurretAmmo> SORTED_TYPE_LIST = new ArrayList<>();
    private static final Map<TurretAmmo, Triplet<String, String, List<String>>> AMMO_NAMES_ICON_DESC = new HashMap<>();

    @SideOnly(Side.CLIENT)
    private Map<TurretAmmo, IIcon> ammoIcons;

    public ItemTurretAmmo() {
        super();
        this.hasSubtypes = false; // subtypes defined through NBT, so no need to check for subtypes via metadata
        this.setMaxDamage(0);
        this.setUnlocalizedName(TurretMod.MOD_ID + ":turret_ammo");
        this.setCreativeTab(TmrCreativeTabs.MISC);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.turret_ammo." + AMMO_NAMES_ICON_DESC.get(getTypeFromItem(stack)).getValue0();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.ammoIcons = new HashMap<>(SORTED_TYPE_LIST.size());
        for( TurretAmmo type : SORTED_TYPE_LIST ) {
            this.ammoIcons.put(type, iconRegister.registerIcon(AMMO_NAMES_ICON_DESC.get(type).getValue1()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.ammoIcons.get(getTypeFromItem(stack));
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        super.addInformation(stack, player, lines, advInfo);
        List<String> info = AMMO_NAMES_ICON_DESC.get(getTypeFromItem(stack)).getValue2();

        if( info != null ) {
            lines.addAll(info);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
        for( TurretAmmo type : SORTED_TYPE_LIST ) {
            itemList.add(getItemFromType(type, 1));
        }
    }

    public static void registerAmmoItem(TurretAmmo type, String name, String icon, String... descLines) {
        if( SORTED_TYPE_LIST.contains(type) ) {
            TurretMod.MOD_LOG.log(Level.WARN, "Ammo type %s already registered as item!", type.toString());
        } else {
            SORTED_TYPE_LIST.add(type);
            AMMO_NAMES_ICON_DESC.put(type, Triplet.with(name, icon, descLines == null || descLines.length == 0 ? null : Arrays.asList(descLines)));
        }
    }

    public static ItemStack getItemFromType(TurretAmmo type, int count) {
        ItemStack newStack = new ItemStack(TmrItems.turretAmmo, count);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("typeId", TurretAmmoRegistry.getTypeId(type).toString());
        newStack.setTagCompound(nbt);

        return newStack;
    }

    public static TurretAmmo getTypeFromItem(ItemStack stack) {
        if( !stack.hasTagCompound() ) {
            return null;
        } else {
            NBTTagCompound nbt = stack.getTagCompound();
            if( !nbt.hasKey("typeId") ) {
                return null;
            } else {
                return TurretAmmoRegistry.getType(nbt.getString("typeId"));
            }
        }
    }
}
