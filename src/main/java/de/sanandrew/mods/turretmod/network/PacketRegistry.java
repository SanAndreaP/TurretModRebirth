/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public final class PacketRegistry
{
    public static void initialize() {
        TurretModRebirth.NETWORK.registerMessage(0, SyncPlayerListPacket.class, SyncPlayerListPacket::new);
        TurretModRebirth.NETWORK.registerMessage(1, SyncTurretStatePacket.class, SyncTurretStatePacket::new);
        TurretModRebirth.NETWORK.registerMessage(2, SyncTurretTargetsPacket.class, SyncTurretTargetsPacket::new);
        TurretModRebirth.NETWORK.registerMessage(3, OpenRemoteTcuGuiPacket.class, OpenRemoteTcuGuiPacket::new);
        TurretModRebirth.NETWORK.registerMessage(4, TurretPlayerActionPacket.class, TurretPlayerActionPacket::new);
        TurretModRebirth.NETWORK.registerMessage(5, SyncUpgradesPacket.class, SyncUpgradesPacket::new);
        TurretModRebirth.NETWORK.registerMessage(6, SyncTurretStages.class, SyncTurretStages::new);
        TurretModRebirth.NETWORK.registerMessage(7, SmartTargetingActionPacket.class, SmartTargetingActionPacket::new);
        TurretModRebirth.NETWORK.registerMessage(8, AssemblyActionPacket.class, AssemblyActionPacket::new);
    }

    public static void writeOptional(boolean doWrite, PacketBuffer buffer, IOErrorConsumer<PacketBuffer> writeValue) {
        buffer.writeBoolean(doWrite);
        if( doWrite ) {
            try {
                writeValue.accept(buffer);
            } catch(IOException ex) { /* ignored */ }
        }
    }

    @FunctionalInterface
    public interface IOErrorConsumer<T>
    {
        void accept(T t) throws IOException;
    }
}
