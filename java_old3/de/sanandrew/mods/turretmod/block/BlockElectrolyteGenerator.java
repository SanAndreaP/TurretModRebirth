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
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

@SuppressWarnings({ "NullableProblems", "deprecation" })
public class BlockElectrolyteGenerator
        extends ContainerBlock
{
    private static final VoxelShape MAIN_SEL_BB = Block.makeCuboidShape(0, 0, 0, 16, 32, 16);
    private static final VoxelShape UPPER_SEL_BB = Block.makeCuboidShape(0, -16, 0, 16, 16, 16);

    private static final BooleanProperty TILE_HOLDER = BooleanProperty.create("tile_main");

    BlockElectrolyteGenerator() {
        super(Properties.create(Material.IRON, MaterialColor.BROWN)
                        .hardnessAndResistance(4.25F)
                        .sound(SoundType.STONE)
                        .notSolid()
                        .setRequiresTool());
//        this. = 4.25F;
//        this.soundType = SoundType.STONE;
//        this.setCreativeTab(TmrCreativeTabs.MISC);
//        this.setTranslationKey(TmrConstants.ID + ":potato_generator");
//        this.setLightOpacity(0);
        this.setDefaultState(this.stateContainer.getBaseState().with(TILE_HOLDER, true));
//        this.setRegistryName(TmrConstants.ID, "");
    }

    @Override
    @Deprecated
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);

        if( state.get(TILE_HOLDER) ) {
            worldIn.setBlockState(pos.up(1), this.stateContainer.getBaseState().with(TILE_HOLDER, false));
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("ConstantConditions")
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if( state.get(TILE_HOLDER) ) {
            TileEntityElectrolyteGenerator electrolyteGen = (TileEntityElectrolyteGenerator) world.getTileEntity(pos);

            if( electrolyteGen != null ) {
                IItemHandler handler = electrolyteGen.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
                if( handler != null ) {
                    for( int i = 0, max = handler.getSlots(); i < max; i++ ) {
                        ItemStackUtils.dropBlockItem(handler.getStackInSlot(i), world, pos);
                    }
                }

                world.updateComparatorOutputLevel(pos, this);
            }

            BlockPos upBlock = pos.up(1);
            if( world.getBlockState(upBlock).getBlock() == this ) {
                world.playEvent(2001, upBlock, getStateId(world.getBlockState(upBlock)));
                world.setBlockState(upBlock, Blocks.AIR.getDefaultState(), 35);
            }
        } else {
            BlockPos downBlock = pos.down(1);
            if( world.getBlockState(downBlock).getBlock() == this ) {
                world.playEvent(2001, downBlock, getStateId(world.getBlockState(downBlock)));
                world.setBlockState(downBlock, Blocks.AIR.getDefaultState(), 35);
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if( !world.isRemote && player.isCreative() ) {
            if( !state.get(TILE_HOLDER) ) {
                BlockPos blockpos = pos.down();
                BlockState blockstate = world.getBlockState(blockpos);
                if( blockstate.getBlock() == state.getBlock() && blockstate.get(TILE_HOLDER) ) {
                    world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                    world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
                }
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        if( blockpos.getY() < world.getHeight() - 1 && world.getBlockState(blockpos.up()).isReplaceable(context) ) {
            return this.getDefaultState().with(TILE_HOLDER, true);
        } else {
            return null;
        }
    }

    @Override
    @Deprecated
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            if( state.get(TILE_HOLDER) ) {
                TileEntity tileentity = worldIn.getTileEntity(pos);
                if( tileentity instanceof TileEntityElectrolyteGenerator ) {
                    player.openContainer((TileEntityElectrolyteGenerator) tileentity);
                    player.addStat(Stats.INSPECT_HOPPER);
                }
            } else {
                return this.onBlockActivated(worldIn.getBlockState(pos.down()), worldIn, pos.down(), player, handIn, hit);
            }

            return ActionResultType.CONSUME;
        }
    }

    @Override
    @Deprecated
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isIn(this);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.up(), state.with(TILE_HOLDER, false), 3);

        if( stack.hasDisplayName() && state.get(TILE_HOLDER) ) {
            TileEntity te = world.getTileEntity(pos);
            assert te != null;
            ((TileEntityElectrolyteGenerator) te).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(TILE_HOLDER) ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.get(TILE_HOLDER);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityElectrolyteGenerator();
    }

    @Override
    @Deprecated
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return blockState.get(TILE_HOLDER)
               ? Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos))
               : getComparatorInputOverride(worldIn.getBlockState(pos.down()), worldIn, pos.down());
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(TILE_HOLDER) ? MAIN_SEL_BB : UPPER_SEL_BB;
    }

    @Override
    @Deprecated
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.getOpacity(state, worldIn, pos);
    }
}
