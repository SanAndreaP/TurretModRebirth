/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.TurretRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ItemTurret
        extends Item
{
    @SideOnly(Side.CLIENT)
    private Map<String, IIcon> turretIcons;

    private static final String NBT_TURRET = "turretName";

    public static Turret spawnTurret(World world, String name, double x, double y, double z) {
        Turret turret = createEntity(name, world);
        if (turret != null) {
            EntityLiving turretEntity = turret.getEntity();
            turretEntity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            turretEntity.rotationYawHead = turretEntity.rotationYaw;
            turretEntity.renderYawOffset = turretEntity.rotationYaw;
            turretEntity.onSpawnWithEgg(null);
            world.spawnEntityInWorld(turretEntity);
            turretEntity.playLivingSound();
        }

        return turret;
    }

    private static Turret createEntity(String name, World world) {
        Turret entity = null;
        try {
            Class<? extends Turret> entityClass = TurretRegistry.getTurretInfo(name).getTurretClass();
            if( entityClass != null ) {
                entity = entityClass.getConstructor(World.class).newInstance(world);
            }
        } catch( InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ex ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Cannot instanciate turret!", ex);
        }

        if( entity == null ) {
            TurretMod.MOD_LOG.printf(Level.WARN, "Skipping turret with name %s", name);
        }

        return entity;
    }

    public ItemTurret() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretMod.MOD_ID + ":turret_placer");
    }

    public static String getTurretName(ItemStack stack) {
        if( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TURRET) ) {
            return stack.getTagCompound().getString(NBT_TURRET);
        }

        return null;
    }

    public static void setTurretName(ItemStack stack, String name) {
        NBTTagCompound nbt;
        if( stack.hasTagCompound() ) {
            nbt = stack.getTagCompound();
        } else {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        nbt.setString(NBT_TURRET, name);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Class<? extends Turret> cls = TurretRegistry.getTurretInfo(getTurretName(stack)).getTurretClass();
        String entityName = cls != null ? (String) EntityList.classToStringMapping.get(cls) : "UNKNOWN";
        return SAPUtils.translate("entity." + entityName + ".name");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float offX, float offY, float offZ) {
        if( !world.isRemote ) {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double shiftY = 0.0D;
            if( side == 1 && block.getRenderType() == 11 ) {
                shiftY = 0.5D;
            }

            Turret turret = spawnTurret(world, getTurretName(stack), x + 0.5D, y + shiftY, z + 0.5D);
            if( turret != null ) {
                if( stack.hasDisplayName() ) {
                    turret.getEntity().setCustomNameTag(stack.getDisplayName());
                }

                if( !player.capabilities.isCreativeMode ) {
                    stack.stackSize--;
                }

                turret.setOwner(player);
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if( !world.isRemote ) {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
            if( movingobjectposition == null ) {
                return stack;
            } else {
                if( movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK ) {
                    int x = movingobjectposition.blockX;
                    int y = movingobjectposition.blockY;
                    int z = movingobjectposition.blockZ;
                    if( !world.canMineBlock(player, x, y, z) ) {
                        return stack;
                    }
                    if( !player.canPlayerEdit(x, y, z, movingobjectposition.sideHit, stack) ) {
                        return stack;
                    }
                    if( world.getBlock(x, y, z) instanceof BlockLiquid ) {
                        Turret turret = spawnTurret(world, getTurretName(stack), x, y, z);
                        if( turret != null ) {
                            if( stack.hasDisplayName() ) {
                                turret.getEntity().setCustomNameTag(stack.getDisplayName());
                            }

                            if( !player.capabilities.isCreativeMode ) {
                                stack.stackSize--;
                            }

                            turret.setOwner(player);
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.turretIcons.get(getTurretName(stack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        List<String> turrets = TurretRegistry.getAllTurretNamesSorted();
        for( String name : turrets ) {
            ItemStack newStack = new ItemStack(this);
            setTurretName(newStack, name);
            items.add(newStack);
        }
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 1;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.turretIcons = Maps.newHashMap();
        List<String> turrets = TurretRegistry.getAllTurretNamesSorted();
        for( String name : turrets ) {
            this.turretIcons.put(name, iconRegister.registerIcon(TurretRegistry.getTurretInfo(name).getIcon()));
        }
    }
}
