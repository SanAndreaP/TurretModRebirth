package sanandreasp.mods.TurretMod3.packet;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvPlayerNBT;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvSpawnParticle;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvTargetListClt;
import sanandreasp.mods.turretmod3.client.packet.PacketRecvUpgrades;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.lang.reflect.Field;
import java.util.EnumMap;

public class PacketHandlerCommon implements IMessageHandler<PacketBase, IMessage> {

    public void registerOn(SimpleNetworkWrapper wrapper){
        try {
            Field field = wrapper.getClass().getDeclaredField("packetCodec");
            field.setAccessible(true);
            SimpleIndexedCodec packetCodec = (SimpleIndexedCodec)field.get(wrapper);
            field = wrapper.getClass().getDeclaredField("channels");
            field.setAccessible(true);
            EnumMap<Side, FMLEmbeddedChannel> channels = (EnumMap<Side, FMLEmbeddedChannel>) field.get(wrapper);
            registerMessage(packetCodec, channels, PacketSendUpgrades.class, 0, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvTargetListSrv.class, 1, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvTurretSettings.class, 2, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvTurretShootKey.class, 3, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvLaptopTargets.class, 4, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvLaptopGUICng.class, 5, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvLaptopGeneralStg.class, 6, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvLaptopUpgrades.class, 7, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvLaptopMisc.class, 8, Side.SERVER);
            registerMessage(packetCodec, channels, PacketRecvUpgrades.class, 100, Side.CLIENT);
            registerMessage(packetCodec, channels, PacketRecvPlayerNBT.class, 101, Side.CLIENT);
            registerMessage(packetCodec, channels, PacketRecvTargetListClt.class, 102, Side.CLIENT);
            registerMessage(packetCodec, channels, PacketRecvSpawnParticle.class, 103, Side.CLIENT);
        }catch (Exception e){
            FMLLog.bigWarning("TurretMod 3: Couldn't register network system \n"+e.getMessage());
        }
    }

    @Override
    public IMessage onMessage(PacketBase packet, MessageContext context) {
        EntityPlayer player = TM3ModRegistry.proxy.getPlayer(context);
        packet.handle(player);
        return null;
    }

    private void registerMessage(SimpleIndexedCodec packetCodec, EnumMap<Side, FMLEmbeddedChannel> channels, Class<? extends PacketBase> requestType, int discriminator, Side side)
    {
        packetCodec.addDiscriminator(discriminator, requestType);
        FMLEmbeddedChannel channel = channels.get(side);
        String type = channel.findChannelHandlerNameForType(SimpleIndexedCodec.class);
        SimpleChannelHandlerWrapper handler = new SimpleChannelHandlerWrapper(this, side, requestType);
        channel.pipeline().addAfter(type, this.getClass().getName()+discriminator, handler);
    }
}
