/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.world;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.SyncPlayerListPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;

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
    private static final ResourceLocation NULL_TEXTURE = new ResourceLocation("null");

    private static final String WSD_NAME = String.format("%s_%s", TmrConstants.ID, "playerlist");
    @Nullable
    private static PlayerList playerList;

    //concurrent hash map to prevent different dimension altering this list at the same time
    private final Map<UUID, PlayerData> playerMap = new ConcurrentHashMap<>();

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

                    this.playerMap.put(id, new PlayerData(name));
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        ListNBT nbtList = new ListNBT();
        for( Map.Entry<UUID, PlayerData> player : this.playerMap.entrySet() ) {
            CompoundNBT playerNbt = new CompoundNBT();
            playerNbt.putUUID("PlayerId", player.getKey());
            playerNbt.putString("PlayerName", ITextComponent.Serializer.toJson(player.getValue().name));
            nbtList.add(playerNbt);
        }
        compound.put("Players", nbtList);

        return compound;
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        if( playerList != null && event.getEntity() instanceof PlayerEntity && !event.getWorld().isClientSide ) {
            playerList.playerMap.put(event.getEntity().getUUID(), new PlayerData(event.getEntity().getName()));
            playerList.setDirty();

            syncPlayers();
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

                syncPlayers();
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

        ITextComponent s = playerList.playerMap.get(playerUUID).name;
        return s == null ? new StringTextComponent("[unknown]") : s;
    }

    public static boolean playerNameExists(String name) {
        return playerList != null && !Strings.isNullOrEmpty(name) && playerList.playerMap.values().stream().anyMatch(p -> name.equals(p.name.getString()));
    }

    @Nonnull
    public static UUID getPlayerUUID(String name) {
        if( playerList != null && !Strings.isNullOrEmpty(name) ) {
            return playerList.playerMap.entrySet().stream()
                                                  .filter(e -> name.equals(e.getValue().name.getString()))
                                                  .map(Map.Entry::getKey)
                                                  .findFirst()
                                                  .orElse(UuidUtils.EMPTY_UUID);
        }

        return UuidUtils.EMPTY_UUID;
    }

    public static String getPlayerNameSuggestion(String partName) {
        if( playerList != null && !Strings.isNullOrEmpty(partName) ) {
            return playerList.playerMap.values().stream()
                                       .map(e -> e.name.getString())
                                       .filter(n -> n.startsWith(partName))
                                       .findFirst()
                                       .orElse("");
        }

        return "";
    }

    public static Map<UUID, PlayerData> getPlayerMap() {
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

    public static void syncPlayersClient(Map<UUID, PlayerData> players) {
        if( playerList != null ) {
            playerList.playerMap.putAll(players);

            new Thread(() -> {
                Minecraft mc = Minecraft.getInstance();
                for( UUID playerId : playerList.playerMap.keySet() ) {
                    loadPlayerTexture(mc, playerId, playerList.playerMap.get(playerId));
                }
            }).start();
        }
    }

    private static void loadPlayerTexture(Minecraft mc, UUID playerId, PlayerData playerData) {
        if( playerData.texture == null ) {
            playerData.texture = DefaultPlayerSkin.getDefaultSkin(playerId);
            playerData.skinModel = "default";
            try {
                GameProfile profile = mc.getMinecraftSessionService().fillProfileProperties(new GameProfile(playerId, null), true);
                mc.getSkinManager().registerSkins(profile, (skinType, texturePath, profileTexture) -> {
                    if( skinType == MinecraftProfileTexture.Type.SKIN ) {
                        playerData.texture = texturePath;
                        playerData.skinModel = MiscUtils.get(profileTexture.getMetadata("model"), "default");
                    }
                }, true);
            } catch( Exception ex ) {
                // ignored
            }
        }
    }

    public static ResourceLocation getSkinLocation(UUID playerId) {
        return MiscUtils.get(playerList != null ? playerList.playerMap.get(playerId).texture : null, () -> DefaultPlayerSkin.getDefaultSkin(playerId));
    }

    public static String getSkinType(UUID playerId) {
        return MiscUtils.get(playerList != null ? playerList.playerMap.get(playerId).skinModel : null, () -> DefaultPlayerSkin.getSkinModelName(playerId));
    }

    private static void syncPlayers() {
        TurretModRebirth.NETWORK.sendToAll(new SyncPlayerListPacket());
    }

    @Nullable
    public static PlayerList getData() {
        return playerList;
    }

    public static final class PlayerData
    {
        public final ITextComponent name;
        ResourceLocation texture;
        String skinModel;

        public PlayerData(ITextComponent name) {
            this.name = name;
        }
    }
}
