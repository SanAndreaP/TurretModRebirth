/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.block;

import com.google.common.collect.ImmutableMap;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockTurretCrate
        extends Block
{
    BlockTurretCrate() {
        super(Material.IRON);
        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setHardness(4.25F);
        this.blockSoundType = SoundType.METAL;
        this.setTranslationKey(TmrConstants.ID + ":turret_crate");
        this.setRegistryName(TmrConstants.ID, "turret_crate");
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityTurretCrate crate = (TileEntityTurretCrate) world.getTileEntity(pos);

        if( crate != null ) {
            TmrUtils.dropBlockItems(crate.getInventory(), world, pos);
        }

        world.updateComparatorOutputLevel(pos, this);
        super.breakBlock(world, pos, state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return TmrUtils.buildCustomBlockStateContainer(this, MyStateImplementation::new);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.TCRATE, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        if( stack.hasDisplayName() ) {
            TileEntity tileentity = world.getTileEntity(pos);

            if( tileentity instanceof TileEntityTurretCrate ) {
                ((TileEntityTurretCrate) tileentity).setCustomName(stack.getDisplayName());
            }
        }
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTurretCrate();
    }

    private static final class MyStateImplementation
            extends BlockStateContainer.StateImplementation
    {
        MyStateImplementation(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
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

        @Override
        public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
            return BlockFaceShape.UNDEFINED;
        }
    }
}
