/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public final class ModelRegistry
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) throws Exception {
        setStandardModel(ItemRegistry.turret_control_unit);
        setStandardModel(ItemRegistry.turret_info);
        setStandardModel(ItemRegistry.assembly_upg_filter);
        setStandardModel(ItemRegistry.assembly_upg_auto);
        setStandardModel(ItemRegistry.assembly_upg_speed);
        setStandardModel(BlockRegistry.electrolyte_generator);
        setStandardModel(BlockRegistry.turret_assembly);

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectrolyteGenerator.class, new RenderElectrolyteGenerator());
    }

    public static void registerModelsInit() {
        setCustomMeshModel(ItemRegistry.turret_placer, new MeshDefUUID.Turret());
        setCustomMeshModel(ItemRegistry.turret_ammo, new MeshDefUUID.Ammo());
        setCustomMeshModel(ItemRegistry.turret_upgrade, new MeshDefUUID.Upgrade());
        setCustomMeshModel(ItemRegistry.repair_kit, new MeshDefUUID.Repkit());
    }

    private static void setStandardModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void setStandardModel(Block item) {
        Item itm = Item.getItemFromBlock(item);
        if( itm != null ) {
            setStandardModel(itm);
        }
    }

    private static void setCustomMeshModel(Item item, MeshDefUUID<?> mesher) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, mesher);
        ModelBakery.registerItemVariants(item, mesher.getResLocations());
    }

    private static abstract class MeshDefUUID<T>
            implements ItemMeshDefinition
    {
        public final Map<UUID, ModelResourceLocation> modelRes = new HashMap<>();

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            T type = getType(stack);
            return type != null ? this.modelRes.get(getId(type)) : new ModelResourceLocation(stack.getItem().getRegistryName(), "inventory");
        }

        public abstract T getType(ItemStack stack);
        public abstract UUID getId(T type);

        public ResourceLocation[] getResLocations() {
            return this.modelRes.values().toArray(new ModelResourceLocation[this.modelRes.size()]);
        }

        static final class Turret
                extends MeshDefUUID<TurretInfo>
        {
            Turret() {
                for( TurretInfo info : TurretRegistry.INSTANCE.getRegisteredInfos() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(info.getModel(), "inventory");
                    this.modelRes.put(info.getUUID(), modelRes);
                }
            }

            @Override
            public TurretInfo getType(ItemStack stack) { return ItemTurret.getTurretInfo(stack); }

            @Override
            public UUID getId(TurretInfo type) { return type.getUUID(); }
        }

        static final class Ammo
                extends MeshDefUUID<TurretAmmo>
        {
            public Ammo() {
                for( TurretAmmo ammo : AmmoRegistry.INSTANCE.getRegisteredTypes() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(ammo.getModel(), "inventory");
                    this.modelRes.put(ammo.getId(), modelRes);
                }
            }

            @Override
            public TurretAmmo getType(ItemStack stack) { return AmmoRegistry.INSTANCE.getType(stack); }

            @Override
            public UUID getId(TurretAmmo type) { return type.getId(); }
        }

        static final class Upgrade
                extends MeshDefUUID<TurretUpgrade>
        {
            public Upgrade() {
                for( TurretUpgrade upg : UpgradeRegistry.INSTANCE.getRegisteredTypes() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(upg.getModel(), "inventory");
                    this.modelRes.put(UpgradeRegistry.INSTANCE.getUpgradeUUID(upg), modelRes);
                }
            }

            @Override
            public TurretUpgrade getType(ItemStack stack) { return UpgradeRegistry.INSTANCE.getUpgrade(stack); }

            @Override
            public UUID getId(TurretUpgrade type) { return UpgradeRegistry.INSTANCE.getUpgradeUUID(type); }
        }

        static final class Repkit
                extends MeshDefUUID<TurretRepairKit>
        {
            public Repkit() {
                for( TurretRepairKit kit : RepairKitRegistry.INSTANCE.getRegisteredTypes() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(kit.getModel(), "inventory");
                    this.modelRes.put(RepairKitRegistry.INSTANCE.getTypeId(kit), modelRes);
                }
            }

            @Override
            public TurretRepairKit getType(ItemStack stack) { return RepairKitRegistry.INSTANCE.getType(stack); }

            @Override
            public UUID getId(TurretRepairKit type) { return RepairKitRegistry.INSTANCE.getTypeId(type); }
        }
    }
}
