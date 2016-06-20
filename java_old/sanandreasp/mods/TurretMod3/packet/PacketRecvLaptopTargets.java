package sanandreasp.mods.TurretMod3.packet;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

import java.util.Map;
import java.util.Map.Entry;

public class PacketRecvLaptopTargets extends PacketBase {
    private int x, y, z;
    private NBTTagCompound tag;
	@Override
	public void handle(EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityLaptop) {
            Map<String, Boolean> tgt = Maps.newHashMap();
            NBTTagList var1 = tag.getTagList("targetsTag", Constants.NBT.TAG_COMPOUND);

            for (int var2 = 0; var2 < var1.tagCount(); ++var2)
            {
                NBTTagCompound var3 = var1.getCompoundTagAt(var2);
                tgt.put(var3.getString("tgName"), var3.getBoolean("isEnabled"));
            }
            ((TileEntityLaptop)te).programItemsTargets(tgt);
            player.openContainer.detectAndSendChanges();
        }
	}
    public PacketRecvLaptopTargets(){}
	public PacketRecvLaptopTargets(Map<String, Boolean> list, TileEntity te) {
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
		tag = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		for (Entry<String, Boolean> target : list.entrySet()) {
			NBTTagCompound var4 = new NBTTagCompound();
            var4.setString("tgName", target.getKey());
            var4.setBoolean("isEnabled", target.getValue());
            nbtList.appendTag(var4);
		}
		tag.setTag("targetsTag", nbtList);
	}

	public static void sendServer(Map<String, Boolean> list, TileEntity te) {
    	TM3ModRegistry.networkWrapper.sendToServer(new PacketRecvLaptopTargets(list, te));
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeTag(buf, tag);
    }
}
