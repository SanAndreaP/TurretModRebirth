/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.Collection;

public class BlockElectrolyteGenerator
        extends Block
{
    public static final PropertyBool TILE_HOLDER = PropertyBool.create("tile_main");

    protected BlockElectrolyteGenerator() {
        super(Material.ROCK);
        this.blockHardness = 4.25F;
        this.blockSoundType = SoundType.STONE;
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":potato_generator");
        this.setDefaultState(this.blockState.getBaseState().withProperty(TILE_HOLDER, true));
    }

    @Override
    @SuppressWarnings("TailRecursion")
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            if( state.getValue(TILE_HOLDER) ) {
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_POTATOGEN, pos.getX(), pos.getY(), pos.getZ());
            } else {
                return this.onBlockActivated(world, pos.down(1), world.getBlockState(pos.down(1)), player, hand, heldItem, side, hitX, hitY, hitZ);
                //            ItemStack held = player.getHeldItem();
                //            if( ItemStackUtils.isValidStack(held) && held.getItem() instanceof IToolHammer && !player.isSneaking() ) {
                //                int meta = (world.getBlockMetadata(x, y, z) + 1) & 3;
                //                world.setBlockMetadataWithNotify(x, y, z, meta, 2);
                //            } else {
                //            }
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up(1)).getBlock().isReplaceable(world, pos.up(1));
    }

    //    @Override
//    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
//        return super.canPlaceBlockAt(world, x, y, z) && world.getBlock(x, y+1, z).isReplaceable(world, x, y+1, z);
//    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        if( state.getValue(TILE_HOLDER) ) {
            worldIn.setBlockState(pos.up(1), this.blockState.getBaseState().withProperty(TILE_HOLDER, false));
        }
    }


//    @Override
//    public void onBlockAdded(World world, int x, int y, int z) {
//        super.onBlockAdded(world, x, y, z);
//
//        if( world.getBlockMetadata(x, y, z) == 0 ) {
//            world.setBlock(x, y+1, z, this, 1, 3);
//        }
//    }

//    @Override
//    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
//        return super.getSelectedBoundingBox(state, worldIn, pos);
//    }

//    @Override
//    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, BlockPos pos) {
//        int meta = world.getBlockMetadata(x, y, z);
//        return AxisAlignedBB.getBoundingBox(x, y + (meta == 0 ? 0 : -1), z, x + 1, y + (meta == 0 ? 2 : 1), z + 1);
//    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if( stack.hasDisplayName() && state.getValue(TILE_HOLDER) ) {
            TileEntity te = world.getTileEntity(pos);
            assert te != null;
            ((TileEntityElectrolyteGenerator) te).setCustomName(stack.getDisplayName());
        }
    }


//    @Override
//    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack stack) {
//        if( stack.hasDisplayName() && world.getBlockMetadata(x, y, z) == 0 ) {
//            ((TileEntityElectrolyteGenerator) world.getTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
//        }
//    }


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

                    if( ItemStackUtils.isValidStack(stack) ) {
                        float xOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;
                        float yOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;
                        float zOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;

                        EntityItem entityitem = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

                        float motionSpeed = 0.05F;
                        entityitem.motionX = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed);
                        entityitem.motionY = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed + 0.2F);
                        entityitem.motionZ = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed);
                        world.spawnEntityInWorld(entityitem);
                    }
                }

                world.updateComparatorOutputLevel(pos, this);
            }

            BlockPos upBlock = pos.up(1);
            if( world.getBlockState(upBlock).getBlock() == this ) {
                world.playEvent(2001, upBlock, getStateId(world.getBlockState(upBlock)));
//                world.playAuxSFX(2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y + 1, z)));
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
    @Deprecated
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

    //    @Override
//    public boolean hasTileEntity(int metadata) {
//        return metadata == 0;
//    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityElectrolyteGenerator();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new CustomBlockState(this, TILE_HOLDER);
    }

    //    @Override
//    public TileEntity createTileEntity(World world, int metadata) {
//        return new TileEntityElectrolyteGenerator();
//    }

//    @Override
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return super.getRenderType(state);
//    }

//    @Override
//    public int getRenderType() {
//        return -1;
//    }

//    @Override
//    public boolean isOpaqueCube() {
//        return false;
//    }

//    @Override
//    public boolean renderAsNormalBlock() {
//        return false;
//    }

//    @Override
//    public boolean hasComparatorInputOverride() {
//        return true;
//    }
//
//    @Override
//    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
//        return world.getBlockMetadata(x, y, z) == 0 ? Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z)) : 0;
//    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    public static final class CustomBlockState
            extends BlockStateContainer
    {
        public CustomBlockState(Block blockIn, IProperty<?>... properties) {
            super(blockIn, properties);
        }

        @Override
        protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
            return new CustomStateImpl(block, properties);
        }

        public static final class CustomStateImpl
                extends StateImplementation
        {
            private static final AxisAlignedBB MAIN_SEL_BB = new AxisAlignedBB(0, 0, 0, 1, 2, 1);
            private static final AxisAlignedBB UPPER_SEL_BB = new AxisAlignedBB(0, -1, 0, 1, 1, 1);

            protected CustomStateImpl(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
                super(blockIn, propertiesIn);
            }

            @Override
            public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
                return (this.getValue(TILE_HOLDER) ? MAIN_SEL_BB : UPPER_SEL_BB).offset(pos);
            }

            @Override
            public EnumBlockRenderType getRenderType() {
                return EnumBlockRenderType.INVISIBLE;
            }

            @Override
            public boolean isOpaqueCube() {
                return false;
            }

            @Override
            public boolean isBlockNormalCube() {
                return false;
            }

            @Override
            public boolean hasComparatorInputOverride() {
                return true;
            }

            @Override
            public int getComparatorInputOverride(World world, BlockPos pos) {
                return this.getValue(TILE_HOLDER) ? Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos)) : 0;
            }
        }
    }
}
