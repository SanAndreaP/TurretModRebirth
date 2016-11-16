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
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockElectrolyteGenerator
        extends Block
{
    private static final AxisAlignedBB MAIN_SEL_BB = new AxisAlignedBB(0, 0, 0, 1, 2, 1);
    private static final AxisAlignedBB UPPER_SEL_BB = new AxisAlignedBB(0, -1, 0, 1, 1, 1);

    private static final PropertyBool TILE_HOLDER = PropertyBool.create("tile_main");

    BlockElectrolyteGenerator() {
        super(Material.ROCK);
        this.blockHardness = 4.25F;
        this.blockSoundType = SoundType.STONE;
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setUnlocalizedName(TurretModRebirth.ID + ":potato_generator");
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TILE_HOLDER, true));
    }

    @Override
    @SuppressWarnings("TailRecursion")
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            if( state.getValue(TILE_HOLDER) ) {
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_POTATOGEN, pos.getX(), pos.getY(), pos.getZ());
            } else {
                return this.onBlockActivated(world, pos.down(1), world.getBlockState(pos.down(1)), player, hand, heldItem, side, hitX, hitY, hitZ);
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up(1)).getBlock().isReplaceable(world, pos.up(1));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        if( state.getValue(TILE_HOLDER) ) {
            worldIn.setBlockState(pos.up(1), this.blockState.getBaseState().withProperty(TILE_HOLDER, false));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if( stack.hasDisplayName() && state.getValue(TILE_HOLDER) ) {
            TileEntity te = world.getTileEntity(pos);
            assert te != null;
            ((TileEntityElectrolyteGenerator) te).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if( state.getValue(TILE_HOLDER) ) {
            TileEntityElectrolyteGenerator potatoGen = (TileEntityElectrolyteGenerator) world.getTileEntity(pos);

            if( potatoGen != null ) {
                for( int i = 0; i < potatoGen.getSizeInventory(); i++ ) {
                    if( TileEntityElectrolyteGenerator.isSlotProcessing(i) ) {
                        continue;
                    }

                    ItemStack stack = potatoGen.getStackInSlot(i);

                    if( ItemStackUtils.isValid(stack) ) {
                        float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                        float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
                        float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

                        EntityItem entityitem = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

                        float motionSpeed = 0.05F;
                        entityitem.motionX = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
                        entityitem.motionY = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
                        entityitem.motionZ = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
                        world.spawnEntityInWorld(entityitem);
                    }
                }

                world.updateComparatorOutputLevel(pos, this);
            }

            BlockPos upBlock = pos.up(1);
            if( world.getBlockState(upBlock).getBlock() == this ) {
                world.playEvent(2001, upBlock, getStateId(world.getBlockState(upBlock)));
                world.setBlockToAir(upBlock);
            }
        } else {
            BlockPos downBlock = pos.down(1);
            if( world.getBlockState(downBlock).getBlock() == this ) {
                world.playEvent(2001, downBlock, getStateId(world.getBlockState(downBlock)));
                world.setBlockToAir(downBlock);
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TILE_HOLDER, meta == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean type = state.getValue(TILE_HOLDER);
        return type ? 0 : 1;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TILE_HOLDER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityElectrolyteGenerator();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TILE_HOLDER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return (state.getValue(TILE_HOLDER) ? MAIN_SEL_BB : UPPER_SEL_BB).offset(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(TILE_HOLDER) ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
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
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return state.getValue(TILE_HOLDER) ? Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos)) : 0;
    }
}
