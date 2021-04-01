/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.init.TmrItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemTurretControlUnit
        extends Item
{
    private static final String NBT_BOUND_TURRET = TmrConstants.ID + ".bound_turret";
    private static final String NBT_BOUND_TURRET_ID = "Id";
    private static final int EE_NAME_COUNT = 5;

    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    ItemTurretControlUnit() {
        super(new Properties().group(TmrItemGroups.MISC));
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        long currDisplayNameTime = System.currentTimeMillis();
        if( this.prevDisplayNameTime + 1000 < currDisplayNameTime ) {
            if( MiscUtils.RNG.randomInt(1000) != 0 ) {
                this.nameId = 0;
            } else {
                this.nameId = MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 1;// MathHelper.ceil(MiscUtils.RNG.randomInt(EE_NAME_COUNT - 1) + 2);
            }
        }

        this.prevDisplayNameTime = currDisplayNameTime;

        if( this.nameId < 1 ) {
            return super.getDisplayName(stack);
        } else {
            return new TranslationTextComponent(String.format("%s.%d", this.getTranslationKey(), this.nameId));
        }
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if( getBoundID(stack) != null ) {
            tooltip.add(new TranslationTextComponent(String.format("%s.bound", this.getTranslationKey())).mergeStyle(TextFormatting.GRAY));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if( ItemStackUtils.isItem(heldStack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            ITurretInst turretInst = getBoundTurret(heldStack, world);
            if( turretInst != null ) {
                if( !world.isRemote ) {
                    if( player.isSneaking() ) {
                        bindTurret(heldStack, null);
                    } else {
                        //TODO: reimplement TCU GUI
//                        TurretModRebirth.proxy.openGui(player, EnumGui.TCU, turretInst.get().getEntityId(), 0, 1);
                    }
                }

                return ActionResult.resultSuccess(heldStack);
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    private static UUID getBoundID(ItemStack stack) {
        CompoundNBT boundTurret = stack.getChildTag(NBT_BOUND_TURRET);
        if( boundTurret != null && boundTurret.contains("Id") ) {
            return boundTurret.getUniqueId("Id");
        }

        return null;
    }

    public static boolean isHeldTcuBoundToTurret(PlayerEntity player, ITurretInst turretInst) {
        if( player == null ) {
            return false;
        }

        ItemStack mh = player.getHeldItemMainhand();
        ItemStack oh = player.getHeldItemOffhand();

        return ItemTurretControlUnit.getBoundTurret(mh, player.world) == turretInst
               || ItemTurretControlUnit.getBoundTurret(oh, player.world) == turretInst;
    }

    public static ITurretInst getBoundTurret(ItemStack stack, World world) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return null;
        }

        UUID id = getBoundID(stack);
        if( id != null ) {
            Entity entity = EntityUtils.getEntityByUUID(world, id);
            if( entity instanceof EntityTurret ) {
                return (EntityTurret) entity;
            }
        }

        return null;
    }

    public static void bindTurret(ItemStack stack, ITurretInst turretInst) {
        if( !ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            return;
        }

        CompoundNBT tag = stack.getChildTag(NBT_BOUND_TURRET);
        if( turretInst != null ) {
            if( tag == null ) {
                tag = stack.getOrCreateChildTag(NBT_BOUND_TURRET);
            }
            tag.putUniqueId(NBT_BOUND_TURRET_ID, turretInst.get().getUniqueID());
        } else {
            stack.removeChildTag(NBT_BOUND_TURRET);

            CompoundNBT cmp = stack.getTag();
            if( cmp != null && cmp.isEmpty() ) {
                stack.setTag(null);
            }
        }
    }
}
