/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/** LEGACY!!! **/
@Deprecated
public class ItemTurretOld
        extends Item
{
    @Deprecated
    private static final IItemPropertyGetter TURRET_TEX_ID = (stack, worldIn, entityIn) ->
                                                                     TurretRegistry.INSTANCE.getTurrets().indexOf(TurretRegistry.INSTANCE.getTurret(stack));

    @Deprecated
    ItemTurretOld() {
        super();
        this.setUnlocalizedName(TmrConstants.ID + ":turret_placer");
        this.addPropertyOverride(new ResourceLocation("turretId"), TURRET_TEX_ID);
        this.setRegistryName(TmrConstants.ID, "turret_placer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(TextFormatting.RED + "PLEASE PLACE DOWN AND DISMANTLE TURRET TO GET THE REAL ITEM BACK!!!");
    }

    private static void setTurretStats(EntityTurret turret, ItemStack stack) {
        Float initHealth = getTurretHealth(stack);
        if( initHealth != null ) {
            turret.setHealth(initHealth);
        }

        String name = getTurretName(stack);
        if( name != null ) {
            turret.setCustomNameTag(name);
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            BlockPos placingOn = pos.add(facing.getFrontOffsetX(), 0, facing.getFrontOffsetZ());
            if( facing.getFrontOffsetY() == 0 ) {
                placingOn = placingOn.offset(EnumFacing.DOWN);
            }

            ItemStack stack = player.getHeldItem(hand);
            ITurret delegate = TurretRegistry.INSTANCE.getTurret(stack);
            if( !delegate.isBuoy() && EntityTurret.canTurretBePlaced(delegate, world, placingOn, false) ) {
                EntityTurret turret = ItemTurret.spawnTurret(world, delegate, placingOn.getX() + 0.5D, placingOn.getY() + 1, placingOn.getZ() + 0.5D, player);
                if( turret != null ) {
                    setTurretStats(turret, stack);

                    if( !player.capabilities.isCreativeMode ) {
                        stack.shrink(1);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        } else {
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if( !world.isRemote ) {
            RayTraceResult traceResult = this.rayTrace(world, player, true);
            if( traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK ) {
                BlockPos blockPos = traceResult.getBlockPos();
                if( !world.isBlockModifiable(player, blockPos) ) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                if( !player.canPlayerEdit(blockPos, traceResult.sideHit, stack) ) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                ITurret delegate = TurretRegistry.INSTANCE.getTurret(stack);
                BlockPos lowerPos = blockPos.down();
                if( delegate.isBuoy() && isBlockLiquid(world, blockPos) && isBlockLiquid(world, lowerPos) && EntityTurret.canTurretBePlaced(delegate, world, lowerPos, false) ) {
                    EntityTurret turret = spawnTurret(world, delegate, lowerPos, player);
                    if( turret != null ) {
                        setTurretStats(turret, stack);

                        if( !player.capabilities.isCreativeMode ) {
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static boolean isBlockLiquid(World world, BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();
        return b instanceof BlockLiquid || b instanceof IFluidBlock;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) { }

    private static Float getTurretHealth(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretHealth") ) {
            return nbt.getFloat("turretHealth");
        }

        return null;
    }

    private static String getTurretName(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretName") ) {
            return nbt.getString("turretName");
        }

        if( stack.hasDisplayName() ) {
            return stack.getDisplayName();
        }

        return null;
    }

    private static EntityTurret spawnTurret(World world, ITurret turret, BlockPos pos, EntityPlayer owner) {
        return ItemTurret.spawnTurret(world, turret, pos.getX(), pos.getY(), pos.getZ(), owner);
    }
}
