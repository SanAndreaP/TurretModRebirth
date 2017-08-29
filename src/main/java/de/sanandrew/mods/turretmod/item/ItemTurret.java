/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretNew;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemTurret
        extends Item
{
    private static final IItemPropertyGetter TURRET_TEX_ID = (stack, worldIn, entityIn) -> TurretRegistry.INSTANCE.getTurrets().indexOf(ItemTurret.getTurret(stack));

    public ItemTurret() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_placer");
        this.addPropertyOverride(new ResourceLocation("turretId"), TURRET_TEX_ID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        ITurret turret = getTurret(stack);
        if( turret != null ) {
            tooltip.add(Lang.translate(Lang.TURRET_NAME, turret.getName()));
//            tooltip.add(Lang.translateEntityCls(turret.getTurretClass()));
        }

        String name = getTurretName(stack);
        if( name != null ) {
            tooltip.add(String.format(Lang.translate("%s.turret_name", this.getUnlocalizedName()), name));
        }

        Float health = getTurretHealth(stack);
        if( health != null ) {
            tooltip.add(String.format(Lang.translate("%s.health", this.getUnlocalizedName()), health));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            BlockPos placingOn = pos.add(facing.getFrontOffsetX(), 0, facing.getFrontOffsetZ());
            if( facing.getFrontOffsetY() == 0 ) {
                placingOn = placingOn.offset(EnumFacing.DOWN);
                facing = EnumFacing.UP;
                if( world.getBlockState(placingOn).getBlock().isReplaceable(world, placingOn) ) {
                    placingOn = placingOn.offset(EnumFacing.UP, 2);
                    facing = EnumFacing.DOWN;
                }
            }

            int shiftY = facing == EnumFacing.UP ? 1 : -1;

            if( EntityTurretNew.canTurretBePlaced(world, placingOn, false, facing == EnumFacing.DOWN) ) {
                ItemStack stack = player.getHeldItem(hand);
                EntityTurretNew turret = spawnTurret(world, getTurret(stack), placingOn.getX() + 0.5D, placingOn.getY() + shiftY, placingOn.getZ() + 0.5D, facing == EnumFacing.DOWN, player);
                if( turret != null ) {
                    Float initHealth = getTurretHealth(stack);
                    if( initHealth != null ) {
                        turret.setHealth(initHealth);
                    }

                    String name = getTurretName(stack);
                    if( name != null ) {
                        turret.setCustomNameTag(name);
                    }

                    if( !player.capabilities.isCreativeMode ) {
                        stack.shrink(1);
                    }
                }
            }
        }

        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if( !world.isRemote ) {
            RayTraceResult traceResult = this.rayTrace(world, player, true);
            //noinspection ConstantConditions
            if( traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK ) {
                BlockPos blockPos = traceResult.getBlockPos();
                if( !world.isBlockModifiable(player, blockPos) ) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                if( !player.canPlayerEdit(blockPos, traceResult.sideHit, stack) ) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }

                if( world.getBlockState(blockPos).getBlock() instanceof BlockLiquid ) {
                    EntityTurretNew turret = spawnTurret(world, getTurret(stack), blockPos, false, player);
                    if( turret != null ) {
                        Float initHealth = getTurretHealth(stack);
                        if( initHealth != null ) {
                            turret.setHealth(initHealth);
                        }

                        String name = getTurretName(stack);
                        if( name != null ) {
                            turret.setCustomNameTag(name);
                        }

                        if( !player.capabilities.isCreativeMode ) {
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if( this.isInCreativeTab(tab) ) {
            items.addAll(TurretRegistry.INSTANCE.getTurrets().stream().map(type -> this.getTurretItem(1, type)).collect(Collectors.toList()));
        }
    }

    public static ITurret getTurret(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretUUID") ) {
            String id = nbt.getString("turretUUID");
            if( UuidUtils.isStringUuid(id) ) {
                return TurretRegistry.INSTANCE.getTurret(UUID.fromString(id));
            }
        }

        return TurretRegistry.NULL_TURRET;
    }

    public static Float getTurretHealth(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretHealth") ) {
            return nbt.getFloat("turretHealth");
        }

        return null;
    }

    public static String getTurretName(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretName") ) {
            return nbt.getString("turretName");
        }

        if( stack.hasDisplayName() ) {
            return stack.getDisplayName();
        }

        return null;
    }

    @Nonnull
    public ItemStack getTurretItem(int stackSize, ITurret type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret_placer item with NULL type!");
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("turretUUID", type.getId().toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Nonnull
    public ItemStack getTurretItem(int stackSize, ITurret type, ITurretInst turretInst) {
        ItemStack stack = this.getTurretItem(stackSize, type);
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null ) {
            EntityLiving turretL = turretInst.getEntity();
            nbt.setFloat("turretHealth", turretL.getHealth());
            if( turretL.hasCustomName() ) {
                nbt.setString("turretName", turretL.getCustomNameTag());
            }
        }

        return stack;
    }

    public static EntityTurretNew spawnTurret(World world, ITurret turret, BlockPos pos, boolean isUpsideDown, EntityPlayer owner) {
        return spawnTurret(world, turret, pos.getX(), pos.getY(), pos.getZ(), isUpsideDown, owner);
    }

    public static EntityTurretNew spawnTurret(World world, ITurret turret, double x, double y, double z, boolean isUpsideDown, EntityPlayer owner) {
        EntityTurretNew turretE = new EntityTurretNew(world, isUpsideDown, owner, turret);
        turretE.setLocationAndAngles(x, y - (isUpsideDown ? 1.0D : 0.0D), z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
        turretE.rotationYawHead = turretE.rotationYaw;
        turretE.renderYawOffset = turretE.rotationYaw;
        world.spawnEntity(turretE);
        turretE.playLivingSound();

        return turretE;
    }

//    private static EntityTurretNew createEntity(ITurret turret, World world, boolean isUpsideDown, EntityPlayer owner) {
//        EntityTurretNew entity = null;
//        try {
//            Class<? extends EntityTurretNew> entityClass = info.getTurretClass();
//            if( entityClass != null ) {
//                entity = entityClass.getConstructor(World.class, boolean.class, EntityPlayer.class).newInstance(world, isUpsideDown, owner);
//            }
//        } catch( InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ex ) {
//            TmrConstants.LOG.log(Level.ERROR, "Cannot instanciate turret_placer!", ex);
//        }
//
//        if( entity == null ) {
//            TmrConstants.LOG.printf(Level.WARN, "Skipping turret_placer with name %s", info.getName());
//        }
//
//        return entity;
//    }
}
