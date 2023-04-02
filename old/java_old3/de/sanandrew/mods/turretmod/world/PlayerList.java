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
import de.sanandrew.mods.turretmod.network.PacketSyncPlayerList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList
        extends WorldSavedData
{
    private static final String WSD_NAME = String.format("%s:%s", TmrConstants.ID, "PlayerList");
    public static final PlayerList INSTANCE = new PlayerList();

    //concurrent hash map to prevent different dimension altering this list at the same time
    private final Map<UUID, ITextComponent> playerMap = new ConcurrentHashMap<>();

    private PlayerList() {
        this(WSD_NAME);
    }

    @SuppressWarnings("WeakerAccess")
    public PlayerList(String s) {
        super(s);
    }

    public ITextComponent getPlayerName(UUID playerUUID) {
        if( playerUUID.equals(UuidUtils.EMPTY_UUID) ) {
            return new StringTextComponent("[removed]");
        }

        ITextComponent s = this.playerMap.get(playerUUID);
        return s == null ? new StringTextComponent("[unknown]") : this.playerMap.get(playerUUID);
    }

    public Map<UUID, ITextComponent> getPlayerMap() {
        return new HashMap<>(this.playerMap);
    }

    public Map<UUID, Boolean> getDefaultPlayerList() {
        Map<UUID, Boolean> players = new HashMap<>(this.playerMap.size());

        for( UUID playerUUID : this.playerMap.keySet() ) {
            players.put(playerUUID, false);
        }

        return players;
    }

    @OnlyIn(Dist.CLIENT)
    public void putPlayersClient(Map<UUID, ITextComponent> players) {
        this.playerMap.putAll(players);
    }

    private void syncList() {
        TurretModRebirth.network.sendToAll(new PacketSyncPlayerList(), null);
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if( event.getEntity() instanceof PlayerEntity && !event.getWorld().isRemote ) {
            this.playerMap.put(event.getEntity().getUniqueID(), event.getEntity().getName());
            this.syncList();
            this.markDirty();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if( (event.getWorld() instanceof ServerWorld) ) {
            DimensionSavedDataManager storage = ((ServerWorld) event.getWorld()).getSavedData();
            PlayerList result = storage.getOrCreate(PlayerList::new, WSD_NAME);

            this.playerMap.putAll(result.playerMap);
            storage.set(this);
            this.syncList();
        }
    }

    @Override
    public void read(CompoundNBT nbt) {
        if( nbt.contains(WSD_NAME) ) {
            CompoundNBT tmrNBT = nbt.getCompound(WSD_NAME);
            if( tmrNBT.contains("Players") ) {
                ListNBT nbtList = tmrNBT.getList("Players", Constants.NBT.TAG_COMPOUND);
                int     size    = nbtList.size();
                for( int i = 0; i < size; i++ ) {
                    CompoundNBT playerNbt = nbtList.getCompound(i);

                    if( playerNbt.contains("PlayerIdMSB") && playerNbt.contains("PlayerIdLSB") ) {
                        UUID           id   = new UUID(playerNbt.getLong("PlayerIdMSB"), playerNbt.getLong("PlayerLSB"));
                        ITextComponent name = ITextComponent.Serializer.getComponentFromJson(playerNbt.getString("PlayerName"));

                        this.playerMap.put(id, name);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        CompoundNBT tmrNBT = new CompoundNBT();
        ListNBT nbtList = new ListNBT();
        for( Map.Entry<UUID, ITextComponent> player : this.playerMap.entrySet() ) {
            CompoundNBT playerNbt = new CompoundNBT();
            playerNbt.putLong("PlayerIdMSB", player.getKey().getMostSignificantBits());
            playerNbt.putLong("PlayerIdLSB", player.getKey().getLeastSignificantBits());
            playerNbt.putString("PlayerName", ITextComponent.Serializer.toJson(player.getValue()));
            nbtList.add(playerNbt);
        }
        tmrNBT.put("Players", nbtList);
        compound.put(WSD_NAME, tmrNBT);

        return compound;
    }

    public static final class SafePutBuilder
    {
        private final Map<UUID, ITextComponent> map;

        public SafePutBuilder(Map<UUID, ITextComponent> map) {
            this.map = map;
        }

        public DistExecutor.SafeRunnable get() {
            return () -> PlayerList.INSTANCE.putPlayersClient(this.map);
        }
    }
}
