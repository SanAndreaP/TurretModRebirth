package sanandreasp.mods.TurretMod3.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

public class PacketRecvTurretShootKey extends PacketBase {

	@Override
	public void handle(EntityPlayer player) {
		if (player.ridingEntity != null && player.ridingEntity instanceof EntityTurret_Base) {
			EntityTurret_Base turret = (EntityTurret_Base)player.ridingEntity;
			turret.tryToShoot(true);
		}
	}

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
