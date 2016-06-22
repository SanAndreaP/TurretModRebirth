/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncPlayerList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList
        extends WorldSavedData
{
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String WSD_NAME = String.format("%s_%s", TurretModRebirth.ID, "playerList");
    public static final PlayerList INSTANCE = new PlayerList();

    //concurrent hash map to prevent different dimension altering this list at the same time
    private final Map<UUID, String> playerMap = new ConcurrentHashMap<>();

    public PlayerList() {
        this(WSD_NAME);
    }

    public PlayerList(String name) {
        super(name);
    }

    public String getPlayerName(UUID playerUUID) {
        if( playerUUID.equals(EMPTY_UUID) ) {
            return "[all players]";
        }

        String s = this.playerMap.get(playerUUID);
        return s == null ? "UNKNOWN" : this.playerMap.get(playerUUID);
    }

    public UUID[] getPlayers() {
        return this.playerMap.keySet().toArray(new UUID[this.playerMap.size()]);
    }

    public Map<UUID, String> getPlayerMap() {
        return new HashMap<>(this.playerMap);
    }

    public Map<UUID, Boolean> getDefaultPlayerList() {
        Map<UUID, Boolean> players = new HashMap<>(this.playerMap.size());

        players.put(EMPTY_UUID, false);
        for( UUID playerUUID : this.playerMap.keySet() ) {
            players.put(playerUUID, false);
        }

        return players;
    }

    public void putPlayersClient(Map<UUID, String> players) {
        if( FMLCommonHandler.instance().getSide() == Side.CLIENT ) {
            this.playerMap.putAll(players);
        }
    }

    private void syncList() {
        PacketRegistry.sendToAll(new PacketSyncPlayerList(this));
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if( event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote ) {
            this.playerMap.put(event.getEntity().getUniqueID(), event.getEntity().getName());
            this.syncList();
            this.markDirty();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        MapStorage storage = event.getWorld().getMapStorage();
        if( storage != null ) {
            PlayerList result = (PlayerList) storage.getOrLoadData(PlayerList.class, WSD_NAME);
            if( result == null ) {
                result = this;
            }

            this.playerMap.putAll(result.playerMap);
            storage.setData(WSD_NAME, this);
            this.syncList();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        String tmrNbtName = WSD_NAME;
        if( nbt.hasKey(tmrNbtName) ) {
            NBTTagCompound tmrNBT = nbt.getCompoundTag(tmrNbtName);
            if( tmrNBT.hasKey("players") ) {
                NBTTagList nbtList = tmrNBT.getTagList("players", Constants.NBT.TAG_COMPOUND);
                int size = nbtList.tagCount();
                for( int i = 0; i < size; i++ ) {
                    NBTTagCompound playerNbt = nbtList.getCompoundTagAt(i);
                    this.playerMap.put(UUID.fromString(playerNbt.getString("playerUUID")), playerNbt.getString("playerName"));
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound tmrNBT = new NBTTagCompound();
        NBTTagList nbtList = new NBTTagList();
        for( Map.Entry<UUID, String> player : this.playerMap.entrySet() ) {
            NBTTagCompound playerNbt = new NBTTagCompound();
            playerNbt.setString("playerUUID", player.getKey().toString());
            playerNbt.setString("playerName", player.getValue());
            nbtList.appendTag(playerNbt);
        }
        tmrNBT.setTag("players", nbtList);
        nbt.setTag(WSD_NAME, tmrNBT);

        return nbt;
    }
}