package sanandreasp.mods.TurretMod3.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.List;

public class PacketRecvLaptopGeneralStg extends PacketBase {
    private int id, freq;
    private boolean active;
    public PacketRecvLaptopGeneralStg(){}
    public PacketRecvLaptopGeneralStg(int id){
        this.id = id;
    }
    public PacketRecvLaptopGeneralStg(int id, int freq){
        this(id);
        this.freq = freq;
    }
    public PacketRecvLaptopGeneralStg(int id, int freq, boolean active){
        this(id, freq);
        this.active = active;
    }

	@Override
	public void handle(EntityPlayer player) {
			switch(id) {
				case 0: {
						NBTTagCompound nbt = TM3ModRegistry.proxy.getPlayerTM3Data(player);
						nbt.setBoolean("renderLabels", !nbt.getBoolean("renderLabels"));
					}
					break;
				case 1: {
						NBTTagCompound nbt = TM3ModRegistry.proxy.getPlayerTM3Data(player);
						byte b = nbt.getByte("tcCrosshair");
						nbt.setByte("tcCrosshair", ++b > 4 ? 0 : b);
					}
					break;
				case 2: {
						List<Entity> entities = player.worldObj.loadedEntityList;
						for (Entity e : entities) {
							if (e instanceof EntityTurret_Base) {
								EntityTurret_Base turret = (EntityTurret_Base) e;
								if (turret.getPlayerName().equalsIgnoreCase(player.getCommandSenderName())) {
									if (freq == -1) {
										turret.setActiveState(active);
									} else if (freq >= 0) {
										if (turret.getFrequency() == freq) {
											turret.setActiveState(active);
										}
									}
								}
							}
						}
					}
					break;
				case 3: {
						List<Entity> entities = player.worldObj.loadedEntityList;
						for (Entity e : entities) {
							if (e instanceof EntityTurret_Base) {
								EntityTurret_Base turret = (EntityTurret_Base) e;
								if (turret.getPlayerName().equalsIgnoreCase(player.getCommandSenderName())) {
									if (freq == -1) {
										turret.resetCurrentTarget();
									} else if (freq >= 0) {
										if (turret.getFrequency() == freq) {
											turret.resetCurrentTarget();
										}
									}
								}
							}
						}
					}
					break;
				case 4: {
						List<Entity> entities = player.worldObj.loadedEntityList;
						for (Entity e : entities) {
							if (e instanceof EntityTurret_Base) {
								EntityTurret_Base turret = (EntityTurret_Base) e;
								if (turret.getPlayerName().equalsIgnoreCase(player.getCommandSenderName())) {
									if (freq == -1) {
										turret.setUniqueTargets(active);
									} else if (freq >= 0) {
										if (turret.getFrequency() == freq) {
											turret.setUniqueTargets(active);
										}
									}
								}
							}
						}
					}
					break;
			}
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
        this.freq = buf.readInt();
        this.active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeInt(this.freq);
        buf.writeBoolean(this.active);
    }
}
