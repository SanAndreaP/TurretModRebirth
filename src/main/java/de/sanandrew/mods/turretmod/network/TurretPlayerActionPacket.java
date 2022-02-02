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
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class TurretPlayerActionPacket
        extends SimpleMessage
{
    public static final  byte SET_ACTIVE = 0;
    public static final  byte SET_DEACTIVE = 1;
    public static final byte RETRIEVE_XP = 2;
    private static final byte DISMANTLE = 3;
    private static final byte RENAME = 4;
    private static final byte SET_TARGET_CREATURE = 5;
    private static final byte SET_TARGET_PLAYER = 6;

    private final int  turretId;
    private final byte actionId;

    private String customName;

    private boolean              targetActivate;
    private ResourceLocation     targetCreatureId;
    private EntityClassification targetCreatureType;
    private UUID                 targetPlayerId;

    public TurretPlayerActionPacket(ITurretEntity turret, byte action) {
        this.turretId = turret.get().getId();
        this.actionId = action;
    }

    private TurretPlayerActionPacket(ITurretEntity turret, String cstName) {
        this(turret, RENAME);
        this.customName = cstName != null && cstName.length() > 0 ? cstName : null;
    }

    private TurretPlayerActionPacket(ITurretEntity turret, boolean targetActivate, ResourceLocation targetCreatureId, EntityClassification targetCreatureType) {
        this(turret, SET_TARGET_CREATURE);
        this.targetActivate = targetActivate;
        this.targetCreatureId = targetCreatureId;
        this.targetCreatureType = targetCreatureType;
    }

    private TurretPlayerActionPacket(ITurretEntity turret, boolean targetActivate, UUID targetPlayerId) {
        this(turret, SET_TARGET_PLAYER);
        this.targetActivate = targetActivate;
        this.targetPlayerId = targetPlayerId;
    }

    public TurretPlayerActionPacket(PacketBuffer buffer) {
        this.turretId = buffer.readVarInt();
        this.actionId = buffer.readByte();

        if( this.actionId == RENAME ) {
            this.customName = buffer.readBoolean() ? buffer.readUtf(260) : null;
        } else if( this.actionId == SET_TARGET_CREATURE ) {
            this.targetActivate = buffer.readBoolean();
            this.targetCreatureId = buffer.readBoolean() ? buffer.readResourceLocation() : null;
            this.targetCreatureType = buffer.readBoolean() ? readTargetType(buffer) : null;
        } else if( this.actionId == SET_TARGET_PLAYER ) {
            this.targetActivate = buffer.readBoolean();
            this.targetPlayerId = buffer.readBoolean() ? buffer.readUUID() : null;
        }
    }

    private static EntityClassification readTargetType(PacketBuffer buffer) {
        try {
            return buffer.readWithCodec(EntityClassification.CODEC);
        } catch( IOException ex ) { /* ignored */ }

        return null;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.turretId);
        buffer.writeByte(this.actionId);

        if( this.actionId == RENAME ) {
            writeOptional(this.customName != null, buffer, b -> b.writeUtf(this.customName, 260));
        } else if( this.actionId == SET_TARGET_CREATURE ) {
            buffer.writeBoolean(this.targetActivate);
            writeOptional(this.targetCreatureId != null, buffer, b -> b.writeResourceLocation(this.targetCreatureId));
            writeOptional(this.targetCreatureType != null, buffer, b -> b.writeWithCodec(EntityClassification.CODEC, this.targetCreatureType));
        } else if( this.actionId == SET_TARGET_PLAYER ) {
            buffer.writeBoolean(this.targetActivate);
            writeOptional(this.targetPlayerId != null, buffer, b -> b.writeUUID(this.targetPlayerId));
        }
    }

    private static void writeOptional(boolean doWrite, PacketBuffer buffer, IOErrorConsumer<PacketBuffer> writeValue) {
        buffer.writeBoolean(doWrite);
        if( doWrite ) {
            try {
                writeValue.accept(buffer);
            } catch(IOException ex) { /* ignored */ }
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
                case SET_TARGET_CREATURE:
                    setTarget(turretInst, this.targetActivate, this.targetCreatureId, this.targetCreatureType);
                    break;
                case SET_TARGET_PLAYER:
                    setTarget(turretInst, this.targetActivate, this.targetPlayerId);
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

    public static void setTarget(ITurretEntity turret, boolean activated, ResourceLocation creatureId, EntityClassification creatureType) {
        if( turret.get().level.isClientSide ) {
            TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(turret, activated, creatureId, creatureType));
        } else {
            ITargetProcessor tp = turret.getTargetProcessor();
            if( creatureId != null ) {
                tp.updateEntityTarget(creatureId, activated, true);
            } else if( creatureType != null ) {
                tp.updateEntityTargets(creatureType, activated, true);
            } else {
                tp.updateAllEntityTargets(activated, true);
            }
            turret.updateState();
        }
    }

    public static void setTarget(ITurretEntity turret, boolean activated, UUID playerId) {
        if( turret.get().level.isClientSide ) {
            TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(turret, activated, playerId));
        } else {
            ITargetProcessor tp = turret.getTargetProcessor();
            if( playerId != null ) {
                tp.updatePlayerTarget(playerId, activated, true);
            } else {
                tp.updateAllPlayerTargets(activated, true);
            }
            turret.updateState();
        }
    }

    @FunctionalInterface
    public interface IOErrorConsumer<T>
    {
        void accept(T t) throws IOException;
    }
}
