package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PacketEffect
        extends AbstractMessage<PacketEffect>
{
    private EnumEffect effect;
    private double x;
    private double y;
    private double z;
    private Tuple  data;

    private boolean errored;

    public PacketEffect() {}

    public PacketEffect(EnumEffect effect, double x, double y, double z, Tuple data) {
        this.effect = effect;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
    }

    @Override
    public void handleClientMessage(PacketEffect packet, EntityPlayer player) {
        if( !packet.errored ) {
            TurretModRebirth.proxy.addEffect(packet.effect, packet.x, packet.y, packet.z, packet.data);
        }
    }

    @Override
    public void handleServerMessage(PacketEffect packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.effect = EnumEffect.VALUES[buf.readShort()];
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();

        switch( buf.readByte() ) {
            case 0:
                break;
            case 1:
                final int lng = buf.readInt();
                try( ByteArrayInputStream bais = new ByteArrayInputStream(buf.readBytes(lng).array());
                     ObjectInputStream ois = new ObjectInputStream(bais) )
                {
                    this.data = (Tuple) ois.readObject();
                    break;
                } catch( IOException | ClassNotFoundException ex ) {
                    TmrConstants.LOG.log(Level.ERROR, String.format("Cannot deserialize data for effect %s", this.effect.name()), ex);
                    // fall-through
                }
            default:
                this.errored = true;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.effect.ordinal());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        if(this.data != null) {
            final byte[] dataArray;
            try( ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos) )
            {
                oos.writeObject(this.data);
                dataArray = baos.toByteArray();
            } catch( IOException ex ) {
                buf.writeByte(-1);
                TmrConstants.LOG.log(Level.ERROR, String.format("Cannot serialize data for effect %s", this.effect.name()), ex);
                return;
            }
            buf.writeByte(1);
            buf.writeInt(dataArray.length);
            buf.writeBytes(dataArray);
        } else {
            buf.writeByte(0);
        }
    }

    public static void addEffect(EnumEffect effect, int dimension, double x, double y, double z, Tuple data) {
        PacketRegistry.sendToAllAround(new PacketEffect(effect, x, y, z, data), dimension, x, y, z, 64.0D);
    }
}
