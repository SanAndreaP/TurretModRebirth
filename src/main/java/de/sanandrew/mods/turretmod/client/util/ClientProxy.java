/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.audio.SoundLaser;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.event.RenderEventHandler;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.gui.GuiCameras;
import de.sanandrew.mods.turretmod.client.gui.GuiPotatoGenerator;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiAssemblyFilter;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuEntityTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuPlayerTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuUpgrades;
import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategory;
import de.sanandrew.mods.turretmod.client.model.ModelTurretBase;
import de.sanandrew.mods.turretmod.client.model.ModelTurretFlamethrower;
import de.sanandrew.mods.turretmod.client.model.ModelTurretLaser;
import de.sanandrew.mods.turretmod.client.model.ModelTurretMinigun;
import de.sanandrew.mods.turretmod.client.model.ModelTurretRevolver;
import de.sanandrew.mods.turretmod.client.model.ModelTurretShotgun;
import de.sanandrew.mods.turretmod.client.particle.ParticleAssemblySpark;
import de.sanandrew.mods.turretmod.client.particle.ParticleCryoTrail;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderBullet;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderFlame;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderNothingness;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderPebble;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderTurretArrow;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.client.world.ClientWorldEventListener;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileLaser;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileMinigunPebble;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

@SideOnly(Side.CLIENT)
@SuppressWarnings("MethodCallSideOnly")
public class ClientProxy
        extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientWorldEventListener());

        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCrossbow.class, manager -> new RenderTurret(manager, new ModelTurretBase(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretShotgun.class, manager -> new RenderTurret(manager, new ModelTurretShotgun(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCryolator.class, manager -> new RenderTurret(manager, new ModelTurretBase(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretRevolver.class, manager -> new RenderTurret(manager, new ModelTurretRevolver(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretMinigun.class, manager -> new RenderTurret(manager, new ModelTurretMinigun(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretLaser.class, manager -> new RenderTurret(manager, new ModelTurretLaser(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretFlamethrower.class, manager -> new RenderTurret(manager, new ModelTurretFlamethrower(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCrossbowBolt.class, RenderTurretArrow::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileCryoCell.class, RenderNothingness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectilePebble.class, RenderPebble::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileBullet.class, RenderBullet::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileMinigunPebble.class, RenderPebble::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileLaser.class, RenderNothingness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileFlame.class, RenderFlame::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MinecraftForge.EVENT_BUS.register(RenderForcefieldHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

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
            player = Minecraft.getMinecraft().player;
        }

        super.openGui(player, id, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( id >= 0 && id < EnumGui.VALUES.length ) {
            TileEntity te;
            switch( EnumGui.VALUES[id] ) {
                case GUI_TCU_INFO:
                    return new GuiTcuInfo((EntityTurret) world.getEntityByID(x), y == 1);
                case GUI_TCU_ENTITY_TARGETS:
                    return new GuiTcuEntityTargets((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_PLAYER_TARGETS:
                    return new GuiTcuPlayerTargets((EntityTurret) world.getEntityByID(x));
                case GUI_TCU_UPGRADES:
                    return new GuiTcuUpgrades(player.inventory, (EntityTurret) world.getEntityByID(x));
                case GUI_TASSEMBLY_MAN:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new GuiTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
                case GUI_TASSEMBLY_FLT:
                    ItemStack stack = player.getHeldItemMainhand();
                    if( ItemStackUtils.isValid(stack) && stack.getItem() == ItemRegistry.assembly_upg_filter ) {
                        return new GuiAssemblyFilter(player.inventory, stack);
                    }
                    break;
                case GUI_POTATOGEN:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityElectrolyteGenerator ) {
                        return new GuiPotatoGenerator(player.inventory, (TileEntityElectrolyteGenerator) te);
                    }
                case GUI_TINFO:
                    return new GuiTurretInfo(x, y);
                case GUI_DEBUG_CAMERA:
                    return new GuiCameras(world.getEntityByID(x));
            }
        } else {
            TmrConstants.LOG.log(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }

    @Override
    public void spawnParticle(EnumParticle particle, double x, double y, double z, Tuple data) {
        Minecraft mc = Minecraft.getMinecraft();
        switch( particle ) {
            case ASSEMBLY_SPARK:
                mc.effectRenderer.addEffect(new ParticleAssemblySpark(mc.world, x, y, z, 0.0D, 0.0D, 0.0D));
                break;
            case SHOTGUN_SHOT: {
                float rotXZ = -data.<Float>getValue(0) / 180.0F * (float) Math.PI;
                float rotY = -data.<Float>getValue(1) / 180.0F * (float) Math.PI - 0.1F;
                boolean isUpsideDown = data.getValue(2);

                rotXZ *= isUpsideDown ? -1.0F : 1.0F;
                rotY *= isUpsideDown ? -1.0F : 1.0F;

                double yShift = Math.sin(rotY) * 0.6F;
                double xShift = Math.sin(rotXZ) * 0.6F * Math.cos(rotY);
                double zShift = Math.cos(rotXZ) * 0.6F * Math.cos(rotY);

                xShift *= isUpsideDown ? -1.0F : 1.0F;
                zShift *= isUpsideDown ? -1.0F : 1.0F;
                y -= isUpsideDown ? 1.0F : 0.0F;

                for( int i = 0; i < 8; i++ ) {
                    double xDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    double yDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    double zDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    Particle fx = new ParticleSmokeNormal.Factory().createParticle(0, mc.world, x + xShift, y + yShift, z + zShift, xShift * 0.1F + xDist, yShift * 0.1F + yDist, zShift * 0.1F + zDist);
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

                    double partMotionX = diffMotionX + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;
                    double partMotionY = -MiscUtils.RNG.randomDouble() * 0.025D;
                    double partMotionZ = diffMotionZ + MiscUtils.RNG.randomDouble() * 0.05D - 0.025D;
                    mc.effectRenderer.addEffect(new ParticleCryoTrail(mc.world, x - diffMotionX * i, y - diffMotionY * i, z - diffMotionZ * i, partMotionX, partMotionY, partMotionZ));
                }
                break;
            }
            case MINIGUN_SHOT: {
                boolean isLeft = data.getValue(3);
                float shift = (isLeft ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
                float rotXZ = -(float) data.getValue(0) / 180.0F * (float) Math.PI;
                float rotY = -(float) data.getValue(1) / 180.0F * (float) Math.PI - 0.1F;
                boolean isUpsideDown = data.getValue(2);

                rotXZ *= isUpsideDown ? -1.0F : 1.0F;
                rotY *= isUpsideDown ? -1.0F : 1.0F;

                double motionX = Math.sin(rotXZ) * 0.06F * Math.cos(rotY) * (isUpsideDown ? -1.0F : 1.0F);
                double motionY = Math.sin(rotY) * 0.06F;
                double motionZ = Math.cos(rotXZ) * 0.06F * Math.cos(rotY) * (isUpsideDown ? -1.0F : 1.0F);

                x += (Math.sin(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);
                y += (Math.sin(rotY) * 0.6F) - (isUpsideDown ? 1.0F : 0.0F);
                z += (Math.cos(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);

                for( int i = 0; i < 8; i++ ) {
                    double xDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    double yDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    double zDist = MiscUtils.RNG.randomDouble() * 0.05 - 0.025;
                    Particle fx = new ParticleSmokeNormal.Factory().createParticle(0, mc.world, x, y, z, motionX + xDist, motionY + yDist, motionZ + zDist);
                    mc.effectRenderer.addEffect(fx);
                }
                break;
            }
        }
    }

    @Override
    public void playTurretLaser(EntityTurretLaser turretLaser) {
        if( turretLaser.laserSound == null ) {
            if( turretLaser.getTargetProcessor().isShooting() && turretLaser.getTargetProcessor().hasAmmo() ) {
                turretLaser.laserSound = new SoundLaser(turretLaser);
                Minecraft.getMinecraft().getSoundHandler().playSound(turretLaser.laserSound);
            }
        } else if( turretLaser.laserSound.isDonePlaying() || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(turretLaser.laserSound) ) {
            turretLaser.laserSound = null;
        }
    }
}
