package sanandreasp.mods.TurretMod3.packet;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.Map;
import java.util.Map.Entry;

public class PacketRecvTargetListSrv extends PacketBase {
    private int eID;
    private NBTTagCompound nbt;
    public PacketRecvTargetListSrv(){}
    private PacketRecvTargetListSrv(int entity, NBTTagCompound tag){
        eID = entity;
        nbt = tag;
    }
	@Override
	public void handle(EntityPlayer player) {
        EntityTurret_Base turret = (EntityTurret_Base) player.worldObj.getEntityByID(eID);
        if(turret!=null) {
            Map<String, Boolean> tgt = Maps.newHashMap();
            NBTTagList var1 = nbt.getTagList("targetsTag", Constants.NBT.TAG_COMPOUND);

            for (int var2 = 0; var2 < var1.tagCount(); ++var2)
            {
                NBTTagCompound var3 = var1.getCompoundTagAt(var2);
                tgt.put(var3.getString("tgName"), var3.getBoolean("isEnabled"));
            }

            turret.targets = tgt;
        }
	}

	private static PacketBase getPacket(EntityTurret_Base turret) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (Entry<String, Boolean> target : turret.targets.entrySet()) {
			NBTTagCompound var4 = new NBTTagCompound();
            var4.setString("tgName", target.getKey());
            var4.setBoolean("isEnabled", target.getValue());
            list.appendTag(var4);
		}
		nbt.setTag("targetsTag", list);
        return new PacketRecvTargetListSrv(turret.getEntityId(), nbt);
	}

	public static void sendClient(EntityTurret_Base turret, EntityPlayer player) {
    	TM3ModRegistry.networkWrapper.sendTo(getPacket(turret), (EntityPlayerMP)player);
	}

	public static void sendServer(EntityTurret_Base turret) {
    	TM3ModRegistry.networkWrapper.sendToServer(getPacket(turret));
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        eID = buf.readInt();
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(eID);
        ByteBufUtils.writeTag(buf, nbt);
    }
}
