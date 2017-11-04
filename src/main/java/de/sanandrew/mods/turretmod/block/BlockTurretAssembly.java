/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import com.google.common.collect.ImmutableMap;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BlockTurretAssembly
        extends BlockHorizontal
{
    BlockTurretAssembly() {
        super(Material.ROCK);
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setHardness(4.25F);
        this.blockSoundType = SoundType.STONE;
        this.setUnlocalizedName(TmrConstants.ID + ":turret_assembly");
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setRegistryName(TmrConstants.ID, "turret_assembly");
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
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if( enumfacing.getAxis() == EnumFacing.Axis.Y ) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        BlockTurretAssembly.setDefaultFacing(worldIn, pos, state);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityTurretAssembly assembly = (TileEntityTurretAssembly) world.getTileEntity(pos);

        if( assembly != null ) {
            for( int i = 0; i < assembly.getInventory().getSizeInventory(); i++ ) {
                ItemStack stack = assembly.getInventory().getStackInSlot(i);

                if( ItemStackUtils.isValid(stack) ) {
                    float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                    float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                    float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

                    float motionSpeed = 0.05F;
                    entityitem.motionX = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
                    entityitem.motionY = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
                    entityitem.motionZ = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
                    world.spawnEntity(entityitem);
                }
            }

            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_MAN, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if( stack.hasDisplayName() ) {
            TileEntity tileentity = world.getTileEntity(pos);

            if( tileentity instanceof TileEntityTurretAssembly ) {
                ((TileEntityTurretAssembly) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new MyBlockStateContainer(this, FACING);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public EnumFacing getDirection(int meta) {
        return this.getStateFromMeta(meta).getValue(FACING);
    }

    private static final class MyBlockStateContainer
            extends BlockStateContainer
    {
        public MyBlockStateContainer(Block blockIn, IProperty<?>... properties) {
            super(blockIn, properties);
        }

        @Override
        protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, com.google.common.base.Optional<?>> unlistedProperties) {
            return new MyStateImplementation(block, properties);
        }
    }

    private static final class MyStateImplementation
            extends BlockStateContainer.StateImplementation
    {
        protected MyStateImplementation(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
            super(blockIn, propertiesIn);
        }

        @Override
        public boolean isFullCube() {
            return false;
        }

        @Override
        public EnumBlockRenderType getRenderType() {
            return EnumBlockRenderType.MODEL;
        }

        @Override
        public boolean hasComparatorInputOverride() {
            return true;
        }

        @Override
        public int getComparatorInputOverride(World worldIn, BlockPos pos) {
            return Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos));
        }

        @Override
        public boolean isOpaqueCube() {
            return false;
        }
    }
}
