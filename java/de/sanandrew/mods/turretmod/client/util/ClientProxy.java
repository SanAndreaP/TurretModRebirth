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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.event.RenderWorldLastHandler;
import de.sanandrew.mods.turretmod.client.gui.GuiPotatoGenerator;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiAssemblyFilter;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuEntityTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuPlayerTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuUpgrades;
import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategory;
import de.sanandrew.mods.turretmod.client.model.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.model.ModelTurretShotgun;
import de.sanandrew.mods.turretmod.client.model.ModelTurretSnowball;
import de.sanandrew.mods.turretmod.client.particle.ParticleAssemblySpark;
import de.sanandrew.mods.turretmod.client.particle.ParticleCryoTrail;
import de.sanandrew.mods.turretmod.client.render.item.ItemRendererTile;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderPebble;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderNothingness;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderTurretArrow;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.javatuples.Tuple;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretShotgun.class, new RenderTurret(new ModelTurretShotgun(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCryolator.class, new RenderTurret(new ModelTurretSnowball(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCrossbowBolt.class, new RenderTurretArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectilePebble.class, new RenderPebble());
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCryoCell.class, new RenderNothingness());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectrolyteGenerator.class, new RenderElectrolyteGenerator());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.assemblyTable), new ItemRendererTile(new TileEntityTurretAssembly(true)));
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.potatoGenerator), new ItemRendererTile(new TileEntityElectrolyteGenerator(true), 0.8F));

        MinecraftForge.EVENT_BUS.register(RenderForcefieldHandler.INSTANCE);

        FMLCommonHandler.instance().bus().register(new ClientTickHandler());

        ShaderHelper.initShaders();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        TurretInfoCategory.initialize();
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
            TileEntity te;
            switch( EnumGui.VALUES[id] ) {
                case GUI_TCU_INFO:
                    return new GuiTcuInfo((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_ENTITY_TARGETS:
                    return new GuiTcuEntityTargets((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_PLAYER_TARGETS:
                    return new GuiTcuPlayerTargets((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_UPGRADES:
                    return new GuiTcuUpgrades(player.inventory, (EntityTurret) world.getEntityByID(x));
                case GUI_TASSEMBLY_MAN:
                    te = world.getTileEntity(x, y, z);
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new GuiTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
                case GUI_TASSEMBLY_FLT:
                    ItemStack stack = player.getCurrentEquippedItem();
                    if( ItemStackUtils.isValidStack(stack) && stack.getItem() == ItemRegistry.asbFilter ) {
                        return new GuiAssemblyFilter(player.inventory, stack);
                    }
                    break;
                case GUI_POTATOGEN:
                    te = world.getTileEntity(x, y, z);
                    if( te instanceof TileEntityElectrolyteGenerator ) {
                        return new GuiPotatoGenerator(player.inventory, (TileEntityElectrolyteGenerator) te);
                    }
                case GUI_TINFO:
                    return new GuiTurretInfo(x, y);
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
                mc.effectRenderer.addEffect(new ParticleAssemblySpark(mc.theWorld, x, y, z, 0.0D, 0.0D, 0.0D));
                break;
            case SHOTGUN_SHOT: {
                float rotXZ = -(float) data.getValue(0) / 180.0F * (float) Math.PI;
                float rotY = -(float) data.getValue(1) / 180.0F * (float) Math.PI - 0.1F;
                double yShift = Math.sin(rotY) * 0.6F;
                double xShift = Math.sin(rotXZ) * 0.6F * Math.cos(rotY);
                double zShift = Math.cos(rotXZ) * 0.6F * Math.cos(rotY);
                boolean isUpsideDown = (boolean) data.getValue(2);

                xShift *= isUpsideDown ? -1.0F : 1.0F;
                yShift *= isUpsideDown ? -1.0F : 1.0F;
                zShift *= isUpsideDown ? -1.0F : 1.0F;
                y -= isUpsideDown ? 1.0F : 0.0F;

                for( int i = 0; i < 8; i++ ) {
                    double xDist = TmrUtils.RNG.nextDouble() * 0.05 - 0.025;
                    double yDist = TmrUtils.RNG.nextDouble() * 0.05 - 0.025;
                    double zDist = TmrUtils.RNG.nextDouble() * 0.05 - 0.025;
                    EntityFX fx = new EntitySmokeFX(mc.theWorld, x + xShift, y + yShift, z + zShift, xShift * 0.1F + xDist, yShift * 0.1F + yDist, zShift * 0.1F + zDist);
                    mc.effectRenderer.addEffect(fx);
                }
                break;
            }
            case CRYO_PARTICLE: {
                int max = 10;
                for( int i = 0; i < max; i++ ) {
                    double diffMotionX = (double) data.getValue(0) / max;
                    double diffMotionY = (double) data.getValue(1) / max;
                    double diffMotionZ = (double) data.getValue(2) / max;

                    double partMotionX = diffMotionX + TmrUtils.RNG.nextDouble() * 0.05D - 0.025D;
                    double partMotionY = -TmrUtils.RNG.nextDouble() * 0.025D;
                    double partMotionZ = diffMotionZ + TmrUtils.RNG.nextDouble() * 0.05D - 0.025D;
                    mc.effectRenderer.addEffect(new ParticleCryoTrail(mc.theWorld, x - diffMotionX * i, y - diffMotionY * i, z - diffMotionZ * i, partMotionX, partMotionY, partMotionZ));
                }
                break;
            }
        }
    }
}
