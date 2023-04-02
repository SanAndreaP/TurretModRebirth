package sanandreasp.mods.TurretMod3.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sanandreasp.mods.turretmod3.entity.EntityMobileBase;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

public class PacketRecvTurretSettings extends PacketBase {
    private int eID;
    private byte type;
    private int freq;
    public PacketRecvTurretSettings(){}
    public PacketRecvTurretSettings(int entity, byte action){
        eID = entity;
        type = action;
    }
    public PacketRecvTurretSettings(int entity, byte action, int frequency){
        this(entity, action);
        freq = frequency;
    }

	@Override
	public void handle(EntityPlayer player) {
        EntityTurret_Base turret = (EntityTurret_Base) player.worldObj.getEntityByID(eID);
        if(turret==null)
            return;
			switch(type) {
				case 0x0:
					turret.dismantle();
					break;
				case 0x1:
					turret.setUniqueTargets(!turret.useUniqueTargets());
					break;
				case 0x2:
					player.addExperience(turret.getExperience());
					turret.remExperience();
					break;
				case 0x3:
					EntityMobileBase base = (EntityMobileBase) (turret.ridingEntity instanceof EntityMobileBase ? turret.ridingEntity : null);
					if (base != null) {
						turret.mountEntity(null);
						turret.posY -= 0.8D;
						lblPos:
						for (int x = -1; x <= 1; x++) {
							for (int z = -1; z <= 1; z++) {
								if (Math.abs(x) + Math.abs(z) != 0) {
									double pX = turret.posX + x;
									double pZ = turret.posZ + z;
									double pY = turret.posY;

									base.setPosition(pX, pY, pZ);
									if (!base.isEntityInsideOpaqueBlock())
										break lblPos;
								}
							}
						}
						if (base.isEntityInsideOpaqueBlock())
							base.setPosition(turret.posX, turret.posY, turret.posZ);
					}
					break;
				case 0x4:
					player.mountEntity(turret);
					break;
				case 0x5:
					try {
						turret.setFrequency(freq);
					} catch (NumberFormatException nfe) {
						;
					}
					break;
				case 0x6:
					turret.setActiveState(!turret.isActive());
					break;
			}
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        eID = buf.readInt();
        type = buf.readByte();
        freq = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(eID);
        buf.writeByte(type);
        buf.writeInt(freq);
    }
}
