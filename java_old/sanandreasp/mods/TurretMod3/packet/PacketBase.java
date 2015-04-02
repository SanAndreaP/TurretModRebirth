package sanandreasp.mods.TurretMod3.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;

public abstract class PacketBase implements IMessage{

    public abstract void handle(EntityPlayer player);

}
