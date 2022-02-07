/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.turretmod.init.TurretModRebirth;

public final class PacketRegistry
{
    public static void initialize() {
        TurretModRebirth.NETWORK.registerMessage(0, SyncPlayerListPacket.class, SyncPlayerListPacket::new);
        TurretModRebirth.NETWORK.registerMessage(1, SyncTurretStatePacket.class, SyncTurretStatePacket::new);
        TurretModRebirth.NETWORK.registerMessage(2, SyncTurretTargetsPacket.class, SyncTurretTargetsPacket::new);
        TurretModRebirth.NETWORK.registerMessage(3, OpenRemoteTcuGuiPacket.class, OpenRemoteTcuGuiPacket::new);
        TurretModRebirth.NETWORK.registerMessage(4, TurretPlayerActionPacket.class, TurretPlayerActionPacket::new);
        TurretModRebirth.NETWORK.registerMessage(5, SyncUpgradesPacket.class, SyncUpgradesPacket::new);
//        TurretModRebirth.network.registerMessage(1,  PacketUpdateTargets.class, PacketUpdateTargets::new);
//        TurretModRebirth.network.registerMessage(2,  PacketUpdateTurretState.class, PacketUpdateTurretState::new);
//        TurretModRebirth.network.registerMessage(3,  PacketPlayerTurretAction.class, PacketPlayerTurretAction::new);
//        TurretModRebirth.network.registerMessage(4,  PacketSyncTileEntity.class, PacketSyncTileEntity::new);
//        TurretModRebirth.network.registerMessage(5,  PacketInitAssemblyCrafting.class, PacketInitAssemblyCrafting::new);
//        TurretModRebirth.network.registerMessage(6,  PacketAssemblyToggleAutomate.class, PacketAssemblyToggleAutomate::new);
//        TurretModRebirth.network.registerMessage(7,  PacketOpenGui.class, PacketOpenGui::new);
//        TurretModRebirth.network.registerMessage(8,  PacketUpdateUgradeSlot.class, PacketUpdateUgradeSlot::new);
//        TurretModRebirth.network.registerMessage(9,  PacketTurretNaming.class, PacketTurretNaming::new);
//        TurretModRebirth.network.registerMessage(10, PacketSyncUpgradeInst.class, PacketSyncUpgradeInst::new);
//        TurretModRebirth.network.registerMessage(11, PacketSyncTcuGuis.class, PacketSyncTcuGuis::new);
//        TurretModRebirth.network.registerMessage(12, PacketSyncAttackTarget.class, PacketSyncAttackTarget::new);
//        TurretModRebirth.network.registerMessage(13, PacketEffect.class, PacketEffect::new);
    }
}
