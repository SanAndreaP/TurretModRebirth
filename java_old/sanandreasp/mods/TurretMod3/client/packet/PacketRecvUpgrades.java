package sanandreasp.mods.TurretMod3.client.packet;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.packet.PacketBase;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.Map;

public class PacketRecvUpgrades extends PacketBase {
    private int eID;
    private NBTTagCompound nbt;
    public PacketRecvUpgrades(){}
    private PacketRecvUpgrades(int entity, NBTTagCompound tag){
        eID = entity;
        nbt = tag;
    }
	@Override
	public void handle(EntityPlayer player) {
        EntityTurret_Base turret = (EntityTurret_Base) player.worldObj.getEntityByID(eID);
        if (turret != null) {
            NBTTagList var2 = nbt.getTagList("TurretUpgrades", Constants.NBT.TAG_COMPOUND);

            Map<Integer, ItemStack> upgrades = Maps.newHashMap();

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                int var5 = var4.getInteger("UgID");
                ItemStack var6 = ItemStack.loadItemStackFromNBT(var4);
                upgrades.put(var5, var6);
            }
            turret.upgrades = upgrades;
        }
	}

    public PacketRecvUpgrades(EntityTurret_Base turret) {
        eID = turret.getEntityId();
        NBTTagList var2 = new NBTTagList();
        for (int upgradeID : turret.upgrades.keySet())
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setInteger("UgID", upgradeID);
            turret.upgrades.get(upgradeID).writeToNBT(var4);
            var2.appendTag(var4);
        }
        nbt = new NBTTagCompound();
        nbt.setTag("TurretUpgrades", var2);
	}

    public static void send(EntityTurret_Base turret){
        TM3ModRegistry.networkWrapper.sendToDimension(new PacketRecvUpgrades(turret), turret.worldObj.provider.dimensionId);
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
