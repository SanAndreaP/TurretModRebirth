/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAmmoCartridge
        extends Item
{
    ItemAmmoCartridge() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName(TmrConstants.ID, "ammo.cartridge");
        this.setUnlocalizedName(TmrConstants.ID + ":ammo.cartridge");
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        super.getSubItems(tab, list);
        if( this.isInCreativeTab(tab) ) {
            AmmunitionRegistry.INSTANCE.getTypes().forEach(t -> {
                if( t.isValid() ) {
                    ItemStack typeStack = AmmunitionRegistry.INSTANCE.getItem(t.getId());
                    typeStack.setCount(typeStack.getMaxStackSize());
                    ItemStack filled = new ItemStack(this, 1);
                    IInventory inv = getInventory(filled);
                    if( inv != null ) {
                        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
                            inv.setInventorySlotContents(i, typeStack.copy());
                        }
                        list.add(filled);
                    }
                }
            });
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if( !worldIn.isRemote ) {
            TurretModRebirth.proxy.openGui(playerIn, EnumGui.CARTRIDGE, 0, 0, 0);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        AmmoCartridgeInventory inv = getInventory(stack);
        if( inv != null && !inv.isEmpty() ) {
            tooltip.add("Stored: " + inv.getTotalAmmoCount() + "x " + AmmunitionRegistry.INSTANCE.getItem(inv.getAmmoType().getId()).getDisplayName());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static AmmoCartridgeInventory getInventory(ItemStack item) {
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if( itemHandler instanceof InvWrapper ) {
            IInventory inv = ((InvWrapper) itemHandler).getInv();
            if( inv instanceof AmmoCartridgeInventory) {
                return (AmmoCartridgeInventory) inv;
            }
        }

        return null;
    }

    public static boolean extractAmmoStacks(ItemStack item, ITargetProcessor processor) {
        boolean success = false;
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if( itemHandler != null ) {
            for( int i = 0, max = itemHandler.getSlots(); i < max; i++ ) {
                ItemStack invStack = itemHandler.getStackInSlot(i);
                if( ItemStackUtils.isValid(invStack) && invStack.getItem() instanceof ItemAmmo ) {
                    if( processor.isAmmoApplicable(invStack) ) {
                        ItemStack copyInvStack = invStack.copy();
                        if( processor.addAmmo(copyInvStack, item) ) {
                            success = true;
                            itemHandler.extractItem(i, invStack.getCount() - copyInvStack.getCount(), false);
                        }
                    }
                }
            }
        }

        return success;
    }

}
