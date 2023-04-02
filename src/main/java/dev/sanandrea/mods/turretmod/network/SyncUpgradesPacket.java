/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.turret.IUpgradeProcessor;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeData;
import dev.sanandrea.mods.turretmod.entity.turret.UpgradeProcessor;
import dev.sanandrea.mods.turretmod.init.TurretModRebirth;
import dev.sanandrea.mods.turretmod.item.upgrades.UpgradeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class SyncUpgradesPacket
        extends SimpleMessage
{
    private final int turretId;
    private final Upgrade[] upgrades;

    public SyncUpgradesPacket(ITurretEntity turret, int... upgSlots) {
        this.turretId = turret.get().getId();

        IUpgradeProcessor up = turret.getUpgradeProcessor();
        this.upgrades = Arrays.stream(upgSlots).mapToObj(i -> {
            ItemStack iStack = up.getItem(i);
            IUpgrade  u      = UpgradeRegistry.INSTANCE.get(iStack);
            if( u.isValid() ) {
                IUpgradeData<?> data    = up.getUpgradeData(u.getId());
                CompoundNBT     dataNbt = null;
                if( data != null && data.getClass().isAnnotationPresent(IUpgradeData.Syncable.class) ) {
                    dataNbt = new CompoundNBT();
                    data.save(turret, dataNbt);
                }
                return new Upgrade((short) i, iStack, dataNbt);
            } else {
                return new Upgrade((short) i, ItemStack.EMPTY, null);
            }
        }).toArray(Upgrade[]::new);
    }

    public SyncUpgradesPacket(PacketBuffer buffer) {
        this.turretId = buffer.readInt();
        this.upgrades = IntStream.range(0, buffer.readInt()).mapToObj(i -> new Upgrade(buffer)).toArray(Upgrade[]::new);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.turretId);
        buffer.writeInt(this.upgrades.length);
        for( Upgrade u : this.upgrades ) {
            u.write(buffer);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = TurretModRebirth.PROXY.getNetworkPlayer(supplier);
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretId);
            if( e instanceof ITurretEntity ) {
                ITurretEntity    t       = (ITurretEntity) e;
                UpgradeProcessor upgProc = (UpgradeProcessor) t.getUpgradeProcessor();
                for( Upgrade u : this.upgrades ) {
                    upgProc.setItem(u.slot, u.iStack);
                    if( u.dataNbt != null ) {
                        IUpgradeData<?> ud = upgProc.getUpgradeData(u.slot);
                        if( ud != null ) {
                            ud.load(t, u.dataNbt);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }

    private static final class Upgrade
    {
        short slot;
        ItemStack iStack;
        CompoundNBT dataNbt;

        Upgrade(short slot, ItemStack iStack, CompoundNBT dataNbt) {
            this.slot = slot;
            this.iStack = iStack;
            this.dataNbt = dataNbt;
        }

        Upgrade(PacketBuffer buffer) {
            this(buffer.readShort(),
                 buffer.readItem(),
                 buffer.readBoolean() ? buffer.readNbt() : null);
        }

        void write(PacketBuffer buffer) {
            buffer.writeShort(this.slot);
            buffer.writeItem(this.iStack);
            buffer.writeBoolean(this.dataNbt != null);
            if( this.dataNbt != null ) {
                buffer.writeNbt(this.dataNbt);
            }
        }
    }
}
