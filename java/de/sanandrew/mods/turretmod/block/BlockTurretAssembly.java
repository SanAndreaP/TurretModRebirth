/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.block;

import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
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
            TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TASSEMBLY_MAN, x, y, z);
        } else {
//            ((TileEntityTurretAssembly) world.getTileEntity(x, y, z)).active = !((TileEntityTurretAssembly) world.getTileEntity(x, y, z)).active;
        }
        return true;
//        return super.onBlockActivated(world, x, y, z, player, side, offX, offY, offZ);
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
}
