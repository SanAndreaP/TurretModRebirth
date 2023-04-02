package sanandreasp.mods.TurretMod3.client.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import sanandreasp.mods.turretmod3.packet.PacketBase;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class PacketRecvSpawnParticle extends PacketBase {
    private short ID;
    private float posX, posY, posZ;
    private int eID;
    public PacketRecvSpawnParticle(){}
    public PacketRecvSpawnParticle(short type, int entity, double... pos){
        ID = type;
        eID = entity;
        posX = (float)pos[0];
        posY = (float)pos[1];
        posZ = (float)pos[2];
    }

	@Override
	public void handle(EntityPlayer player) {
        Entity entity = eID > -1 ? player.worldObj.getEntityByID(eID) : null;
        TM3ModRegistry.proxy.handleSpawnParticles(this, entity, player);
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        ID = buf.readShort();
        posX = buf.readFloat();
        posY = buf.readFloat();
        posZ = buf.readFloat();
        eID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(ID);
        buf.writeFloat(posX);
        buf.writeFloat(posY);
        buf.writeFloat(posZ);
        buf.writeInt(eID);
    }

    public short getID() {
        return ID;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosZ() {
        return posZ;
    }
}
