/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileMinigunPebble;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.inventory.ContainerAssemblyFilter;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketOpenGui;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.javatuples.Tuple;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

public class CommonProxy
        implements IGuiHandler
{
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerList.INSTANCE);
        int entityCnt = 0;
        EntityRegistry.registerModEntity(EntityTurretCrossbow.class, "turret_i_crossbow", entityCnt++, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityTurretShotgun.class, "turret_i_shotgun", entityCnt++, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityTurretCryolator.class, "turret_i_snowball", entityCnt++, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityTurretRevolver.class, "turret_ii_revolver", entityCnt++, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityTurretMinigun.class, "turret_ii_minigun", entityCnt++, TurretModRebirth.instance, 128, 1, false);

        EntityRegistry.registerModEntity(EntityProjectileCrossbowBolt.class, "turret_proj_arrow", entityCnt++, TurretModRebirth.instance, 128, 1, true);
        EntityRegistry.registerModEntity(EntityProjectilePebble.class, "turret_proj_pebble", entityCnt++, TurretModRebirth.instance, 128, 1, true);
        EntityRegistry.registerModEntity(EntityProjectileCryoCell.class, "turret_proj_snowball", entityCnt++, TurretModRebirth.instance, 128, 1, true);
        EntityRegistry.registerModEntity(EntityProjectileBullet.class, "turret_proj_bullet", entityCnt++, TurretModRebirth.instance, 128, 1, true);
        EntityRegistry.registerModEntity(EntityProjectileMinigunPebble.class, "turret_proj_mgpebble", entityCnt++, TurretModRebirth.instance, 128, 1, true);
    }

    public void init(FMLInitializationEvent event) { }

    public void postInit(FMLPostInitializationEvent event) { }

    @Override
    public final Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( id >= 0 && id < EnumGui.VALUES.length ) {
            TileEntity te;
            switch( EnumGui.VALUES[id] ) {
                case GUI_TCU_UPGRADES: {
                    Entity e = world.getEntityByID(x);
                    if( e instanceof EntityTurret ) {
                        return new ContainerTurretUpgrades(player.inventory, ((EntityTurret) e).getUpgradeProcessor());
                    }
                    break;
                }
                case GUI_TASSEMBLY_MAN:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new ContainerTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
                case GUI_TASSEMBLY_FLT:
                    ItemStack stack = player.getHeldItemMainhand();
                    if( ItemStackUtils.isValidStack(stack) && stack.getItem() == ItemRegistry.asbFilter ) {
                        return new ContainerAssemblyFilter(player.inventory, stack, player.inventory.currentItem);
                    }
                    break;
                case GUI_POTATOGEN:
                    te = world.getTileEntity(new BlockPos(x, y, z));
                    if( te instanceof TileEntityElectrolyteGenerator ) {
                        return new ContainerElectrolyteGenerator(player.inventory, (TileEntityElectrolyteGenerator) te);
                    }

            }
        } else {
            TurretModRebirth.LOG.log(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        int guiId = id.ordinal();

        if( player instanceof EntityPlayerMP ) {
            if( getServerGuiElement(guiId, player, player.worldObj, x, y, z) == null ) {
                PacketRegistry.sendToPlayer(new PacketOpenGui((byte) guiId, x, y, z), (EntityPlayerMP) player);
            } else {
                FMLNetworkHandler.openGui(player, TurretModRebirth.instance, guiId, player.worldObj, x, y, z);
            }
        } else {
            if( getServerGuiElement(guiId, player, player.worldObj, x, y, z) == null ) {
                FMLNetworkHandler.openGui(player, TurretModRebirth.instance, guiId, player.worldObj, x, y, z);
            } else {
                PacketRegistry.sendToServer(new PacketOpenGui((byte) guiId, x, y, z));
            }
        }
    }

    public void spawnParticle(EnumParticle particle, double x, double y, double z) {
        this.spawnParticle(particle, x, y, z, null);
    }

    public void spawnParticle(EnumParticle particle, double x, double y, double z, Tuple data) { }
}
