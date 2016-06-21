/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class ItemTurret
        extends Item
{
    private static final IItemPropertyGetter TURRET_TEX_ID = (stack, worldIn, entityIn) -> TurretRegistry.INSTANCE.getRegisteredInfos().indexOf(ItemTurret.getTurretInfo(stack));

    public ItemTurret() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName("turret_placer");
        this.addPropertyOverride(new ResourceLocation("turretId"), TURRET_TEX_ID);
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
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if( !world.isRemote ) {
            Block block = world.getBlockState(pos).getBlock();
            BlockPos offPos = pos.add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            double shiftY = 0.0D;
            if( facing == EnumFacing.UP ) {
                shiftY = 1.0F;
            }
//            if( facing == EnumFacing.UP && block ) {
//                shiftY = 0.5D;
//            }

            if( EntityTurret.canTurretBePlaced(world, pos, false, facing == EnumFacing.DOWN) ) {
                EntityTurret turret = spawnTurret(world, getTurretInfo(stack), pos.getX() + 0.5D, pos.getY() + shiftY, pos.getZ() + 0.5D, facing == EnumFacing.DOWN, player);
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

        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if( !world.isRemote ) {
            RayTraceResult traceResult = this.rayTrace(world, player, true);
                if( traceResult.typeOfHit == RayTraceResult.Type.BLOCK ) {
                    BlockPos blockPos = traceResult.getBlockPos();
                    if( !world.isBlockModifiable(player, blockPos) ) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    }

                    if( !player.canPlayerEdit(blockPos, traceResult.sideHit, stack) ) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    }

                    if( world.getBlockState(blockPos).getBlock() instanceof BlockLiquid ) {
                        EntityTurret turret = spawnTurret(world, getTurretInfo(stack), blockPos, false, player);
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
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
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
        assert stack.getTagCompound() != null;
        stack.getTagCompound().setFloat("turretHealth", turret.getHealth());
        if( turret.hasCustomName() ) {
            stack.getTagCompound().setString("turretName", turret.getCustomNameTag());
        }

        return stack;
    }

    public static EntityTurret spawnTurret(World world, TurretInfo info, BlockPos pos, boolean isUpsideDown, EntityPlayer owner) {
        return spawnTurret(world, info, pos.getX(), pos.getY(), pos.getZ(), isUpsideDown, owner);
    }

    public static EntityTurret spawnTurret(World world, TurretInfo info, double x, double y, double z, boolean isUpsideDown, EntityPlayer owner) {
        EntityTurret turret = createEntity(info, world, isUpsideDown, owner);
        if (turret != null) {
            turret.setLocationAndAngles(x, y - (isUpsideDown ? 1.0D : 0.0D), z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
            turret.rotationYawHead = turret.rotationYaw;
            turret.renderYawOffset = turret.rotationYaw;
//            turret.onSpawnWithEgg(null);
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
