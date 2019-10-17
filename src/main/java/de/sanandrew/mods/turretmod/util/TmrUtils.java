/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.ai.EntityAIMoveTowardsTurret;
import de.sanandrew.mods.turretmod.init.TmrConfig;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncAttackTarget;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class TmrUtils
        implements ITmrUtils
{
    public static final TmrUtils INSTANCE = new TmrUtils();

    //region SRG-Reflections
    public static int getExperiencePoints(EntityLivingBase target, EntityPlayer player) {
        return ReflectionUtils.invokeCachedMethod(EntityLivingBase.class, target, "getExperiencePoints", "func_70693_a",
                                                  new Class[] { EntityPlayer.class }, new Object[]{ player });
    }
    //endregion

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        TurretModRebirth.proxy.openGui(player, id, x, y, z);
    }

    @Override
    public boolean canPlayerEditAll() {
        return TmrConfig.Server.playerCanEditAll;
    }

    @Override
    public boolean canOpEditAll() {
        return TmrConfig.Server.opCanEditAll;
    }

    @Override
    public <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return EntityUtils.getPassengersOfClass(e, psgClass);
    }

    @Override
    public void addForcefield(Entity e, IForcefieldProvider provider) {
        TurretModRebirth.proxy.addForcefield(e, provider);
    }

    @Override
    public boolean hasForcefield(Entity e, Class<? extends IForcefieldProvider> providerCls) {
        return TurretModRebirth.proxy.hasForcefield(e, providerCls);
    }

    @Override
    public void setEntityTarget(EntityCreature target, final ITurretInst attackingTurret) {
        EntityLivingBase turretL = attackingTurret.get();
        target.setAttackTarget(turretL);
        target.setRevengeTarget(turretL);
        PacketRegistry.sendToAllAround(new PacketSyncAttackTarget(target, turretL), turretL.dimension, turretL.posX, turretL.posY, turretL.posZ, 64.0D);

        List<EntityAIMoveTowardsTurret> aiLst = EntityUtils.getAisFromTaskList(target.tasks.taskEntries, EntityAIMoveTowardsTurret.class);
        if( aiLst.size() < 1 ) {
            target.tasks.addTask(10, new EntityAIMoveTowardsTurret(target, attackingTurret, 1.1D, 64.0F));
        } else {
            aiLst.forEach(aiTgtFollow -> {
                if( !aiTgtFollow.shouldContinueExecuting() ) {
                    aiTgtFollow.setNewTurret(attackingTurret);
                }
            });
        }
    }

    public static void addJsonProperty(JsonObject jobj, String name, int[] arr) {
        JsonArray jarr = new JsonArray();
        for( int i : arr ) jarr.add(i);
        jobj.add(name, jarr);
    }

    public static void dropItem(ItemStack stack, World world, BlockPos pos) {
        if( ItemStackUtils.isValid(stack) ) {
            float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

            EntityItem entityitem = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

            float motionSpeed = 0.05F;
            entityitem.motionX = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
            entityitem.motionY = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
            entityitem.motionZ = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
            world.spawnEntity(entityitem);
        }
    }

    public static boolean mergeItemStack(Container container, @Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        boolean slotChanged = false;
        int start = beginSlot;

        if( reverse ) {
            start = endSlot - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if( stack.isStackable() ) {
            while( stack.getCount() > 0 && (!reverse && start < endSlot || reverse && start >= beginSlot) ) {
                slot = container.inventorySlots.get(start);
                slotStack = slot.getStack();

                if( ItemStackUtils.areEqual(slotStack, stack) && slot.isItemValid(stack) ) {
                    int combStackSize = slotStack.getCount() + stack.getCount();

                    if( combStackSize <= stack.getMaxStackSize() ) {
                        stack.setCount(0);
                        slotStack.setCount(combStackSize);
                        slot.onSlotChanged();
                        slotChanged = true;
                    } else if( slotStack.getCount() < stack.getMaxStackSize() ) {
                        stack.shrink(stack.getMaxStackSize() - slotStack.getCount());
                        slotStack.setCount(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        slotChanged = true;
                    }
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        if( stack.getCount() > 0 ) {
            if( reverse ) {
                start = endSlot - 1;
            } else {
                start = beginSlot;
            }

            while( !reverse && start < endSlot || reverse && start >= beginSlot ) {
                slot = container.inventorySlots.get(start);

                if( !ItemStackUtils.isValid(slot.getStack()) && slot.isItemValid(stack) ) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.setCount(0);
                    slotChanged = true;
                    break;
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        return slotChanged;
    }

    public static boolean finishTransfer(EntityPlayer player, ItemStack origStack, Slot slot, ItemStack slotStack) {
        if( slotStack.getCount() == 0 ) { // if stackSize of slot got to 0
            slot.putStack(ItemStackUtils.getEmpty());
        } else { // update changed slot stack state
            slot.onSlotChanged();
        }

        if( slotStack.getCount() == origStack.getCount() ) { // if nothing changed stackSize-wise
            return true;
        }

        slot.onTake(player, slotStack);

        return false;
    }

    public static ItemStack getHeldItemOfType(EntityPlayer player, Item type) {
        ItemStack heldStack = player.getHeldItemMainhand();
        if( !ItemStackUtils.isItem(heldStack, type) ) {
            return player.getHeldItemOffhand();
        }

        return heldStack;
    }

    public static float wrap360(float angle) {
        return angle >= 360.0F ? wrap360(angle - 360.0F) : angle < 0 ? wrap360(angle + 360.0F) : angle;
    }

    public static BlockStateContainer buildCustomBlockStateContainer(Block block,
                                                                     BiFunction<Block, ImmutableMap<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> stateImplCtor,
                                                                     IProperty<?>... properties)
    {
        return new BlockStateContainer(block, properties) {
            @Override
            protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
                return stateImplCtor.apply(block, properties);
            }
        };
    }

    public static void dropBlockItems(IInventory inv, World world, BlockPos pos) {
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            dropItem(inv.getStackInSlot(i), world, pos);
        }
    }

    //TODO: use this for modifier applications
    public static void tryApplyModifier(EntityLivingBase e, IAttribute attribute, AttributeModifier modifier) {
        IAttributeInstance attrib = e.getEntityAttribute(attribute);
        if( !attrib.hasModifier(modifier) ) {
            attrib.applyModifier(modifier);
        }
    }

    //TODO: use this for modifier applications
    public static void tryApplyModifier(EntityLivingBase e, String attributeName, AttributeModifier modifier) {
        IAttributeInstance attrib = e.getAttributeMap().getAttributeInstanceByName(attributeName);
        if( attrib != null && !attrib.hasModifier(modifier) ) {
            attrib.applyModifier(modifier);
        }
    }

    //TODO: use this for modifier removal
    public static void tryRemoveModifier(EntityLivingBase e, IAttribute attribute, AttributeModifier modifier) {
        IAttributeInstance attrib = e.getEntityAttribute(attribute);
        if( attrib.hasModifier(modifier) ) {
            attrib.removeModifier(modifier);
        }
    }

    //TODO: use this for modifier removal
    public static void tryRemoveModifier(EntityLivingBase e, String attributeName, AttributeModifier modifier) {
        IAttributeInstance attrib = e.getAttributeMap().getAttributeInstanceByName(attributeName);
        if( attrib != null && attrib.hasModifier(modifier) ) {
            attrib.removeModifier(modifier);
        }
    }

    public static NonNullList<ItemStack> getCompactItems(NonNullList<ItemStack> items, int maxInvStackSize) {
        NonNullList<ItemStack> cmpItems = NonNullList.create();

        items.sort((i1, i2) -> ItemStackUtils.areEqual(i1, i2, false, true, true) ? 0 : i1.getTranslationKey().compareTo(i2.getTranslationKey()));
        items.forEach(v -> {
            int cmpSize = cmpItems.size();
            if( cmpSize < 1 ) {
                cmpItems.add(v.copy());
            } else {
                ItemStack cs = cmpItems.get(cmpSize - 1);
                if( ItemStackUtils.areEqual(cs, v, false, true, true) ) {
                    int rest = Math.min(cs.getMaxStackSize(), maxInvStackSize) - cs.getCount();
                    if( rest >= v.getCount() ) {
                        cs.grow(v.getCount());
                    } else {
                        cs.grow(rest);
                        ItemStack restStack = v.copy();
                        restStack.shrink(rest);
                        cmpItems.add(restStack);
                    }
                } else {
                    cmpItems.add(v.copy());
                }
            }
        });

        return cmpItems;
    }

    public static BufferedReader getFile(ModContainer mod, String file) {
        File source = mod.getSource();

        try {
            if( source.isFile() ) {
                try( FileSystem fs = FileSystems.newFileSystem(source.toPath(), null) ) {
                    return Files.newBufferedReader(fs.getPath('/' + file), StandardCharsets.UTF_8);
                }
            } else if( source.isDirectory() ) {
                return Files.newBufferedReader(source.toPath().resolve(file), StandardCharsets.UTF_8);
            }
        } catch( IOException e ) {
            SanLib.LOG.log(Level.ERROR, "Error loading file: ", e);
            return null;
        }

        return null;
    }
}
