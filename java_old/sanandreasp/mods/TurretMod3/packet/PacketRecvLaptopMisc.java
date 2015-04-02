package sanandreasp.mods.TurretMod3.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class PacketRecvLaptopMisc extends PacketBase {
    private int x,y,z;
    private String name;
    private int freq;
    public PacketRecvLaptopMisc(){}
    public PacketRecvLaptopMisc(TileEntity te, String name, int freq){
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
        this.name = name;
        this.freq = freq;
    }

	@Override
	public void handle(EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityLaptop) {
            ((TileEntityLaptop)te).programItemsNameAndFreq(name, freq);
            player.openContainer.detectAndSendChanges();
        }
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.name = ByteBufUtils.readUTF8String(buf);
        this.freq = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeShort(freq);
    }
}
