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
import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TurretPlayerActionPacket
        extends SimpleMessage
{
    public static final  byte SET_ACTIVE = 0;
    public static final  byte SET_DEACTIVE = 1;
    public static final byte RETRIEVE_XP = 2;
    private static final byte DISMANTLE = 3;
    private static final byte RENAME = 4;

    private final int  turretId;
    private final byte actionId;

    private String customName;

    public TurretPlayerActionPacket(ITurretEntity turret, byte action) {
        this.turretId = turret.get().getId();
        this.actionId = action;
    }

    private TurretPlayerActionPacket(ITurretEntity turret, String cstName) {
        this.turretId = turret.get().getId();
        this.actionId = RENAME;
        this.customName = cstName != null && cstName.length() > 0 ? cstName : null;
    }

    public TurretPlayerActionPacket(PacketBuffer buffer) {
        this.turretId = buffer.readVarInt();
        this.actionId = buffer.readByte();

        if( this.actionId == RENAME ) {
            this.customName = buffer.readBoolean() ? buffer.readUtf(260) : null;
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.turretId);
        buffer.writeByte(this.actionId);

        if( this.actionId == RENAME ) {
            if( this.customName == null ) {
                buffer.writeBoolean(false);
            } else {
                buffer.writeBoolean(true);
                buffer.writeUtf(this.customName, 260);
            }
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = supplier.get().getSender();
        if( player == null ) { // if this is not sent from a player, do nothing!
            return;
        }

        Entity e = player.level.getEntity(this.turretId);
        if( e instanceof ITurretEntity) {
            ITurretEntity turretInst = (ITurretEntity) e;
            if( !turretInst.hasPlayerPermission(player) ) {
                return;
            }

            switch( this.actionId ) {
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
                    //TODO: reimplement upgrades
//                    LevelStorage lvlStg = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.LEVELING.getId());
//                    if( lvlStg != null ) {
//                        player.addExperience(lvlStg.retrieveExcessXp());
//                    }
                    break;
                case RENAME:
                    rename(turretInst, this.customName);
                    break;
                default:
                    // no-op
            }
        }

    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }

    public static void rename(ITurretEntity turret, String cstName) {
        if( turret.get().level.isClientSide ) {
            TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(turret, cstName));
        } else {
            turret.get().setCustomName(cstName != null ? new StringTextComponent(cstName) : null);
        }
    }

    public static boolean tryDismantle(PlayerEntity player, ITurretEntity turret) {
        Tuple crateItm = InventoryUtils.getSimilarStackFromInventory(new ItemStack(BlockRegistry.TURRET_CRATE), player.inventory, false);
        if( crateItm != null && ItemStackUtils.isValid(crateItm.getValue(1)) ) {
            ItemStack    crateStack = crateItm.getValue(1);
            LivingEntity turretL    = turret.get();
            if( turretL.level.isClientSide ) {
                TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(turret, DISMANTLE));
                return true;
            } else {
                if( turret.dismantle() != null ) {
                    crateStack.shrink(1);
                    if( crateStack.getCount() < 1 ) {
                        player.inventory.setItem(crateItm.getValue(0), ItemStack.EMPTY);
                    } else {
                        player.inventory.setItem(crateItm.getValue(0), crateStack.copy());
                    }
                    player.inventoryMenu.broadcastChanges();

                    return true;
                }
            }
        }

        return false;
    }
}
