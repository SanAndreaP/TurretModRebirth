/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.entity.turret.handler;

import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.api.UpgradeHandler;
import de.sanandrew.mods.turretmod.api.UpgradeQueueData;
import de.sanandrew.mods.turretmod.api.registry.TurretUpgradeRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.network.packet.PacketUpgradeList;
import de.sanandrew.mods.turretmod.network.packet.PacketUpgradeListRequest;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class TurretUpgradeHandler
        implements UpgradeHandler<EntityTurretBase>
{
    private List<TurretUpgrade> upgrades = new ArrayList<>(36);
    private Integer upgradeListHash = null;
    private Map<TurretUpgrade, UpgradeQueueData> upgradeUpdQueues = new HashMap<>();

    public void onTurretConstruct(EntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            this.upgradeListHash = this.upgrades.hashCode();
        }
    }

    public void onTurretUpdate(EntityTurretBase turret) {
        int currUpgradeHash = this.upgrades.hashCode();
        if( this.upgradeListHash == null || this.upgradeListHash != currUpgradeHash ) {
            if( turret.worldObj.isRemote ) {
                PacketUpgradeListRequest.sendPacket(turret);
            } else {
                PacketUpgradeList.sendPacket(turret);
            }

            this.upgradeListHash = currUpgradeHash; // prevent resending packet until it arrives
        }

        for( Entry<TurretUpgrade, UpgradeQueueData> queue : this.upgradeUpdQueues.entrySet() ) {
            queue.getKey().onUpdateQueue(turret, queue.getValue());
        }
    }

    public void writeToNbt(EntityTurretBase turret, NBTTagCompound nbt) {
        NBTTagList upgradeList = new NBTTagList();
        for( TurretUpgrade upgrade : this.upgrades ) {
            upgrade.onSave(turret, nbt);
            upgradeList.appendTag(new NBTTagString(TurretUpgradeRegistry.getRegistrationName(upgrade)));
        }
        nbt.setTag("upgradeList", upgradeList);
    }

    public void readFromNbt(EntityTurretBase turret, NBTTagCompound nbt) {
        NBTTagList upgradeList = nbt.getTagList("upgradeList", NBT.TAG_STRING);
        for( int i = 0; i < upgradeList.tagCount(); i++ ) {
            String upgradeName = upgradeList.getStringTagAt(i);
            TurretUpgrade upgrade = TurretUpgradeRegistry.getUpgrade(upgradeName);
            if( upgrade != null ) {
                this.upgrades.add(upgrade);
                upgrade.onLoad(turret, nbt);
            } else {
                TurretMod.MOD_LOG.printf(Level.WARN, "Skipped loading upgrade %s, because it wasn't registered!", upgradeName);
            }
        }
    }

    @Override
    public void registerUpgradeToUpdateQueue(TurretUpgrade upgrade, UpgradeQueueData queueData) {
        this.upgradeUpdQueues.put(upgrade, queueData);
    }

    @Override
    public UpgradeQueueData getUpgradeQueueData(TurretUpgrade upgrade) {
        return this.upgradeUpdQueues.get(upgrade);
    }

    @Override
    public boolean hasUpgrade(TurretUpgrade upg) {
        return this.upgrades.contains(upg);
    }

    @Override
    public void removeUpgrade(EntityTurretBase turret, TurretUpgrade upg) {
        if( this.hasUpgrade(upg) ) {
            upg.onRemove(turret);
            this.upgrades.remove(upg);
            if( this.upgradeUpdQueues.containsKey(upg) ) {
                this.upgradeUpdQueues.remove(upg);
            }
        }
    }

    @Override
    public final List<TurretUpgrade> getUpgradeList() {
        return new ArrayList<>(this.upgrades);
    }

    @Override
    public boolean applyUpgrade(EntityTurretBase turret, TurretUpgrade upg) {
        boolean hasDependency = upg.getDependantOn() == null || this.hasUpgrade(upg.getDependantOn());
        if( this.upgrades.size() < this.getMaxUpgradeSlots(turret) && !this.hasUpgrade(upg) && hasDependency && TurretUpgradeRegistry.isApplicableToCls(upg, turret.getClass()) ) {
            this.upgrades.add(upg);
            upg.onApply(turret);
            return true;
        }

        return false;
    }

    public void onApplyAttributes(BaseAttributeMap attributeMap) {
        attributeMap.registerAttribute(TurretAttributes.MAX_UPGRADE_SLOTS);
    }

    @Override
    public int getMaxUpgradeSlots(EntityTurretBase turret) {
        return MathHelper.ceiling_double_int(turret.getEntityAttribute(TurretAttributes.MAX_UPGRADE_SLOTS).getAttributeValue());
    }

    public void applyUpgradeList(EntityTurretBase turret, List<TurretUpgrade> currUpgList) {
        List<TurretUpgrade> oldUpgList = this.getUpgradeList();
        List<TurretUpgrade> remUpgList = this.getUpgradeList();

        remUpgList.removeAll(currUpgList);

        List<TurretUpgrade> addUpgList = new ArrayList<>(currUpgList);

        addUpgList.removeAll(oldUpgList);

        for( TurretUpgrade removingUpgrade : remUpgList ) {
            this.removeUpgrade(turret, removingUpgrade);
        }

        for( TurretUpgrade addingUpgrade : addUpgList ) {
            this.applyUpgrade(turret, addingUpgrade);
        }
    }
}
