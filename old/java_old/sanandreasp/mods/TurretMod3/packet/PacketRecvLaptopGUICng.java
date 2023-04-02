package sanandreasp.mods.TurretMod3.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class PacketRecvLaptopGUICng extends PacketBase {
    private int guiID, xCoord, yCoord, zCoord;
	@Override
	public void handle(EntityPlayer player) {
        player.openGui(TM3ModRegistry.instance, guiID, player.worldObj, xCoord, yCoord, zCoord);
	}
    public PacketRecvLaptopGUICng() {}
	public PacketRecvLaptopGUICng(int guiID, TileEntity te) {
        this.guiID = guiID;
        this.xCoord = te.xCoord;
        this.yCoord = te.yCoord;
        this.zCoord = te.zCoord;
	}

	public static void sendServer(int guiID, TileEntity te) {
    	TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvLaptopGUICng(guiID, te));
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.guiID = buf.readInt();
        this.xCoord = buf.readInt();
        this.yCoord = buf.readInt();
        this.zCoord = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(guiID);
        buf.writeInt(xCoord);
        buf.writeInt(yCoord);
        buf.writeInt(zCoord);
    }
}
