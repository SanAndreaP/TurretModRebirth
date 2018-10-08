/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
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

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TmrConstants.ID)
public final class ModelRegistry
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        setStandardModel(ItemRegistry.TURRET_CONTROL_UNIT);
        setStandardModel(ItemRegistry.TURRET_INFO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_FILTER);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_AUTO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_SPEED);
        setStandardModel(BlockRegistry.ELECTROLYTE_GENERATOR);
        setStandardModel(BlockRegistry.TURRET_ASSEMBLY);

        setCustomMeshModel(ItemRegistry.TURRET_PLACER, new MeshDefUUID.Turret());
        setCustomMeshModel(ItemRegistry.TURRET_AMMO, new MeshDefUUID.Ammo());
        setCustomMeshModel(ItemRegistry.TURRET_UPGRADE, new MeshDefUUID.Upgrade());
        setCustomMeshModel(ItemRegistry.REPAIR_KIT, new MeshDefUUID.Repkit());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurretAssembly.class, new RenderTurretAssembly());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectrolyteGenerator.class, new RenderElectrolyteGenerator());
    }

    private static void setStandardModel(Item item) {
        ResourceLocation regName = item.getRegistryName();
        if( regName != null ) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(regName, "inventory"));
        }
    }

    private static void setStandardModel(Block item) {
        Item itm = Item.getItemFromBlock(item);
        if( itm != Items.AIR ) {
            setStandardModel(itm);
        }
    }

    private static void setCustomMeshModel(Item item, MeshDefUUID<?> mesher) {
        ModelLoader.setCustomMeshDefinition(item, mesher);
        ModelBakery.registerItemVariants(item, mesher.getResLocations());
    }

    private static abstract class MeshDefUUID<T>
            implements ItemMeshDefinition
    {
        final Map<UUID, ModelResourceLocation> modelRes = new HashMap<>();

        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            T type = getType(stack);
            ResourceLocation regName = stack.getItem().getRegistryName();
            if( regName != null ) {
                return type != null ? this.modelRes.get(getId(type)) : new ModelResourceLocation(regName, "inventory");
            } else {
                return null;
            }
        }

        protected abstract T getType(@Nonnull ItemStack stack);
        protected abstract UUID getId(T type);

        ResourceLocation[] getResLocations() {
            return this.modelRes.values().toArray(new ModelResourceLocation[0]);
        }

        static final class Turret
                extends MeshDefUUID<ITurret>
        {
            Turret() {
                for( ITurret info : TurretRegistry.INSTANCE.getTurrets() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(info.getItemModel(), "inventory");
                    this.modelRes.put(info.getId(), modelRes);
                }
            }

            @Override
            public ITurret getType(@Nonnull ItemStack stack) { return TurretRegistry.INSTANCE.getTurret(stack); }

            @Override
            public UUID getId(ITurret type) { return type.getId(); }
        }

        static final class Ammo
                extends MeshDefUUID<IAmmunition>
        {
            Ammo() {
                for( IAmmunition ammo : AmmunitionRegistry.INSTANCE.getTypes() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(ammo.getModel(), "inventory");
                    this.modelRes.put(ammo.getId(), modelRes);
                }
            }

            @Override
            public IAmmunition getType(@Nonnull ItemStack stack) { return AmmunitionRegistry.INSTANCE.getType(stack); }

            @Override
            public UUID getId(IAmmunition type) { return type.getId(); }
        }

        static final class Upgrade
                extends MeshDefUUID<ITurretUpgrade>
        {
            Upgrade() {
                for( ITurretUpgrade upg : UpgradeRegistry.INSTANCE.getUpgrades() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(upg.getModel(), "inventory");
                    this.modelRes.put(UpgradeRegistry.INSTANCE.getUpgradeId(upg), modelRes);
                }
            }

            @Override
            public ITurretUpgrade getType(@Nonnull ItemStack stack) { return UpgradeRegistry.INSTANCE.getUpgrade(stack); }

            @Override
            public UUID getId(ITurretUpgrade type) { return UpgradeRegistry.INSTANCE.getUpgradeId(type); }
        }

        static final class Repkit
                extends MeshDefUUID<TurretRepairKit>
        {
            Repkit() {
                for( TurretRepairKit kit : RepairKitRegistry.INSTANCE.getRegisteredTypes() ) {
                    ModelResourceLocation modelRes = new ModelResourceLocation(kit.getModel(), "inventory");
                    this.modelRes.put(RepairKitRegistry.INSTANCE.getTypeId(kit), modelRes);
                }
            }

            @Override
            public TurretRepairKit getType(@Nonnull ItemStack stack) { return RepairKitRegistry.INSTANCE.getType(stack); }

            @Override
            public UUID getId(TurretRepairKit type) { return RepairKitRegistry.INSTANCE.getTypeId(type); }
        }
    }
}
