/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketRegistry
{
    public static void initialize() {
        TurretModRebirth.network.registerMessage(0,  PacketUpdateTargets.class, PacketUpdateTargets::new);
        TurretModRebirth.network.registerMessage(1,  PacketUpdateTurretState.class, PacketUpdateTurretState::new);
        TurretModRebirth.network.registerMessage(2,  PacketPlayerTurretAction.class, PacketPlayerTurretAction::new);
        TurretModRebirth.network.registerMessage(3,  PacketSyncPlayerList.class, PacketSyncPlayerList::new);
        TurretModRebirth.network.registerMessage(4,  PacketSyncTileEntity.class, PacketSyncTileEntity::new);
        TurretModRebirth.network.registerMessage(5,  PacketInitAssemblyCrafting.class, PacketInitAssemblyCrafting::new);
        TurretModRebirth.network.registerMessage(6,  PacketAssemblyToggleAutomate.class, PacketAssemblyToggleAutomate::new);
        TurretModRebirth.network.registerMessage(7,  PacketOpenGui.class, PacketOpenGui::new);
        TurretModRebirth.network.registerMessage(8,  PacketUpdateUgradeSlot.class, PacketUpdateUgradeSlot::new);
        TurretModRebirth.network.registerMessage(9,  PacketTurretNaming.class, PacketTurretNaming::new);
        TurretModRebirth.network.registerMessage(10, PacketSyncUpgradeInst.class, PacketSyncUpgradeInst::new);
        TurretModRebirth.network.registerMessage(11, PacketSyncTcuGuis.class, PacketSyncTcuGuis::new);
        TurretModRebirth.network.registerMessage(12, PacketSyncAttackTarget.class, PacketSyncAttackTarget::new);
        TurretModRebirth.network.registerMessage(13, PacketEffect.class, PacketEffect::new);
    }
}
