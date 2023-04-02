/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AssemblyActionPacket
        extends SimpleMessage
{
    public static final byte SET_RECIPE = 0;
    public static final byte SET_AUTOMATE = 1;
    public static final byte SET_MANUAL = 2;
    public static final byte CANCEL_CRAFT = 3;

    private final BlockPos assemblyPos;
    private final byte actionId;

    private final ResourceLocation recipeId;
    private final int recipeAmount;

    private AssemblyActionPacket(BlockPos assemblyPos, byte actionId, ResourceLocation recipeId, int recipeAmount) {
        this.assemblyPos = assemblyPos;
        this.actionId = actionId;
        this.recipeId = recipeId;
        this.recipeAmount = recipeAmount;
    }

    public AssemblyActionPacket(PacketBuffer buffer) {
        this.assemblyPos = buffer.readBlockPos();
        this.actionId = buffer.readByte();
        if( this.actionId == SET_RECIPE ) {
            this.recipeId = buffer.readResourceLocation();
            this.recipeAmount = buffer.readVarInt();
        } else {
            this.recipeId = null;
            this.recipeAmount = 0;
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(this.assemblyPos);
        buffer.writeByte(this.actionId);
        if( this.actionId == SET_RECIPE ) {
            buffer.writeResourceLocation(this.recipeId);
            buffer.writeVarInt(this.recipeAmount);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = supplier.get().getSender();
        if( player == null ) { // if this is not sent from a player, do nothing!
            return;
        }

        TileEntity te = player.level.getBlockEntity(this.assemblyPos);
        if( !(te instanceof TurretAssemblyEntity) ) {
            return;
        }

        TurretAssemblyEntity assembly = (TurretAssemblyEntity) te;
        switch( this.actionId ) {
            case SET_RECIPE:
                assembly.beginCrafting(AssemblyManager.INSTANCE.getRecipe(player.level, this.recipeId), this.recipeAmount);
                break;
            case SET_AUTOMATE:
                assembly.setAutomated(true);
                break;
            case SET_MANUAL:
                assembly.setAutomated(false);
                break;
            case CANCEL_CRAFT:
                assembly.cancelCrafting();
                break;
            default: // no-op
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }

    public static void setRecipe(TurretAssemblyEntity assembly, ResourceLocation id, int amount) {
        TurretModRebirth.NETWORK.sendToServer(new AssemblyActionPacket(assembly.getBlockPos(), SET_RECIPE, id, amount));
    }

    public static void setAutomate(TurretAssemblyEntity assembly) {
        TurretModRebirth.NETWORK.sendToServer(new AssemblyActionPacket(assembly.getBlockPos(), SET_AUTOMATE, null, 0));
    }

    public static void setManual(TurretAssemblyEntity assembly) {
        TurretModRebirth.NETWORK.sendToServer(new AssemblyActionPacket(assembly.getBlockPos(), SET_MANUAL, null, 0));
    }

    public static void cancelCraft(TurretAssemblyEntity assembly) {
        TurretModRebirth.NETWORK.sendToServer(new AssemblyActionPacket(assembly.getBlockPos(), CANCEL_CRAFT, null, 0));
    }
}
