/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
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
    private final boolean initial;

    public OpenRemoteTcuGuiPacket(ITurretEntity turret, Hand tcuHand, ResourceLocation pageId, boolean initial) {
        this.tcuHand = tcuHand.name();
        this.turretNetId = turret.get().getId();
        this.pageId = pageId;
        this.initial = initial;
    }

    public OpenRemoteTcuGuiPacket(PacketBuffer buffer) {
        this.tcuHand = buffer.readUtf(24);
        this.turretNetId = buffer.readVarInt();
        this.pageId = buffer.readResourceLocation();
        this.initial = buffer.readBoolean();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(this.tcuHand, 24);
        buffer.writeVarInt(this.turretNetId);
        buffer.writeResourceLocation(this.pageId);
        buffer.writeBoolean(this.initial);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity player = supplier.get().getSender();
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretNetId);
            ItemStack tcu = player.getItemInHand(Hand.valueOf(this.tcuHand));
            if( e instanceof ITurretEntity && ItemStackUtils.isItem(tcu, ItemRegistry.TURRET_CONTROL_UNIT) ) {
                TurretControlUnit.openTcu(player, tcu, (ITurretEntity) e, this.pageId, this.initial);
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
