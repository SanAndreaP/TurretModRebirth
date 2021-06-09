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

public class TurretControlUnit
        extends Item
{
    private static final String NBT_BOUND_TURRET = TmrConstants.ID + ".bound_turret";
    private static final String NBT_BOUND_TURRET_ID = "Id";
    private static final int EE_NAME_COUNT = 5;

    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    TurretControlUnit() {
        super(new Properties().tab(TmrItemGroups.MISC));
    }

    @Nonnull
    @Override
    public ITextComponent getName(@Nonnull ItemStack stack) {
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
            return super.getName(stack);
        } else {
            return new TranslationTextComponent(String.format("%s.%d", this.getDescriptionId(), this.nameId));
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if( getBoundID(stack) != null ) {
            tooltip.add(new TranslationTextComponent(String.format("%s.bound", this.getDescriptionId())).withStyle(TextFormatting.GRAY));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if( ItemStackUtils.isItem(heldStack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            ITurretInst turretInst = getBoundTurret(heldStack, world);
            if( turretInst != null ) {
                if( !world.isClientSide ) {
                    if( player.isCrouching() ) {
                        bindTurret(heldStack, null);
                    } else {
                        //TODO: reimplement TCU GUI
//                        TurretModRebirth.proxy.openGui(player, EnumGui.TCU, turretInst.get().getEntityId(), 0, 1);
                    }
                }

                return ActionResult.success(heldStack);
            }
        }

        return super.use(world, player, hand);
    }

    private static UUID getBoundID(ItemStack stack) {
        CompoundNBT boundTurret = stack.getTagElement(NBT_BOUND_TURRET);
        if( boundTurret != null && boundTurret.contains("Id") ) {
            return boundTurret.getUUID("Id");
        }

        return null;
    }

    public static boolean isHeldTcuBoundToTurret(PlayerEntity player, ITurretInst turretInst) {
        if( player == null ) {
            return false;
        }

        ItemStack mh = player.getMainHandItem();
        ItemStack oh = player.getOffhandItem();

        return TurretControlUnit.getBoundTurret(mh, player.level) == turretInst
               || TurretControlUnit.getBoundTurret(oh, player.level) == turretInst;
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

        CompoundNBT tag = stack.getTagElement(NBT_BOUND_TURRET);
        if( turretInst != null ) {
            if( tag == null ) {
                tag = stack.getOrCreateTagElement(NBT_BOUND_TURRET);
            }
            tag.putUUID(NBT_BOUND_TURRET_ID, turretInst.get().getUUID());
        } else {
            stack.removeTagKey(NBT_BOUND_TURRET);

            CompoundNBT cmp = stack.getTag();
            if( cmp != null && cmp.isEmpty() ) {
                stack.setTag(null);
            }
        }
    }
}
