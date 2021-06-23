package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenRemoteTcuGuiPacket
        extends SimpleMessage
{
    private final String tcuHand;
    private final int              turretNetId;
    private final ResourceLocation pageId;

    public OpenRemoteTcuGuiPacket(ITurretEntity turret, Hand tcuHand, ResourceLocation pageId) {
        this.tcuHand = tcuHand.name();
        this.turretNetId = turret.get().getId();
        this.pageId = pageId;
    }

    public OpenRemoteTcuGuiPacket(PacketBuffer buffer) {
        this.tcuHand = buffer.readUtf(24);
        this.turretNetId = buffer.readVarInt();
        this.pageId = buffer.readResourceLocation();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(this.tcuHand, 24);
        buffer.writeVarInt(this.turretNetId);
        buffer.writeResourceLocation(this.pageId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity player = supplier.get().getSender();
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretNetId);
            ItemStack tcu = player.getItemInHand(Hand.valueOf(this.tcuHand));
            if( e instanceof ITurretEntity && ItemStackUtils.isItem(tcu, ItemRegistry.TURRET_CONTROL_UNIT) ) {
                TurretControlUnit.openTcu(player, tcu, (ITurretEntity) e, this.pageId);
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
