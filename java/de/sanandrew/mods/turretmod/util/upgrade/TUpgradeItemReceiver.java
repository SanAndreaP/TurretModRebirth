/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.UpgradeQueueData;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;

public class TUpgradeItemReceiver
        extends TurretUpgradeBase
{
    public TUpgradeItemReceiver() {
        super("itemReceiver", "upgrades/item_receiver");
    }

    @Override
    public void onApply(Turret turret) {
        turret.registerUpgradeToUpdateQueue(this, new ReceiverData());
    }

    @Override
    public void onLoad(Turret turret, NBTTagCompound nbt) {
        turret.registerUpgradeToUpdateQueue(this, new ReceiverData());
    }

    @Override
    public void onRemove(Turret turret) {
        ReceiverData data = SAPUtils.getCasted(turret.getUpgradeQueueData(this));
        if( data != null && data.currRequestHolder != null ) {
            data.currRequestHolder.removeRequest();
        }
    }

    @Override
    public void onUpdateQueue(Turret turret, UpgradeQueueData queueData) {
        ReceiverData data = SAPUtils.getCasted(queueData);
        EntityLiving entity = turret.getEntity();

        if( !entity.worldObj.isRemote && entity.ticksExisted % 10 == 0 ) {
            if( data.currRequestHolder == null ) {
                AxisAlignedBB rangeBB = turret.getRangeBB();
                ItemStack myAmmo = turret.getAmmoTypeItem();

                if( myAmmo != null && turret.getAmmo() < turret.getMaxAmmo() ) {
                    myAmmo = myAmmo.copy();
                    myAmmo.stackSize = turret.getMaxAmmo() - turret.getAmmo();

                    chunkLoop:
                    for( int chunkX = MathHelper.floor_double(rangeBB.minX) / 16; chunkX <= MathHelper.floor_double(rangeBB.maxX) / 16; chunkX++ ) {
                        for( int chunkZ = MathHelper.floor_double(rangeBB.minZ) / 16; chunkZ <= MathHelper.floor_double(rangeBB.maxZ) / 16; chunkZ++ ) {
                            Chunk chunk = entity.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);

                            if( chunk != null && chunk.chunkTileEntityMap.size() > 0 ) {
                                for( TileEntity tile : SAPUtils.<Collection<TileEntity>>getCasted(chunk.chunkTileEntityMap.values()) ) {
                                    if( tile instanceof TileEntityItemTransmitter && tile.yCoord >= rangeBB.minY && tile.yCoord <= rangeBB.maxY ) {
                                        TileEntityItemTransmitter transmitter = (TileEntityItemTransmitter) tile;

                                        if( !transmitter.hasRequest() ) {
                                            transmitter.requestItem(turret, TileEntityItemTransmitter.RequestType.AMMO, myAmmo);
                                            data.currRequestHolder = transmitter;
                                            break chunkLoop;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if( data.currRequestHolder.isInvalid() ) {
                data.currRequestHolder = null;
            } else if( !data.currRequestHolder.isMyRequest(turret) ) {
                data.currRequestHolder = null;
            }
        }
    }

    public static class ReceiverData
            implements UpgradeQueueData
    {
        public TileEntityItemTransmitter currRequestHolder;
    }
}
