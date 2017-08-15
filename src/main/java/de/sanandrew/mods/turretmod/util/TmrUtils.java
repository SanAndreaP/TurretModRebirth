/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TmrUtils
        implements ITmrUtils
{
    public static final TmrUtils INSTANCE = new TmrUtils();

    @Override
    public ITargetProcessor getNewTargetProcInstance(EntityTurret turret) {
        return new TargetProcessor(turret);
    }

    @Override
    public IUpgradeProcessor getNewUpgradeProcInstance(EntityTurret turret) {
        return new UpgradeProcessor(turret);
    }

    @Override
    public boolean isTCUItem(@Nonnull ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit);
    }

    @Override
    public void onTurretDeath(EntityTurret turret) {
        ((TargetProcessor) turret.getTargetProcessor()).dropAmmo();
        ((UpgradeProcessor) turret.getUpgradeProcessor()).dropUpgrades();
    }

    @Override
    public void updateTurretState(EntityTurret turret) {
        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(turret), turret.dimension, turret.posX, turret.posY, turret.posZ, 64.0D);
    }

    @Override
    @Nonnull
    public ItemStack getPickedTurretResult(RayTraceResult target, EntityTurret turret) {
        return ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(turret.getClass()));
    }

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        TurretModRebirth.proxy.openGui(player, id, x, y, z);
    }

    @Override
    public boolean canPlayerEditAll() {
        return TmrConfiguration.playerCanEditAll;
    }

    @Override
    public boolean canOpEditAll() {
        return TmrConfiguration.opCanEditAll;
    }

    @Override
    public <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return EntityUtils.getPassengersOfClass(e, psgClass);
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return ItemStackUtils.isValid(stack);
    }




}
