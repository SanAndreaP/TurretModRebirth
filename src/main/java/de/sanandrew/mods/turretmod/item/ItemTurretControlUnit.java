/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemTurretControlUnit
        extends Item
{
    private static final String NBT_BOUND_TURRET = TmrConstants.ID + ".bound_turret";
    private static final int EE_NAME_COUNT = 5;

    private long prevDisplayNameTime = 0;
    private int nameId = 0;

    ItemTurretControlUnit() {
        super();
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setTranslationKey(TmrConstants.ID + ":turret_control_unit");
        this.setRegistryName(TmrConstants.ID, "turret_control_unit");
    }

    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
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
            return super.getItemStackDisplayName(stack);
        } else {
            return LangUtils.translate(String.format("%s.name_%d", this.getTranslationKey(), this.nameId));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(stack, world, tooltip, tooltipFlag);

        if( getBoundID(stack) != null ) {
            tooltip.add(ChatFormatting.GRAY + LangUtils.translate(String.format("%s.bound", this.getTranslationKey())));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        if( ItemStackUtils.isItem(heldStack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
            ITurretInst turretInst = getBoundTurret(heldStack, world);
            if( turretInst != null ) {
                if( !world.isRemote ) {
                    if( player.isSneaking() ) {
                        bindTurret(heldStack, null);
                    } else {
                        TurretModRebirth.proxy.openGui(player, EnumGui.TCU, turretInst.get().getEntityId(), 0, 1);
                    }
                }
                return ActionResult.newResult(EnumActionResult.SUCCESS, heldStack);
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    private static UUID getBoundID(ItemStack stack) {
        NBTTagCompound boundTurret = stack.getSubCompound(NBT_BOUND_TURRET);
        if( boundTurret != null && boundTurret.hasKey("id_low", Constants.NBT.TAG_LONG)
            && boundTurret.hasKey("id_high", Constants.NBT.TAG_LONG) )
        {
            return new UUID(boundTurret.getLong("id_high"), boundTurret.getLong("id_low"));
        }

        return null;
    }

    public static boolean isHeldTcuBoundToTurret(EntityPlayer player, ITurretInst turretInst) {
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

        NBTTagCompound boundTurret = stack.getSubCompound(NBT_BOUND_TURRET);
        if( turretInst != null ) {
            if( boundTurret == null ) {
                boundTurret = stack.getOrCreateSubCompound(NBT_BOUND_TURRET);
            }
            UUID persistentId = turretInst.get().getPersistentID();
            boundTurret.setLong("id_low", persistentId.getLeastSignificantBits());
            boundTurret.setLong("id_high", persistentId.getMostSignificantBits());
        } else {
            stack.removeSubCompound(NBT_BOUND_TURRET);

            NBTTagCompound cmp = stack.getTagCompound();
            if( cmp != null && cmp.isEmpty() ) {
                stack.setTagCompound(null);
            }
        }
    }
}
