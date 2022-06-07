package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.smarttargeting.AdvTargetSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SmartTargetingActionPacket
        extends SimpleMessage
{
    public static final byte SET_TURRET_AWS = 0;
    public static final byte SET_TAMED_AWS = 1;
    public static final byte SET_CHILD_AWS = 2;
    public static final byte SET_COUNT_AWS = 3;
    public static final byte SET_COUNT_AMOUNT = 4;

    private final int turretId;
    private final byte actionId;
    private final AdvTargetSettings.TurretAwareness turretAwareness;
    private final AdvTargetSettings.TamedAwareness tamedAwareness;
    private final AdvTargetSettings.ChildAwareness childAwareness;
    private final AdvTargetSettings.CountAwareness countAwareness;
    private final short count;

    private  SmartTargetingActionPacket(ITurretEntity turret,
                                        AdvTargetSettings.TurretAwareness turretAwareness,
                                        AdvTargetSettings.TamedAwareness tamedAwareness,
                                        AdvTargetSettings.ChildAwareness childAwareness,
                                        AdvTargetSettings.CountAwareness countAwareness,
                                        short count)
    {
        this.turretId = turret.get().getId();
        this.turretAwareness = turretAwareness;
        this.tamedAwareness = tamedAwareness;
        this.childAwareness = childAwareness;
        this.countAwareness = countAwareness;
        this.count = count;

        if( turretAwareness != null ) {
            this.actionId = SET_TURRET_AWS;
        } else if( tamedAwareness != null ) {
            this.actionId = SET_TAMED_AWS;
        } else if( childAwareness != null ) {
            this.actionId = SET_CHILD_AWS;
        } else if( countAwareness != null ) {
            this.actionId = SET_COUNT_AWS;
        } else if( count >= 0 ) {
            this.actionId = SET_COUNT_AMOUNT;
        } else {
            this.actionId = -1;
        }
    }

    public SmartTargetingActionPacket(ITurretEntity turret, AdvTargetSettings.TurretAwareness awareness) {
        this(turret, awareness, null, null, null, (short) -1);
    }

    public SmartTargetingActionPacket(ITurretEntity turret, AdvTargetSettings.TamedAwareness awareness) {
        this(turret, null, awareness, null, null, (short) -1);
    }

    public SmartTargetingActionPacket(ITurretEntity turret, AdvTargetSettings.ChildAwareness awareness) {
        this(turret, null, null, awareness, null, (short) -1);
    }

    public SmartTargetingActionPacket(ITurretEntity turret, AdvTargetSettings.CountAwareness awareness) {
        this(turret, null, null, null, awareness, (short) -1);
    }

    public SmartTargetingActionPacket(ITurretEntity turret, short count) {
        this(turret, null, null, null, null, count);
    }

    public SmartTargetingActionPacket(PacketBuffer buffer) {
        this.turretId = buffer.readVarInt();
        this.actionId = buffer.readByte();

        this.turretAwareness = this.actionId == SET_TURRET_AWS ? AdvTargetSettings.TurretAwareness.fromIndex(buffer.readByte()) : null;
        this.tamedAwareness = this.actionId == SET_TAMED_AWS ? AdvTargetSettings.TamedAwareness.fromIndex(buffer.readByte()) : null;
        this.childAwareness = this.actionId == SET_CHILD_AWS ? AdvTargetSettings.ChildAwareness.fromIndex(buffer.readByte()) : null;
        this.countAwareness = this.actionId == SET_COUNT_AWS ? AdvTargetSettings.CountAwareness.fromIndex(buffer.readByte()) : null;
        this.count = this.actionId == SET_COUNT_AMOUNT ? buffer.readShort() : -1;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.turretId);
        buffer.writeByte(this.actionId);

        switch( this.actionId ) {
            case SET_TURRET_AWS: buffer.writeByte(this.turretAwareness.getIndex()); break;
            case SET_TAMED_AWS: buffer.writeByte(this.tamedAwareness.getIndex()); break;
            case SET_CHILD_AWS: buffer.writeByte(this.childAwareness.getIndex()); break;
            case SET_COUNT_AWS: buffer.writeByte(this.countAwareness.getIndex()); break;
            case SET_COUNT_AMOUNT: buffer.writeShort(this.count); break;
            default: // no-op
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

            AdvTargetSettings tgtSettings = turretInst.getUpgradeProcessor().getUpgradeData(Upgrades.SMART_TGT.getId());
            if( tgtSettings == null ) {
                return;
            }
            switch( this.actionId ) {
                case SET_TURRET_AWS: tgtSettings.setTurretAwareness(this.turretAwareness); break;
                case SET_TAMED_AWS: tgtSettings.setTamedAwareness(this.tamedAwareness); break;
                case SET_CHILD_AWS: tgtSettings.setChildAwareness(this.childAwareness); break;
                case SET_COUNT_AWS: tgtSettings.setCountAwareness(this.countAwareness); break;
                case SET_COUNT_AMOUNT: tgtSettings.setCountEntities(this.count); break;
                default: // no-op
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
