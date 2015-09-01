/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import cpw.mods.fml.common.FMLCommonHandler;
import de.sanandrew.core.manpack.mod.client.particle.SAPEffectRenderer;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.event.RenderFxLayerHandler;
import de.sanandrew.mods.turretmod.client.gui.tcu.tooltip.GuiIngameTcuInfos;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuUpgrades;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.util.*;
import de.sanandrew.mods.turretmod.api.registry.TurretUpgradeRegistry;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy
        extends CommonProxy
{
    public static int particleFxLayer1;

    @Override
    public void init() {
        super.init();

        TmrEntities.registerRenderers();
        TmrBlocks.registerBlockAndTeRenderers();

        MinecraftForge.EVENT_BUS.register(new RenderForcefieldHandler());
        MinecraftForge.EVENT_BUS.register(new GuiIngameTcuInfos());
        FMLCommonHandler.instance().bus().register(ClientSideStateCheatPropertyManagerFactoryKeyResponseInterceptorInterpreter.INSTANCE);
        SAPUtils.EVENT_BUS.register(new RenderFxLayerHandler());

        particleFxLayer1 = SAPEffectRenderer.INSTANCE.registerFxLayer(new ResourceLocation(TurretMod.MOD_ID, "textures/particles/particles_1.png"), true);
    }

    @Override
    public void processTargetListClt(ByteBufInputStream stream) throws IOException {
        int entityId = stream.readInt();
        int listSize = stream.readInt();
        Entity e = getMinecraft().theWorld.getEntityByID(entityId);
        if( e instanceof EntityTurretBase ) {
            List<Class<?extends EntityLiving>> applicableTargets = new ArrayList<>(listSize);
            for( int i = 0; i < listSize; i++ ) {
                String clsName = stream.readUTF();
                Class<? extends EntityLiving> entityCls = SAPUtils.getCasted(EntityList.stringToClassMapping.get(clsName));
                applicableTargets.add(entityCls);
            }

            ((EntityTurretBase) e).getTargetHandler().setTargetList(applicableTargets);
        }
    }

    @Override
    public void processUpgradeListClt(ByteBufInputStream stream) throws IOException {
        int entityId = stream.readInt();
        int listSize = stream.readInt();
        Entity e = getMinecraft().theWorld.getEntityByID(entityId);

        if( e instanceof EntityTurretBase ) {
            List<TurretUpgrade> currUpgList = new ArrayList<>(listSize);
            for( int i = 0; i < listSize; i++ ) {
                String regName = stream.readUTF();
                currUpgList.add(TurretUpgradeRegistry.getUpgrade(regName));
            }
            EntityTurretBase turret = (EntityTurretBase) e;
            turret.getUpgradeHandler().applyUpgradeList(turret, currUpgList);
        }
    }

    @Override
    public void processTransmitterExpTime(ByteBufInputStream stream) throws IOException {
        TileEntity tileEntity = getMinecraft().theWorld.getTileEntity(stream.readInt(), stream.readInt(), stream.readInt());
        if( tileEntity instanceof TileEntityItemTransmitter ) {
            ((TileEntityItemTransmitter) tileEntity).requestTimeout = stream.readInt();
        }
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
        if( SAPUtils.isIndexInRange(EnumGui.VALUES, id) ) {
            switch( EnumGui.VALUES[id] ) {
                case GUI_TCU_INFO:
                    return new GuiTcuInfo((EntityTurretBase) getMinecraft().theWorld.getEntityByID(x));
                case GUI_TCU_TARGETS:
                    return new GuiTcuTargets((EntityTurretBase) getMinecraft().theWorld.getEntityByID(x));
                case GUI_TCU_UPGRADES:
                    return new GuiTcuUpgrades((EntityTurretBase) getMinecraft().theWorld.getEntityByID(x));
            }
        } else {
            TurretMod.MOD_LOG.printf(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }

    private static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
}
