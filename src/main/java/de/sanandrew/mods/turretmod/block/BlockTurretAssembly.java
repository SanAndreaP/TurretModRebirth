/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTurretAssembly
        extends BlockHorizontal
{
    BlockTurretAssembly() {
        super(Material.ROCK);
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setHardness(4.25F);
        this.blockSoundType = SoundType.STONE;
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_assembly");
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_MAN, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        BlockTurretAssembly.setDefaultFacing(worldIn, pos, state);
    }

    private static void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
        if( !world.isRemote ) {
            IBlockState northState = world.getBlockState(pos.north());
            IBlockState southState = world.getBlockState(pos.south());
            IBlockState westState = world.getBlockState(pos.west());
            IBlockState eastState = world.getBlockState(pos.east());
            EnumFacing facing = state.getValue(FACING);

            if( facing == EnumFacing.NORTH && northState.isFullBlock() && !southState.isFullBlock() ) {
                facing = EnumFacing.SOUTH;
            } else if( facing == EnumFacing.SOUTH && southState.isFullBlock() && !northState.isFullBlock() ) {
                facing = EnumFacing.NORTH;
            } else if( facing == EnumFacing.WEST && westState.isFullBlock() && !eastState.isFullBlock() ) {
                facing = EnumFacing.EAST;
            } else if( facing == EnumFacing.EAST && eastState.isFullBlock() && !westState.isFullBlock() ) {
                facing = EnumFacing.WEST;
            }

            world.setBlockState(pos, state.withProperty(FACING, facing), 2);
        }
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if( stack.hasDisplayName() ) {
            TileEntity tileentity = world.getTileEntity(pos);

            if( tileentity instanceof TileEntityTurretAssembly ) {
                ((TileEntityTurretAssembly)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityTurretAssembly assembly = (TileEntityTurretAssembly) world.getTileEntity(pos);

        if( assembly != null ) {
            for( int i = 0; i < assembly.getSizeInventory(); i++ ) {
                ItemStack stack = assembly.getStackInSlot(i);

                if( ItemStackUtils.isValid(stack) ) {
                    float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                    float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                    float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

                    float motionSpeed = 0.05F;
                    entityitem.motionX = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);
                    entityitem.motionY = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
                    entityitem.motionZ = ((float)MiscUtils.RNG.randomGaussian() * motionSpeed);
                    world.spawnEntityInWorld(entityitem);
                }
            }

            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurretAssembly();
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if( enumfacing.getAxis() == EnumFacing.Axis.Y ) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public EnumFacing getDirection(int meta) {
        return this.getStateFromMeta(meta).getValue(FACING);
    }
}
