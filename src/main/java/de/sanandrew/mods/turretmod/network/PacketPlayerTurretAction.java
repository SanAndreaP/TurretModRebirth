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
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketPlayerTurretAction
        extends AbstractMessage<PacketPlayerTurretAction>
{
    public static final byte SET_ACTIVE = 0;
    private static final byte DISMANTLE = 1;
    public static final byte SET_DEACTIVE = 2;
    public static final byte RETRIEVE_XP = 3;

    private int turretId;
    private byte actionId;

    @SuppressWarnings("unused")
    public PacketPlayerTurretAction() { }

    public PacketPlayerTurretAction(ITurretInst turretInst, byte action) {
        this.turretId = turretInst.get().getEntityId();
        this.actionId = action;
    }

    @Override
    public void handleClientMessage(PacketPlayerTurretAction packet, EntityPlayer player) { }

    @Override
    public void handleServerMessage(PacketPlayerTurretAction packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof ITurretInst) {
            ITurretInst turretInst = (ITurretInst) e;
            if( !turretInst.hasPlayerPermission(player) ) {
                return;
            }

            switch( packet.actionId ) {
                case DISMANTLE:
                    tryDismantle(player, turretInst);
                    break;
                case SET_ACTIVE:
                    turretInst.setActive(true);
                    break;
                case SET_DEACTIVE:
                    turretInst.setActive(false);
                    break;
                case RETRIEVE_XP:
                    LevelStorage lvlStg = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.LEVELING.getId());
                    if( lvlStg != null ) {
                        player.addExperience(lvlStg.retrieveExcessXp());
                    }
            }
        }
    }

    public static boolean tryDismantle(EntityPlayer player, ITurretInst turretInst) {
        Tuple crateItm = InventoryUtils.getSimilarStackFromInventory(new ItemStack(BlockRegistry.TURRET_CRATE), player.inventory, false);
        if( crateItm != null && ItemStackUtils.isValid(crateItm.getValue(1)) ) {
            ItemStack crateStack = crateItm.getValue(1);
            EntityLiving turretL = turretInst.get();
            if( turretL.world.isRemote ) {
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.DISMANTLE));
                return true;
            } else {
                if( turretInst.dismantle() != null ) {
                    crateStack.shrink(1);
                    if( crateStack.getCount() < 1 ) {
                        player.inventory.setInventorySlotContents(crateItm.getValue(0), ItemStackUtils.getEmpty());
                    } else {
                        player.inventory.setInventorySlotContents(crateItm.getValue(0), crateStack.copy());
                    }
                    player.inventoryContainer.detectAndSendChanges();

                    return true;
                }
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
