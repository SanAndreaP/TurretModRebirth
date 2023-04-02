/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.ammo;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TmrItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AmmoCartridgeItem
        extends Item
{
    public AmmoCartridgeItem() {
        super(new Properties().tab(TmrItemGroups.TURRETS).stacksTo(1));

    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> list) {
        super.fillItemCategory(group, list);
        if( this.allowdedIn(group) ) {
            AmmunitionRegistry.INSTANCE.getAll().forEach(t -> {
                if( t.isValid() ) {
                    ResourceLocation typeId = t.getId();
                    String[] subtypes = t.getSubtypes();
                    if( subtypes.length > 0 ) {
                        Arrays.stream(subtypes).forEach(s -> this.addItem(AmmunitionRegistry.INSTANCE.getItem(typeId, s), list));
                    } else {
                        ItemStack typeStack = AmmunitionRegistry.INSTANCE.getItem(typeId);
                        this.addItem(typeStack, list);
                    }
                }
            });
        }
    }

    private void addItem(ItemStack stack, NonNullList<ItemStack> list) {
        stack.setCount(stack.getMaxStackSize());
        ItemStack filled = new ItemStack(this, 1);
        IInventory inv = getInventory(filled);
        if( inv != null ) {
            for( int i = 0, max = inv.getContainerSize(); i < max; i++ ) {
                inv.setItem(i, stack.copy());
            }
            list.add(filled);
        }
    }

    //TODO: ?????????
//    @Nonnull
//    @Override
//    public ActionResultType interactLivingEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
//        return entity instanceof TurretEntity ? ActionResultType.SUCCESS : ActionResultType.PASS;
//    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World level, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if( !level.isClientSide ) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new AmmoCartridgeContainer.Provider(heldStack),
                                 pb -> pb.writeVarInt((hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND).getIndex()));
        }

        return ActionResult.success(heldStack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new AmmoCartridgeInventory(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        AmmoCartridgeInventory inv = getInventory(stack);
        if( inv != null && !inv.isEmpty() ) {
            ItemStack infoStack = inv.getAmmoTypeItem();
            tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".stored", inv.getTotalAmmoCount(), infoStack.getDisplayName()));
            infoStack.getItem().appendHoverText(infoStack, level, tooltip, flag);
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    public static AmmoCartridgeInventory getInventory(ItemStack item) {
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve().orElse(null);
        if( itemHandler instanceof InvWrapper ) {
            IInventory inv = ((InvWrapper) itemHandler).getInv();
            if( inv instanceof AmmoCartridgeInventory) {
                return (AmmoCartridgeInventory) inv;
            }
        }

        return null;
    }

    public static boolean extractAmmoStacks(ItemStack item, ITargetProcessor processor, boolean replace) {
        boolean success = false;
        IItemHandler itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve().orElse(null);
        if( itemHandler != null ) {
            for( int i = 0, max = itemHandler.getSlots(); i < max; i++ ) {
                ItemStack invStack = itemHandler.getStackInSlot(i);
                if( ItemStackUtils.isValid(invStack) && invStack.getItem() instanceof AmmoItem ) {
                    ITargetProcessor.ApplyType applyType = processor.getAmmoApplyType(invStack);

                    if( applyType == ITargetProcessor.ApplyType.ADD
                            || (applyType == ITargetProcessor.ApplyType.REPLACE && (replace || !processor.hasAmmo())) )
                    {
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

    public static boolean putAmmoInPlayerCartridge(ItemStack stack, PlayerEntity player) {
        for( int i = 0, max = player.inventory.getContainerSize(); i < max; i++ ) {
            ItemStack invStack = player.inventory.getItem(i);
            if( invStack.getItem() == ItemRegistry.AMMO_CARTRIDGE ) {
                AmmoCartridgeInventory cartridge = AmmoCartridgeItem.getInventory(invStack);
                ItemStack remain = InventoryUtils.addStackToCapability(stack, cartridge, Direction.UP, false);
                stack.setCount(remain.getCount());
                if( !ItemStackUtils.isValid(remain) ) {
                    return true;
                }
            }
        }

        return false;
    }
}
