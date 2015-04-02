package sanandreasp.mods.TurretMod3.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvUpgrades;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class PacketSendUpgrades extends PacketBase {
    private int eID;
    public PacketSendUpgrades(){}
    private PacketSendUpgrades(int entity){
        eID = entity;
    }
	@Override
	public void handle(EntityPlayer player) {
        EntityTurret_Base turret = (EntityTurret_Base) player.worldObj.getEntityByID(eID);
        if (turret == null || turret.upgrades == null) return;
        if(player instanceof EntityPlayerMP) {
            IMessage packetTrans = new PacketRecvUpgrades(turret);
            TM3ModRegistry.networkWrapper.sendTo(packetTrans, (EntityPlayerMP)player);
        }
	}

	public static void send(EntityTurret_Base etb) {
        TM3ModRegistry.networkWrapper.sendToServer(new PacketSendUpgrades(etb.getEntityId()));
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        eID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(eID);
    }
}
