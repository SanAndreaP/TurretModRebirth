/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.world;

import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.util.ForcefieldProvider;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import net.darkhax.bookshelf.lib.ColorObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class ClientWorldEventListener
        implements IWorldEventListener
{
    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {

    }

    @Override
    public void notifyLightSet(BlockPos pos) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {

    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {

    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {

    }

    @Override
    public void onEntityAdded(Entity entityIn) {
//        if( entityIn instanceof EntityTurretCryolator ) {
//            RenderForcefieldHandler.INSTANCE.addForcefieldRenderer(entityIn, new ForcefieldProvider()
//            {
//                @Override
//                public boolean hasShieldActive() {
//                    return true;
//                }
//
//                @Override
//                public AxisAlignedBB getShieldBoundingBox() {
//                    return new AxisAlignedBB(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F);
//                }
//
//                @Override
//                public ColorObject getShieldColor() {
//                    return new ColorObject(0.2F, 0.2F, 1.0F, 1.0F);
//                }
//            });
//        }
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {

    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {

    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {

    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {

    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(this);
    }
}
