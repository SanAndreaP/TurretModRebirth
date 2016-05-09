/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import cofh.api.item.IToolHammer;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockTurretAssembly
        extends Block
{
    protected BlockTurretAssembly() {
        super(Material.rock);
        setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setBlockName(TurretModRebirth.ID + ":turret_assembly");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offX, float offY, float offZ) {
        if( !world.isRemote ) {
            ItemStack held = player.getHeldItem();
            if( ItemStackUtils.isValidStack(held) && held.getItem() instanceof IToolHammer && !player.isSneaking() ) {
                int meta = (world.getBlockMetadata(x, y, z) + 1) & 3;
                world.setBlockMetadataWithNotify(x, y, z, meta, 2);
            } else {
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_MAN, x, y, z);
            }
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack stack) {
        int dir = MathHelper.floor_double((livingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        world.setBlockMetadataWithNotify(x, y, z, dir, 2);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
        TileEntityTurretAssembly assembly = (TileEntityTurretAssembly) world.getTileEntity(x, y, z);

        if( assembly != null ) {
            for( int i = 0; i < assembly.getSizeInventory(); i++ ) {
                ItemStack stack = assembly.getStackInSlot(i);

                if( ItemStackUtils.isValidStack(stack) ) {
                    float xOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;
                    float yOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;
                    float zOff = TmrUtils.RNG.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(world, (x + xOff), (y + yOff), (z + zOff), stack.copy());

                    float motionSpeed = 0.05F;
                    entityitem.motionX = ((float)TmrUtils.RNG.nextGaussian() * motionSpeed);
                    entityitem.motionY = ((float)TmrUtils.RNG.nextGaussian() * motionSpeed + 0.2F);
                    entityitem.motionZ = ((float)TmrUtils.RNG.nextGaussian() * motionSpeed);
                    world.spawnEntityInWorld(entityitem);
                }
            }

            world.func_147453_f(x, y, z, oldBlock);
        }

        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTurretAssembly();
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

    public int getDirection(int meta) {
        return meta & 3;
    }
}
