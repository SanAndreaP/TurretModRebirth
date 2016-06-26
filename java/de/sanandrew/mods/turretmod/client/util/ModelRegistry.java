/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.medpack.TurretRepairKit;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ModelRegistry
{
    public static void registerItemModels() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        registerStandardModel(renderItem, ItemRegistry.tcu);
        registerStandardModel(renderItem, ItemRegistry.turretInfo);
        registerStandardModel(renderItem, ItemRegistry.asbFilter);
        registerStandardModel(renderItem, ItemRegistry.asbAuto);
        registerStandardModel(renderItem, ItemRegistry.asbSpeed);
        registerStandardModel(renderItem, BlockRegistry.potatoGenerator);
        registerStandardModel(renderItem, BlockRegistry.assemblyTable);

        {
            MeshDefTurret meshDef = new MeshDefTurret();
            for( TurretInfo info : TurretRegistry.INSTANCE.getRegisteredInfos() ) {
                ModelResourceLocation modelRes = new ModelResourceLocation(info.getModel(), "inventory");
                meshDef.modelRes.put(info.getUUID(), modelRes);
            }
            ModelBakery.registerItemVariants(ItemRegistry.turret, meshDef.getResLocations());
            renderItem.getItemModelMesher().register(ItemRegistry.turret, meshDef);
        }

        {
            MeshDefAmmo meshDef = new MeshDefAmmo();
            for( TurretAmmo ammo : AmmoRegistry.INSTANCE.getRegisteredTypes() ) {
                ModelResourceLocation modelRes = new ModelResourceLocation(ammo.getModel(), "inventory");
                meshDef.modelRes.put(ammo.getId(), modelRes);
            }
            ModelBakery.registerItemVariants(ItemRegistry.ammo, meshDef.getResLocations());
            renderItem.getItemModelMesher().register(ItemRegistry.ammo, meshDef);
        }

        {
            MeshDefUpgrade meshDef = new MeshDefUpgrade();
            for( TurretUpgrade upg : UpgradeRegistry.INSTANCE.getRegisteredTypes() ) {
                ModelResourceLocation modelRes = new ModelResourceLocation(upg.getModel(), "inventory");
                meshDef.modelRes.put(UpgradeRegistry.INSTANCE.getUpgradeUUID(upg), modelRes);
            }
            ModelBakery.registerItemVariants(ItemRegistry.turretUpgrade, meshDef.getResLocations());
            renderItem.getItemModelMesher().register(ItemRegistry.turretUpgrade, meshDef);
        }

        {
            MeshDefRepkit meshDef = new MeshDefRepkit();
            for( TurretRepairKit kit : RepairKitRegistry.INSTANCE.getRegisteredTypes() ) {
                ModelResourceLocation modelRes = new ModelResourceLocation(kit.getModel(), "inventory");
                meshDef.modelRes.put(RepairKitRegistry.INSTANCE.getTypeId(kit), modelRes);
            }
            ModelBakery.registerItemVariants(ItemRegistry.repairKit, meshDef.getResLocations());
            renderItem.getItemModelMesher().register(ItemRegistry.repairKit, meshDef);
        }
    }

    private static void registerStandardModel(RenderItem renderItem, Item item) {
        renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(TurretModRebirth.ID + ':' + item.getRegistryName().getResourcePath(), "inventory"));
    }

    private static void registerStandardModel(RenderItem renderItem, Block item) {
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(item), 0, new ModelResourceLocation(TurretModRebirth.ID + ':' + item.getRegistryName().getResourcePath(), "inventory"));
    }

    private static abstract class MeshDefUUID<T>
            implements ItemMeshDefinition
    {
        public final Map<UUID, ModelResourceLocation> modelRes = new HashMap<>();

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            T type = getType(stack);
            return type != null ? modelRes.get(getId(type)) : null;
        }

        public abstract T getType(ItemStack stack);
        public abstract UUID getId(T type);

        public ModelResourceLocation[] getResLocations() {
            return this.modelRes.values().toArray(new ModelResourceLocation[this.modelRes.size()]);
        }
    }

    private static final class MeshDefTurret
            extends MeshDefUUID<TurretInfo>
    {
        @Override
        public TurretInfo getType(ItemStack stack) { return ItemTurret.getTurretInfo(stack); }

        @Override
        public UUID getId(TurretInfo type) { return type.getUUID(); }
    }

    private static final class MeshDefAmmo
            extends MeshDefUUID<TurretAmmo>
    {
        @Override
        public TurretAmmo getType(ItemStack stack) { return AmmoRegistry.INSTANCE.getType(stack); }

        @Override
        public UUID getId(TurretAmmo type) { return type.getId(); }
    }

    private static final class MeshDefUpgrade
            extends MeshDefUUID<TurretUpgrade>
    {
        @Override
        public TurretUpgrade getType(ItemStack stack) { return UpgradeRegistry.INSTANCE.getUpgrade(stack); }

        @Override
        public UUID getId(TurretUpgrade type) { return UpgradeRegistry.INSTANCE.getUpgradeUUID(type); }
    }

    private static final class MeshDefRepkit
            extends MeshDefUUID<TurretRepairKit>
    {
        @Override
        public TurretRepairKit getType(ItemStack stack) { return RepairKitRegistry.INSTANCE.getType(stack); }

        @Override
        public UUID getId(TurretRepairKit type) { return RepairKitRegistry.INSTANCE.getTypeId(type); }
    }
}
