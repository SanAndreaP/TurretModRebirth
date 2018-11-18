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
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderElectrolyteGenerator;
import de.sanandrew.mods.turretmod.client.render.tileentity.RenderTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
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
import java.util.Objects;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TmrConstants.ID)
public final class ModelRegistry
{
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        setStandardModel(ItemRegistry.TURRET_CONTROL_UNIT);
        setStandardModel(ItemRegistry.TURRET_INFO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_FILTER);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_AUTO);
        setStandardModel(ItemRegistry.ASSEMBLY_UPG_SPEED);
        setStandardModel(BlockRegistry.ELECTROLYTE_GENERATOR);
        setStandardModel(BlockRegistry.TURRET_ASSEMBLY);

        ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "turrets/" + regName.getResourcePath()));
        });
        ItemRegistry.TURRET_AMMO.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "ammo/" + regName.getResourcePath()));
        });
        ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> {
            ResourceLocation regName = Objects.requireNonNull(item.getRegistryName());
            setStandardModel(item, new ResourceLocation(regName.getResourceDomain(), "upgrades/" + regName.getResourcePath()));
        });
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

    private static void setStandardModel(Item item, ResourceLocation modelLocation) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modelLocation, "inventory"));
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
