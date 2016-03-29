/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuEntityTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuPlayerTargets;
import de.sanandrew.mods.turretmod.client.model.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.event.RenderWorldLastHandler;
import de.sanandrew.mods.turretmod.client.particle.ParticleAssemblySpark;
import de.sanandrew.mods.turretmod.client.render.item.ItemRendererTile;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderTurretArrow;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.turretAssembly), new ItemRendererTile(new TileEntityTurretAssembly(true)));
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
                case GUI_TCU_INFO:
                    return new GuiTcuInfo((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_ENTITY_TARGETS:
                    return new GuiTcuEntityTargets((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_PLAYER_TARGETS:
                    return new GuiTcuPlayerTargets((EntityTurret) world.getEntityByID(x));
//                case GUI_TCU_UPGRADES:
//                    return new GuiTcuUpgrades((EntityTurret) Minecraft.getMinecraft().theWorld.getEntityByID(x));
                case GUI_TASSEMBLY_MAN:
                    TileEntity te = world.getTileEntity(x, y, z);
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new GuiTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
            }
        } else {
            TurretModRebirth.LOG.log(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }

    @Override
    public void spawnParticle(EnumParticle particle, double x, double y, double z, Tuple data) {
        Minecraft mc = Minecraft.getMinecraft();
        switch( particle ) {
            case ASSEMBLY_SPARK:
                mc.effectRenderer.addEffect(new ParticleAssemblySpark(mc.theWorld, x, y, z, 0.0F, 0.0F, 0.0F));
        }
    }
}
