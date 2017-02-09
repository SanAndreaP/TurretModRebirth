/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;

public class PacketPlayerTurretAction
        extends AbstractMessage<PacketPlayerTurretAction>
{
    public static final byte TOGGLE_ACTIVE = 0;
    public static final byte DISMANTLE = 1;

    private int turretId;
    private byte actionId;

    @SuppressWarnings("unused")
    public PacketPlayerTurretAction() { }

    public PacketPlayerTurretAction(EntityTurret turret, byte action) {
        this.turretId = turret.getEntityId();
        this.actionId = action;
    }

    @Override
    public void handleClientMessage(PacketPlayerTurretAction packet, EntityPlayer player) { }

    @Override
    public void handleServerMessage(PacketPlayerTurretAction packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret) {
            EntityTurret turret = (EntityTurret) e;
            if( !turret.hasPlayerPermission(player) ) {
                return;
            }

            switch( packet.actionId ) {
                case DISMANTLE:
                    tryDismantle(player, turret);
                    break;
                case TOGGLE_ACTIVE:
                    turret.setActive(!turret.isActive());
            }
        }
    }

    public static boolean tryDismantle(EntityPlayer player, EntityTurret turret) {
        Tuple chestItm = InventoryUtils.getSimilarStackFromInventory(new ItemStack(Blocks.CHEST), player.inventory, true);
        if( chestItm != null && ItemStackUtils.isValid(chestItm.getValue(1)) ) {
            ItemStack chestStack = chestItm.getValue(1);
            if( turret.world.isRemote ) {
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turret, PacketPlayerTurretAction.DISMANTLE));
                return true;
            } else {
//                turret.checkBlock = false;
//                turret.posY += 2048.0F;
//                turret.setPosition(turret.posX, turret.posY, turret.posZ);
//                turret.world.loadedEntityList.remove(turret);
                int y = turret.isUpsideDown ? 3 : 0;
                BlockPos chestPos = turret.getPosition();
                if( turret.world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 3) )
                {
                    TileEntity te = turret.world.getTileEntity(chestPos);

                    if( te instanceof TileEntityChest ) {
//                        turret.world.loadedEntityList.add(turret);

                        TileEntityChest chest = (TileEntityChest) te;
                        chest.setInventorySlotContents(0, ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(turret.getClass()), turret));
                        ((TargetProcessor) turret.getTargetProcessor()).putAmmoInInventory(chest);

                        if( --chestStack.stackSize < 1 ) {
                            player.inventory.setInventorySlotContents(chestItm.getValue(0), null);
                        } else {
                            player.inventory.setInventorySlotContents(chestItm.getValue(0), chestStack.copy());
                        }
                        player.inventoryContainer.detectAndSendChanges();
                        //TODO: make custom container for turrets and put upgrades in it
                        ((UpgradeProcessor) turret.getUpgradeProcessor()).dropUpgrades();
                        turret.kill();
                        return true;
                    }
                }
//                this.checkBlock = true;
//                this.posY -= 2048.0F;
//                this.setPosition(this.posX, this.posY, this.posZ);
//                turret.world.loadedEntityList.add(turret);
            }
        }

        return false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.actionId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeByte(this.actionId);
    }
}
