/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ItemTurret
        extends Item
{
    public final ITurret turret;

    public ItemTurret(ITurret turret) {
        super();
        this.turret = turret;

        ResourceLocation id = turret.getId();

        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName(id);
        this.setTranslationKey(id.toString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        TurretStats stats = new TurretStats(stack.getTagCompound());
        if( stats.name != null ) {
            tooltip.add(LangUtils.translate(Lang.ITEM_TURRET_PLACER.get("customname"), stats.name));
        }

        if( stats.health != null ) {
            tooltip.add(LangUtils.translate(Lang.ITEM_TURRET_PLACER.get("health"), stats.health));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            BlockPos placingOn = pos.add(facing.getXOffset(), 0, facing.getZOffset());
            if( facing.getYOffset() == 0 ) {
                placingOn = placingOn.offset(EnumFacing.DOWN);
            }

            ItemStack stack = player.getHeldItem(hand);
            if( !this.turret.isBuoy() && EntityTurret.canTurretBePlaced(this.turret, world, placingOn, false) ) {
                EntityTurret bob = spawnTurret(world, this.turret, placingOn.getX() + 0.5D, placingOn.getY() + 1, placingOn.getZ() + 0.5D, player);
                if( bob != null ) {
                    new TurretStats(stack.getTagCompound()).apply(bob);

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

                BlockPos lowerPos = blockPos.down();
                if( this.turret.isBuoy() && isBlockLiquid(world, blockPos) && isBlockLiquid(world, lowerPos) && EntityTurret.canTurretBePlaced(this.turret, world, lowerPos, false) ) {
                    EntityTurret susan = spawnTurret(world, this.turret, lowerPos, player);
                    if( susan != null ) {
                        new TurretStats(stack.getTagCompound()).apply(susan);

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

    private static EntityTurret spawnTurret(World world, ITurret turret, BlockPos pos, EntityPlayer owner) {
        return spawnTurret(world, turret, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, owner);
    }

    private static EntityTurret spawnTurret(World world, ITurret turret, double x, double y, double z, EntityPlayer owner) {
        EntityTurret turretE = new EntityTurret(world, owner, turret);
        turretE.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
        turretE.rotationYawHead = turretE.rotationYaw;
        turretE.renderYawOffset = turretE.rotationYaw;
        world.spawnEntity(turretE);
        turretE.playLivingSound();

        return turretE;
    }

    public static final class TurretStats
    {
        public final Float health;
        public final String name;

        public TurretStats(ITurretInst turretInst) {
            EntityLiving bob = turretInst.get();
            this.health = bob.getHealth();
            this.name = bob.hasCustomName() ? bob.getCustomNameTag() : null;
        }

        public TurretStats(NBTTagCompound nbt) {
            if( nbt != null && nbt.hasKey("TurretStats", Constants.NBT.TAG_COMPOUND) ) {
                nbt = nbt.getCompoundTag("TurretStats");

                this.health = nbt.hasKey("Health", Constants.NBT.TAG_ANY_NUMERIC) ? nbt.getFloat("Health") : null;
                this.name = nbt.hasKey("Name", Constants.NBT.TAG_STRING) ? nbt.getString("Name") : null;
            } else {
                this.health = null;
                this.name = null;
            }
        }

        public void apply(ITurretInst turretInst) {
            EntityLiving bob = turretInst.get();
            if( this.health != null ) {
                bob.setHealth(this.health);
            }
            if( this.name != null ) {
                bob.setCustomNameTag(this.name);
            }
        }

        public NBTTagCompound updateData(ItemStack stack) {
            return this.serialize(stack.getOrCreateSubCompound("TurretStats"));
        }

        public NBTTagCompound serialize(NBTTagCompound nbt) {
            if( this.health != null ) {
                nbt.setFloat("Health", this.health);
            }
            if( this.name != null ) {
                nbt.setString("Name", this.name);
            }

            return nbt;
        }
    }
}
