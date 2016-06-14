/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemTurret
        extends Item
{
    @SideOnly(Side.CLIENT)
    private Map<UUID, IIcon> iconMap;

    public ItemTurret() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TurretModRebirth.ID + ":turret_placer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        TurretInfo type = getTurretInfo(stack);
        if( type != null ) {
            return iconMap.get(type.getUUID());
        }
        return super.getIcon(stack, pass);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        List<TurretInfo> types = TurretRegistry.INSTANCE.getRegisteredInfos();
        this.iconMap = new HashMap<>(types.size());
        for( TurretInfo type : types ) {
            IIcon icon = iconRegister.registerIcon(String.format("%s:turrets/%s", TurretModRebirth.ID, type.getIcon()));
            if( this.itemIcon == null ) {
                this.itemIcon = icon;
            }
            this.iconMap.put(type.getUUID(), icon);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advInfo) {
        super.addInformation(stack, player, lines, advInfo);
        TurretInfo info = getTurretInfo(stack);
        if( info != null ) {
            lines.add(Lang.translateEntityCls(info.getTurretClass()));
        }

        String name = getTurretName(stack);
        if( name != null ) {
            lines.add(String.format(Lang.translate("%s.turret_name", this.getUnlocalizedName()), name));
        }

        Float health = getTurretHealth(stack);
        if( health != null ) {
            lines.add(String.format(Lang.translate("%s.health", this.getUnlocalizedName()), health));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float offX, float offY, float offZ) {
        if( !world.isRemote ) {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double shiftY = 0.0D;
            if( side == EnumFacing.UP.ordinal() && block.getRenderType() == 11 ) {
                shiftY = 0.5D;
            }

            if( EntityTurret.canTurretBePlaced(world, x, y, z, false, side == EnumFacing.DOWN.ordinal()) ) {
                EntityTurret turret = spawnTurret(world, getTurretInfo(stack), x + 0.5D, y + shiftY, z + 0.5D, side == EnumFacing.DOWN.ordinal(), player);
                if( turret != null ) {
                    Float initHealth = getTurretHealth(stack);
                    if( initHealth != null ) {
                        turret.setHealth(initHealth);
                    }

                    String name = getTurretName(stack);
                    if( name != null ) {
                        turret.setCustomNameTag(name);
                    }

                    if( !player.capabilities.isCreativeMode ) {
                        stack.stackSize--;
                    }
                }
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
                        EntityTurret turret = spawnTurret(world, getTurretInfo(stack), x, y, z, false, player);
                        if( turret != null ) {
                            Float initHealth = getTurretHealth(stack);
                            if( initHealth != null ) {
                                turret.setHealth(initHealth);
                            }

                            String name = getTurretName(stack);
                            if( name != null ) {
                                turret.setCustomNameTag(name);
                            }

                            if( !player.capabilities.isCreativeMode ) {
                                stack.stackSize--;
                            }
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for( TurretInfo type : TurretRegistry.INSTANCE.getRegisteredInfos() ) {
            list.add(this.getTurretItem(1, type));
        }
    }

    public static TurretInfo getTurretInfo(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretInfoUUID") ) {
            return TurretRegistry.INSTANCE.getInfo(UUID.fromString(nbt.getString("turretInfoUUID")));
        }

        return null;
    }

    public static Float getTurretHealth(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretHealth") ) {
            return nbt.getFloat("turretHealth");
        }

        return null;
    }

    public static String getTurretName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretName") ) {
            return nbt.getString("turretName");
        }

        if( stack.hasDisplayName() ) {
            return stack.getDisplayName();
        }

        return null;
    }

    public ItemStack getTurretItem(int stackSize, TurretInfo type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret item with NULL type!");
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("turretInfoUUID", type.getUUID().toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    public ItemStack getTurretItem(int stackSize, TurretInfo type, EntityTurret turret) {
        ItemStack stack = this.getTurretItem(stackSize, type);
        stack.getTagCompound().setFloat("turretHealth", turret.getHealth());
        if( turret.hasCustomNameTag() ) {
            stack.getTagCompound().setString("turretName", turret.getCustomNameTag());
        }

        return stack;
    }

    public static EntityTurret spawnTurret(World world, TurretInfo info, double x, double y, double z, boolean isUpsideDown, EntityPlayer owner) {
        EntityTurret turret = createEntity(info, world, isUpsideDown, owner);
        if (turret != null) {
            turret.setLocationAndAngles(x, y - (isUpsideDown ? 1.0D : 0.0D), z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            turret.rotationYawHead = turret.rotationYaw;
            turret.renderYawOffset = turret.rotationYaw;
            turret.onSpawnWithEgg(null);
            world.spawnEntityInWorld(turret);
            turret.playLivingSound();
        }

        return turret;
    }

    private static EntityTurret createEntity(TurretInfo info, World world, boolean isUpsideDown, EntityPlayer owner) {
        EntityTurret entity = null;
        try {
            Class<? extends EntityTurret> entityClass = info.getTurretClass();
            if( entityClass != null ) {
                entity = entityClass.getConstructor(World.class, boolean.class, EntityPlayer.class).newInstance(world, isUpsideDown, owner);
            }
        } catch( InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ex ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot instanciate turret!", ex);
        }

        if( entity == null ) {
            TurretModRebirth.LOG.printf(Level.WARN, "Skipping turret with name %s", info.getName());
        }

        return entity;
    }
}
