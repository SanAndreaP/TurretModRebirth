/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;

import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public final class PlayerHeads
{
    private static final UUID[] PLAYERS = new UUID[] {
            UuidUtils.EMPTY_UUID, // STANDARD
            UUID.fromString("044d980d-5c2a-4030-95cf-cbfde69ea3cb"), // SanAndreasP
            UUID.fromString("5399b615-3440-4c66-939d-ab1375952ac3"), // Drullkus
            UUID.fromString("8c826f34-113b-4238-a173-44639c53b6e6"), // Vazkii
            UUID.fromString("d183e5a2-a087-462a-963e-c3d7295f9ec5"), // Darkhax
    };

    private static final String[] PLAYER_NAMES = new String[PLAYERS.length];

    private static Tuple lastHead;

    public static void preLoadPlayerHeadsAsync() {
        Thread t = new Thread(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            for( int i = 0, max = PLAYERS.length; i < max; i++ ) {
                UUID player = PLAYERS[i];
                try {
                    GameProfile profile = mc.getSessionService().fillProfileProperties(new GameProfile(player, null), true);
                    PLAYER_NAMES[i] = profile.getName();
                } catch( Exception ex ) {
                    TmrConstants.LOG.log(Level.WARN, "Error while loading player skin", ex);
                }
            }
        }, "TmrSkinPreloader");
        t.start();
    }

    public static ItemStack getRandomSkull() {
        if( lastHead == null || lastHead.<Long>getValue(0) + 5000 < System.currentTimeMillis() ) {
            lastHead = new Tuple(System.currentTimeMillis(), MiscUtils.defIfNull(PLAYER_NAMES[MiscUtils.RNG.randomInt(PLAYER_NAMES.length)], ""));
        }

        ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("SkullOwner", lastHead.getValue(1));
        stack.setTagCompound(nbt);
        return stack;
    }
}
