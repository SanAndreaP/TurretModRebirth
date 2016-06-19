/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockElectrolyteGenerator
        extends Block
{
    protected BlockElectrolyteGenerator() {
        super(Material.rock);
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setHardness(4.25F);
        this.setStepSound(soundTypePiston);
        this.setBlockName(TurretModRebirth.ID + ":potato_generator");
    }

    @Override
    @SuppressWarnings("TailRecursion")
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offX, float offY, float offZ) {
        if( !world.isRemote ) {
            if( world.getBlockMetadata(x, y, z) != 0 ) {
                return this.onBlockActivated(world, x, y - 1, z, player, side, offX, offY, offZ);
            } else {
                //            ItemStack held = player.getHeldItem();
                //            if( ItemStackUtils.isValidStack(held) && held.getItem() instanceof IToolHammer && !player.isSneaking() ) {
                //                int meta = (world.getBlockMetadata(x, y, z) + 1) & 3;
                //                world.setBlockMetadataWithNotify(x, y, z, meta, 2);
                //            } else {
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_POTATOGEN, x, y, z);
                //            }
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && world.getBlock(x, y+1, z).isReplaceable(world, x, y+1, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        if( world.getBlockMetadata(x, y, z) == 0 ) {
            world.setBlock(x, y+1, z, this, 1, 3);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return AxisAlignedBB.getBoundingBox(x, y + (meta == 0 ? 0 : -1), z, x + 1, y + (meta == 0 ? 2 : 1), z + 1);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack stack) {
        if( stack.hasDisplayName() && world.getBlockMetadata(x, y, z) == 0 ) {
            ((TileEntityElectrolyteGenerator) world.getTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
        if( oldMeta == 0 ) {
            TileEntityElectrolyteGenerator potatoGen = (TileEntityElectrolyteGenerator) world.getTileEntity(x, y, z);

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

                        EntityItem entityitem = new EntityItem(world, (x + xOff), (y + yOff), (z + zOff), stack.copy());

                        float motionSpeed = 0.05F;
                        entityitem.motionX = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed);
                        entityitem.motionY = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed + 0.2F);
                        entityitem.motionZ = ((float) TmrUtils.RNG.nextGaussian() * motionSpeed);
                        world.spawnEntityInWorld(entityitem);
                    }
                }

                world.func_147453_f(x, y, z, oldBlock);
            }

            if( world.getBlock(x, y+1, z) == this ) {
                world.playAuxSFX(2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y + 1, z)));
                world.setBlockToAir(x, y + 1, z);
            }
        } else if( world.getBlock(x, y - 1, z) == this ) {
            world.playAuxSFX(2001, x, y - 1, z, Block.getIdFromBlock(world.getBlock(x, y - 1, z)));
            world.setBlockToAir(x, y - 1, z);
        }

        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 0;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityElectrolyteGenerator();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = Blocks.anvil.getIcon(0, 0);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return world.getBlockMetadata(x, y, z) == 0 ? Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z)) : 0;
    }
}
