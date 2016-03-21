/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuTargets;
import de.sanandrew.mods.turretmod.client.model.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.event.RenderWorldLastHandler;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderTurretArrow;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new RenderWorldLastHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCrossbow.class, new RenderTurret(new ModelTurretCrossbow(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCrossbowBolt.class, new RenderTurretArrow());
    }

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        if( player == null ) {
            player = Minecraft.getMinecraft().thePlayer;
        }

        super.openGui(player, id, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( id >= 0 && id < EnumGui.VALUES.length ) {
            switch( EnumGui.VALUES[id] ) {
//                case GUI_TCU_INFO:
//                    return new GuiTcuInfo((EntityTurret) Minecraft.getMinecraft().theWorld.getEntityByID(x));
                case GUI_TCU_TARGETS:
                    return new GuiTcuTargets((EntityTurret) Minecraft.getMinecraft().theWorld.getEntityByID(x));
//                case GUI_TCU_UPGRADES:
//                    return new GuiTcuUpgrades((EntityTurret) Minecraft.getMinecraft().theWorld.getEntityByID(x));
            }
        } else {
            TurretModRebirth.LOG.log(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }
}
