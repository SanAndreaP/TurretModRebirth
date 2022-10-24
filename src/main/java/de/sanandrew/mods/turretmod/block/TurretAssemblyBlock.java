package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TurretAssemblyBlock
        extends HorizontalBlock
{
    public static final   BooleanProperty            ENABLED      = BlockStateProperties.ENABLED;

    private static final Map<Direction, VoxelShape> BLOCK_SHAPES = Util.make(() -> {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        VoxelShape base = Block.box(0.5,0,0.5,15.5,10,15.5);
        VoxelShape plate = Block.box(3,10,3,13,11,13);

        map.put(Direction.NORTH, VoxelShapes.or(base, plate,
                                                Block.box(0,8,11,2,16,14),
                                                Block.box(14,8,11,16,16,14),
                                                Block.box(2,14,12,14,15,13)));
        map.put(Direction.SOUTH, VoxelShapes.or(base, plate,
                                                Block.box(0,8,5,2,16,2),
                                                Block.box(14,8,5,16,16,2),
                                                Block.box(2,14,4,14,15,3)));
        map.put(Direction.WEST, VoxelShapes.or(base, plate,
                                                Block.box(11,8,0,14,16,2),
                                                Block.box(11,8,14,14,16,16),
                                                Block.box(12,14,2,13,15,14)));
        map.put(Direction.EAST, VoxelShapes.or(base, plate,
                                               Block.box(5,8,0,2,16,2),
                                               Block.box(5,8,14,2,16,16),
                                               Block.box(4,14,2,3,15,14)));

        return map;
    });

    TurretAssemblyBlock() {
        super(Properties.of(Material.STONE, MaterialColor.CLAY).strength(4.25F).sound(SoundType.STONE)
                        .requiresCorrectToolForDrops().noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext useContext) {
        return this.defaultBlockState().setValue(FACING, useContext.getHorizontalDirection().getOpposite()).setValue(ENABLED, true);
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        if( !oldState.is(state.getBlock()) ) {
            TurretAssemblyEntity assembly = (TurretAssemblyEntity) level.getBlockEntity(pos);

            if( assembly != null ) {
                InventoryUtils.dropBlockItems(assembly.getInventory(), level, pos);
            }

            level.updateNeighbourForOutputSignal(pos, this);
            super.onRemove(state, level, pos, oldState, isMoving);
        }
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull PlayerEntity player,
                                @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit)
    {
        if( player instanceof ServerPlayerEntity ) {
            TileEntity tileentity = level.getBlockEntity(pos);
            if( tileentity instanceof TurretAssemblyEntity ) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileentity, b -> b.writeBlockPos(pos));
            }

            return ActionResultType.CONSUME;
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(@Nonnull World level, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, ItemStack stack) {
        if( stack.hasCustomHoverName() ) {
            TileEntity te = level.getBlockEntity(pos);
            if( te instanceof TurretAssemblyEntity ) {
                ((TurretAssemblyEntity) te).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TurretAssemblyEntity();
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return BLOCK_SHAPES.getOrDefault(state.getValue(FACING), VoxelShapes.block());
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return Container.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return MiscUtils.apply(world.getBlockEntity(pos), te -> ((TurretAssemblyEntity) te).hasRedstoneUpgrade(), false);
    }

    @Override
    public void onPlace(BlockState currState, @Nonnull World level, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if( !newState.is(currState.getBlock()) ) {
            this.checkPoweredState(level, pos, currState);
        }
    }

    @Override
    public void neighborChanged(@Nonnull BlockState currState, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
                                @Nonnull BlockPos neighborPos, boolean isMoving)
    {
        this.checkPoweredState(level, pos, currState);
    }

    private void checkPoweredState(World level, BlockPos pos, BlockState currState) {
        boolean noSignal = !level.hasNeighborSignal(pos);
        if( noSignal != Boolean.TRUE.equals(currState.getValue(ENABLED)) ) {
            level.setBlock(pos, currState.setValue(ENABLED, noSignal || !this.canConnectRedstone(currState, level, pos, null)), Constants.BlockFlags.NO_RERENDER);
        }
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return PushReaction.BLOCK;
    }
}
