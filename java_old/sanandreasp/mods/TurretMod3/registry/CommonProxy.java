package sanandreasp.mods.TurretMod3.registry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvPlayerNBT;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvSpawnParticle;

import java.util.Map;

public class CommonProxy {

	public void registerRenderInformation() {
	}

	public void initTM3PlayerTag(EntityPlayer player) {
		NBTTagCompound playerNBT = player.getEntityData();
		NBTTagCompound persNBT = new NBTTagCompound();
		if (!playerNBT.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			playerNBT.setTag(EntityPlayer.PERSISTED_NBT_TAG, persNBT);
		} else {
			persNBT = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		}

		if (!persNBT.hasKey("TurretMod3NBT")) {
			persNBT.setTag("TurretMod3NBT", new NBTTagCompound());
		}

		if (!player.worldObj.isRemote && MinecraftServer.getServer() != null) {
			boolean b = MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
			this.getPlayerTM3Data(player).setBoolean("isOP", b);
			sendTM3NBTToClient(player);
		}
	}

	public NBTTagCompound getPlayerTM3Data(EntityPlayer player) {
		return player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("TurretMod3NBT");
	}

	public void setPlayerTM3Data(EntityPlayer player, NBTTagCompound nbt) {
		player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag("TurretMod3NBT", nbt);
	}

	private void sendTM3NBTToClient(EntityPlayer player) {
        if(player instanceof EntityPlayerMP) {
            IMessage packetTrans = new PacketRecvPlayerNBT(getPlayerTM3Data(player));
            TM3ModRegistry.networkWrapper.sendTo(packetTrans, (EntityPlayerMP)player);
        }
	}

	public void spawnParticle(int ID, double partX, double partY, double partZ, int dist, int dimID, Entity entity) {
        if(entity!=null)
            TM3ModRegistry.networkWrapper.sendToAllAround(new PacketRecvSpawnParticle((short)ID, entity.getEntityId(), partX, partY, partZ), new NetworkRegistry.TargetPoint(dimID, partX, partY, partZ, dist));
	}

    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
        FMLCommonHandler.instance().bus().register(new SchedTickHandlerWorld());
    }

    public EntityPlayer getPlayer(MessageContext context) {
        return context.getServerHandler().playerEntity;
    }

    public void handleSpawnParticles(PacketRecvSpawnParticle packetRecvSpawnParticle, Entity entity, EntityPlayer player) {

    }

    public void handleTargetList(Entity entityByID, Map<String, Boolean> tgt) {

    }
}
