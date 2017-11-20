/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public final class PlayerHeads
{
    public static final UUID[] PLAYERS = new UUID[] {
            UuidUtils.EMPTY_UUID, // STANDARD
            UUID.fromString("044d980d-5c2a-4030-95cf-cbfde69ea3cb"), // SanAndreasP
            UUID.fromString("5399b615-3440-4c66-939d-ab1375952ac3"), // Drullkus
            UUID.fromString("8c826f34-113b-4238-a173-44639c53b6e6"), // Vazkii
            UUID.fromString("d183e5a2-a087-462a-963e-c3d7295f9ec5"), // Darkhax
    };

    public static void preLoadPlayerHeadsAsync() {
        Thread t = new Thread(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            for( UUID player : PLAYERS ) {
                try {
                    mc.getSkinManager().loadSkinFromCache(new GameProfile(player, null));
//                    mc.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                } catch( Exception ex ) {
                    TmrConstants.LOG.log(Level.WARN, "Error while loading player skin", ex);
                }
            }
        }, "TmrSkinPreloader");
        t.run();
    }
}
