/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.world;

import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.SyncPlayerListPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class PlayerList
        extends WorldSavedData
{
    private static final String WSD_NAME = String.format("%s_%s", TmrConstants.ID, "playerlist");
    @Nullable
    private static PlayerList playerList;

    //concurrent hash map to prevent different dimension altering this list at the same time
    private final Map<UUID, ITextComponent> playerMap = new ConcurrentHashMap<>();

    private PlayerList() {
        this(WSD_NAME);
    }

    @SuppressWarnings("WeakerAccess")
    public PlayerList(String s) {
        super(s);
    }

    @Override
    public void load(CompoundNBT nbt) {
        if( nbt.contains("Players") ) {
            ListNBT nbtList = nbt.getList("Players", Constants.NBT.TAG_COMPOUND);
            int     size    = nbtList.size();
            for( int i = 0; i < size; i++ ) {
                CompoundNBT playerNbt = nbtList.getCompound(i);

                if( playerNbt.contains("PlayerId") ) {
                    UUID           id   = playerNbt.getUUID("PlayerId");
                    ITextComponent name = ITextComponent.Serializer.fromJson(playerNbt.getString("PlayerName"));

                    this.playerMap.put(id, name);
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        ListNBT nbtList = new ListNBT();
        for( Map.Entry<UUID, ITextComponent> player : this.playerMap.entrySet() ) {
            CompoundNBT playerNbt = new CompoundNBT();
            playerNbt.putUUID("PlayerId", player.getKey());
            playerNbt.putString("PlayerName", ITextComponent.Serializer.toJson(player.getValue()));
            nbtList.add(playerNbt);
        }
        compound.put("Players", nbtList);

        return compound;
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        if( playerList != null && event.getEntity() instanceof PlayerEntity && !event.getWorld().isClientSide ) {
            playerList.playerMap.put(event.getEntity().getUUID(), event.getEntity().getName());
            playerList.setDirty();

            syncList();
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        IWorld level = event.getWorld();
        if( level instanceof ServerWorld ) {
            ServerWorld sLevel = (ServerWorld) level;
            if( sLevel.dimension() == World.OVERWORLD ) {
                DimensionSavedDataManager storage = sLevel.getDataStorage();
                playerList = storage.computeIfAbsent(PlayerList::new, WSD_NAME);

                syncList();
            }
        }
    }

    public static ITextComponent getPlayerName(UUID playerUUID) {
        if( playerList == null ) {
            return StringTextComponent.EMPTY;
        }

        if( playerUUID.equals(UuidUtils.EMPTY_UUID) ) {
            return new StringTextComponent("[removed]");
        }

        ITextComponent s = playerList.playerMap.get(playerUUID);
        return s == null ? new StringTextComponent("[unknown]") : playerList.playerMap.get(playerUUID);
    }

    public static Map<UUID, ITextComponent> getPlayerMap() {
        return playerList != null ? new HashMap<>(playerList.playerMap) : Collections.emptyMap();
    }

    public static Map<UUID, Boolean> getDefaultPlayerList() {
        Map<UUID, Boolean> players = new HashMap<>();

        if( playerList != null ) {
            for( UUID playerUUID : playerList.playerMap.keySet() ) {
                players.put(playerUUID, false);
            }
        }

        return players;
    }

    @OnlyIn(Dist.CLIENT)
    public static void putPlayersClient(Map<UUID, ITextComponent> players) {
        if( playerList != null ) {
            playerList.playerMap.putAll(players);
        }
    }

    private static void syncList() {
        TurretModRebirth.NETWORK.sendToAll(new SyncPlayerListPacket(), null);
    }

    @Nullable
    public static PlayerList getData() {
        return playerList;
    }
}
