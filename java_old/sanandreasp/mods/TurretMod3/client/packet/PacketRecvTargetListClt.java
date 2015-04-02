package sanandreasp.mods.TurretMod3.client.packet;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.packet.PacketBase;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.Map;

public class PacketRecvTargetListClt extends PacketBase {
    private int eID;
    private NBTTagCompound nbt;
    public PacketRecvTargetListClt(){}
    public PacketRecvTargetListClt(int entity, NBTTagCompound tag){
        eID = entity;
        nbt = tag;
    }

	@Override
	public void handle(EntityPlayer player) {
        Map<String, Boolean> tgt = Maps.newHashMap();
        NBTTagList var1 = nbt.getTagList("targetsTag", Constants.NBT.TAG_COMPOUND);

        for (int var2 = 0; var2 < var1.tagCount(); ++var2)
        {
            NBTTagCompound var3 = var1.getCompoundTagAt(var2);
            tgt.put(var3.getString("tgName"), var3.getBoolean("isEnabled"));
        }
        TM3ModRegistry.proxy.handleTargetList(player.worldObj.getEntityByID(eID), tgt);
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
