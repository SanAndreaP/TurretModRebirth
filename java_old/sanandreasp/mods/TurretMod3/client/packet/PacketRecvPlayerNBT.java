package sanandreasp.mods.TurretMod3.client.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sanandreasp.mods.turretmod3.packet.PacketBase;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class PacketRecvPlayerNBT extends PacketBase {
    private NBTTagCompound nbt;
    public PacketRecvPlayerNBT(){}
    public PacketRecvPlayerNBT(NBTTagCompound tag){
        nbt = tag;
    }

	@Override
	public void handle(EntityPlayer player) {
        TM3ModRegistry.proxy.initTM3PlayerTag(player);
        TM3ModRegistry.proxy.setPlayerTM3Data(player, nbt);
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }
}
