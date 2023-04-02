/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.client.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.client.util.TurretMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemTransmitter
        extends Block
{
    public BlockItemTransmitter() {
        super(Material.iron);
        this.setBlockName(TurretMod.MOD_ID + ":item_transmitter");
        this.setCreativeTab(TmrCreativeTabs.MISC);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityItemTransmitter();
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
}
