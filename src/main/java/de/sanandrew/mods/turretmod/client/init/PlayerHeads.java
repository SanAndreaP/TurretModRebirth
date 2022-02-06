/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.init;

import com.mojang.authlib.GameProfile;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public final class PlayerHeads
{
    private static final UUID[] PLAYERS = new UUID[] {
            UuidUtils.EMPTY_UUID, // STANDARD
            UUID.fromString("044d980d-5c2a-4030-95cf-cbfde69ea3cb"), // SanAndreasP
            UUID.fromString("5399b615-3440-4c66-939d-ab1375952ac3"), // Drullkus
            UUID.fromString("8c826f34-113b-4238-a173-44639c53b6e6"), // Vazkii
            UUID.fromString("d183e5a2-a087-462a-963e-c3d7295f9ec5") // Darkhax
    };

    private static final GameProfile[] PLAYER_PROFILES = new GameProfile[PLAYERS.length];

    private static Tuple lastHead;

    public static void preLoadPlayerHeadsAsync() {
        new Thread(() -> {
            Minecraft mc = Minecraft.getInstance();
            for( int i = 0, max = PLAYERS.length; i < max; i++ ) {
                UUID player = PLAYERS[i];
                try {
                    GameProfile profile = mc.getMinecraftSessionService().fillProfileProperties(new GameProfile(player, null), true);
                    PLAYER_PROFILES[i] = profile;
                } catch( Exception ex ) {
                    TmrConstants.LOG.log(Level.WARN, "Error while loading player skin", ex);
                }
            }
        }, "TmrSkinPreloader").start();
    }

    public static ItemStack getRandomSkull() {
        if( lastHead == null || lastHead.<Long>getValue(0) + 5000 < System.currentTimeMillis() ) {
            GameProfile p;
            if( MiscUtils.RNG.randomInt(100) == 0 ) {
                p = PLAYER_PROFILES[1 + MiscUtils.RNG.randomInt(PLAYER_PROFILES.length - 1)];
            } else {
                p = PLAYER_PROFILES[0];
            }

            if( p == null ) {
                p = new GameProfile(null, "null");
            }

            lastHead = new Tuple(System.currentTimeMillis(), p);
        }

        ItemStack   stack = new ItemStack(Items.PLAYER_HEAD, 1);
        CompoundNBT nbt   = stack.getOrCreateTagElement("SkullOwner");
        NBTUtil.writeGameProfile(nbt, lastHead.getValue(1));
        return stack;
    }
}
