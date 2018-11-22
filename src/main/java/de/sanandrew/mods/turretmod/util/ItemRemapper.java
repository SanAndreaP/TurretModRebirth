/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.item.*;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;

@SuppressWarnings("serial")
@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class ItemRemapper
{
    private static final ResourceLocation OLD_AMMO_ID = new ResourceLocation(TmrConstants.ID, "turret_ammo");
    private static final Map<UUID, ResourceLocation> OLD_AMMO_MAPPINGS = new HashMap<UUID, ResourceLocation>() {
        {
            this.put(UUID.fromString("7B497E61-4E8D-4E49-AC71-414751E399E8"), Ammunitions.ARROW.getId());
            this.put(UUID.fromString("3B3AA3F7-DA37-4B92-8F18-53694361447F"), Ammunitions.SGSHELL.getId());
            this.put(UUID.fromString("0B567594-E5CA-48B5-A538-E87C213F439C"), Ammunitions.CRYOCELL_MK1.getId());
            this.put(UUID.fromString("CB5BE826-0480-4D30-AF1F-23BE19329B37"), Ammunitions.CRYOCELL_MK2.getId());
            this.put(UUID.fromString("3181E328-0151-44E0-ADD2-5FCB6B724AEC"), Ammunitions.CRYOCELL_MK3.getId());
            this.put(UUID.fromString("E8CB6C41-00FE-4FA0-AD98-FC8DAD6609AC"), Ammunitions.BULLET.getId());
            this.put(UUID.fromString("3851173D-3AC3-4F17-A488-68C33716AF26"), Ammunitions.MGSHELL.getId());
            this.put(UUID.fromString("E9B4AEDF-AA9C-4041-9C1E-24B20E4D48CD"), Ammunitions.ELECTROLYTECELL.getId());
            this.put(UUID.fromString("48800C6A-9A31-4F45-8AD5-DD02B8B18BCB"), Ammunitions.FLUXCELL.getId());
            this.put(UUID.fromString("0CA51FA8-FD33-4C3D-A9AB-BA29DFFF4ABA"), Ammunitions.FUELTANK.getId());
        }
    };

    private static final ResourceLocation OLD_TURRET_ID = new ResourceLocation(TmrConstants.ID, "turret_placer");
    public static final Map<UUID, ResourceLocation> OLD_TURRET_MAPPINGS = Collections.unmodifiableMap(new HashMap<UUID, ResourceLocation>() {
        {
            this.put(UUID.fromString("50E1E69C-395C-486C-BB9D-41E82C8B22E2"), Turrets.CROSSBOW.getId());
            this.put(UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5"), Turrets.SHOTGUN.getId());
            this.put(UUID.fromString("3AF4D8C3-FCFC-42B0-98A3-BFB669AA7CE6"), Turrets.CRYOLATOR.getId());
            this.put(UUID.fromString("4449D836-F122-409A-8E6C-D7B7438FD08C"), Turrets.REVOLVER.getId());
            this.put(UUID.fromString("97E1FB65-EE36-43BA-A900-583B4BD7973A"), Turrets.MINIGUN.getId());
            this.put(UUID.fromString("95C3D0DC-000E-4E2D-9551-C9C897E072DC"), Turrets.FORCEFIELD.getId());
            this.put(UUID.fromString("F6196022-3F9D-4D3F-B3C1-9ED644DB436B"), Turrets.LASER.getId());
            this.put(UUID.fromString("0C61E401-A5F9-44E9-8B29-3A3DC7762C73"), Turrets.FLAMETHROWER.getId());
        }
    });

    private static final ResourceLocation OLD_UPGRADE_ID = new ResourceLocation(TmrConstants.ID, "turret_upgrade");
    private static final Map<UUID, ResourceLocation> OLD_UPGRADE_MAPPINGS = new HashMap<UUID, ResourceLocation>() {
        {
            this.put(UUID.fromString("1749478F-2A8E-4C56-BC03-6C76CB5DE921"), Upgrades.UPG_STORAGE_I.getId());
            this.put(UUID.fromString("DEFFE281-A2F5-488A-95C1-E9A3BB6E0DD1"), Upgrades.UPG_STORAGE_II.getId());
            this.put(UUID.fromString("50DB1AC3-1CCD-4CB0-AD5A-0777C548655D"), Upgrades.UPG_STORAGE_III.getId());
            this.put(UUID.fromString("2C850D81-0C01-47EA-B3AD-86E4FF523521"), Upgrades.AMMO_STORAGE.getId());
            this.put(UUID.fromString("13218AB7-3DA6-461D-9882-13482291164B"), Upgrades.HEALTH_I.getId());
            this.put(UUID.fromString("612A78CB-ED0C-4990-B1F3-041BE8171B1A"), Upgrades.HEALTH_II.getId());
            this.put(UUID.fromString("2239A7BB-DD38-4764-9FFC-6E04934F9B3C"), Upgrades.HEALTH_III.getId());
            this.put(UUID.fromString("FF6CC60F-EEC7-40C5-92D8-A614DFA06777"), Upgrades.HEALTH_IV.getId());
            this.put(UUID.fromString("4ED4E813-E2D8-43E9-B499-9911E214C5E9"), Upgrades.RELOAD_I.getId());
            this.put(UUID.fromString("80877F84-F03D-4ED8-A9D3-BAF6DF4F3BF1"), Upgrades.RELOAD_II.getId());
            this.put(UUID.fromString("12435AB9-5AA3-4DB9-9B76-7943BA71597A"), Upgrades.SMART_TGT.getId());
            this.put(UUID.fromString("A8F29058-C8B7-400D-A7F4-4CEDE627A7E8"), Upgrades.ECONOMY_I.getId());
            this.put(UUID.fromString("2A76A2EB-0EA3-4EB0-9EC2-61E579361306"), Upgrades.ECONOMY_II.getId());
            this.put(UUID.fromString("C3CF3EE9-8314-4766-A5E0-6033DB3EE9DB"), Upgrades.ECONOMY_INF.getId());
            this.put(UUID.fromString("0ED3D861-F11D-4F6B-B9FC-67E22C8EB538"), Upgrades.ENDER_MEDIUM.getId());
            this.put(UUID.fromString("677FA826-DA2D-40E9-9D86-7FAD7DE398CC"), Upgrades.FUEL_PURIFY.getId());
            this.put(UUID.fromString("90F61412-4ECC-431B-A6AC-288F26C37608"), Upgrades.SHIELD_PERSONAL.getId());
            this.put(UUID.fromString("AB5E19F9-C241-4F3C-B04E-6C276369B0CF"), Upgrades.SHIELD_PROJECTILE.getId());
            this.put(UUID.fromString("853DB6B1-EAEF-4175-B1EE-02F765D24D25"), Upgrades.SHIELD_EXPLOSIVE.getId());
            this.put(UUID.fromString("C03BFDDA-1415-4519-BE59-61C568B6345E"), Upgrades.SHIELD_STRENGTH_I.getId());
            this.put(UUID.fromString("EF8BF1BB-437E-491D-AD6A-03F807987FAE"), Upgrades.SHIELD_STRENGTH_II.getId());
            this.put(UUID.fromString("320F0103-BA1B-4DA6-9ABA-211A1EF84F12"), Upgrades.ENDER_TOXIN_I.getId());
            this.put(UUID.fromString("6A68C909-D73D-49A7-AF71-5366BCEFBB37"), Upgrades.ENDER_TOXIN_II.getId());
        }
    };

    private static final ResourceLocation OLD_REPKIT_ID = new ResourceLocation(TmrConstants.ID, "repair_kit");
    private static final Map<UUID, IRepairKit> OLD_REPKIT_MAPPINGS = new HashMap<UUID, IRepairKit>() {
        {
            this.put(UUID.fromString("89db7dd5-2ded-4e58-96dd-07e47bffa919"), RepairKits.STANDARD_MK1);
            this.put(UUID.fromString("36477c40-3eb3-4997-a2ec-3a9a37be86d5"), RepairKits.STANDARD_MK2);
            this.put(UUID.fromString("c9ecc3ea-8bfa-4e42-b401-e0475a23d7f6"), RepairKits.STANDARD_MK3);
            this.put(UUID.fromString("6b3cbd27-1efa-4ee2-b8c8-35d2988361b9"), RepairKits.STANDARD_MK4);
            this.put(UUID.fromString("4c44ca3d-4f32-44e6-bf2e-11189ec88a73"), RepairKits.REGEN_MK1);

        }
    };

    @SubscribeEvent
    public static void onMissingItem(RegistryEvent.MissingMappings<Item> event) {
        List<RegistryEvent.MissingMappings.Mapping<Item>> list = event.getMappings();
        for( RegistryEvent.MissingMappings.Mapping<Item> map : list ) {
            if( map.key.equals(OLD_AMMO_ID) ) {
                map.remap(ItemRegistry.TURRET_AMMO.get(Ammunitions.ARROW.getId()));
            } else if( map.key.equals(OLD_TURRET_ID) ) {
                map.remap(ItemRegistry.TURRET_PLACERS.get(Turrets.CROSSBOW.getId()));
            } else if( map.key.equals(OLD_UPGRADE_ID) ) {
                map.remap(ItemRegistry.TURRET_UPGRADES.get(UpgradeRegistry.EMPTY_UPGRADE.getId()));
            } else if( map.key.equals(OLD_REPKIT_ID) ) {
                map.remap(ItemRegistry.TURRET_REPAIRKITS.get(RepairKits.STANDARD_MK1.getId()));
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        // replace items in loaded tile entities (chests etc.)
        Chunk chunk = event.getChunk();
        chunk.getTileEntityMap().values().forEach(te -> Arrays.stream(EnumFacing.VALUES).forEach(f -> {
            IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f);
            if( handler instanceof IItemHandlerModifiable ) {
                replaceOldItems((IItemHandlerModifiable) handler);
            }
        }));

        // replace items in entities (donkeys with chests etc.) and turrets
        Arrays.stream(chunk.getEntityLists()).forEach(cimm -> cimm.forEach(e -> {
            IItemHandler handler = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            if( handler instanceof IItemHandlerModifiable ) {
                replaceOldItems((IItemHandlerModifiable) handler);
            }

            if( e instanceof EntityTurret ) {
                EntityTurret turret = (EntityTurret) e;
                ItemStack oldStack = turret.getTargetProcessor().getAmmoStack();
                if( ItemStackUtils.isValid(oldStack) && oldStack.getItem() instanceof ItemAmmo ) {
                    ((TargetProcessor) turret.getTargetProcessor()).setAmmoStackInternal(getNewAmmoStack(oldStack));
                }

                IUpgradeProcessor uProc = turret.getUpgradeProcessor();
                for( int i = 0, max = uProc.getSizeInventory(); i < max; i++ ) {
                    oldStack = uProc.getStackInSlot(i);
                    if( ItemStackUtils.isValid(oldStack) && oldStack.getItem() instanceof ItemUpgrade ) {
                        uProc.setInventorySlotContents(i, getNewUpgradeStack(oldStack));
                    }
                }
            }
        }));
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        // replace items in player inventories
        if( event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote ) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            if( handler instanceof IItemHandlerModifiable ) {
                replaceOldItems((IItemHandlerModifiable) handler);
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }

    private static void replaceOldItems(IItemHandlerModifiable handler) {
        for( int i = 0, max = handler.getSlots(); i < max; i++ ) {
            ItemStack oldStack = handler.getStackInSlot(i);
            if( ItemStackUtils.isValid(oldStack) ) {
                ItemStack stack = ItemStack.EMPTY;
                if( oldStack.getItem() instanceof ItemAmmo ) {
                    stack = getNewAmmoStack(oldStack);
                } else if( oldStack.getItem() instanceof ItemTurret ) {
                    stack = getNewTurretStack(oldStack);
                } else if( oldStack.getItem() instanceof ItemUpgrade ) {
                    stack = getNewUpgradeStack(oldStack);
                } else if( oldStack.getItem() instanceof ItemRepairKit ) {
                    stack = getNewRepkitStack(oldStack);
                }

                if( ItemStackUtils.isValid(stack) ) {
                    stack.setCount(oldStack.getCount());
                    handler.setStackInSlot(i, stack);
                }
            }
        }
    }

    private static ItemStack getNewRepkitStack(ItemStack oldStack) {
        NBTTagCompound nbt = oldStack.getTagCompound();
        if( nbt != null && nbt.hasKey("repKitType", Constants.NBT.TAG_STRING) ) {
            String oldRepkitIdStr = nbt.getString("repKitType");
            UUID oldRepkitId = UuidUtils.isStringUuid(oldRepkitIdStr) ? UUID.fromString(oldRepkitIdStr) : null;
            if( OLD_REPKIT_MAPPINGS.containsKey(oldRepkitId) ) {
                ItemStack stack = RepairKitRegistry.INSTANCE.getItem(OLD_REPKIT_MAPPINGS.get(oldRepkitId).getId());
                stack.setCount(oldStack.getCount());
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getNewUpgradeStack(ItemStack oldStack) {
        NBTTagCompound nbt = oldStack.getTagCompound();
        if( nbt != null && nbt.hasKey("upgradeId", Constants.NBT.TAG_STRING) ) {
            String oldUpgradeIdStr = nbt.getString("upgradeId");
            UUID oldUpgradeId = UuidUtils.isStringUuid(oldUpgradeIdStr) ? UUID.fromString(oldUpgradeIdStr) : null;
            if( OLD_UPGRADE_MAPPINGS.containsKey(oldUpgradeId) ) {
                ItemStack stack = UpgradeRegistry.INSTANCE.getItem(OLD_UPGRADE_MAPPINGS.get(oldUpgradeId));
                stack.setCount(oldStack.getCount());
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getNewAmmoStack(ItemStack oldStack) {
        NBTTagCompound nbt = oldStack.getTagCompound();
        if( nbt != null && nbt.hasKey("ammoType", Constants.NBT.TAG_STRING) ) {
            String oldAmmoTypeStr = nbt.getString("ammoType");
            UUID oldAmmoType = UuidUtils.isStringUuid(oldAmmoTypeStr) ? UUID.fromString(oldAmmoTypeStr) : null;
            if( OLD_AMMO_MAPPINGS.containsKey(oldAmmoType) ) {
                ItemStack stack = AmmunitionRegistry.INSTANCE.getItem(OLD_AMMO_MAPPINGS.get(oldAmmoType));
                stack.setCount(oldStack.getCount());
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getNewTurretStack(ItemStack oldStack) {
        NBTTagCompound nbt = oldStack.getTagCompound();
        if( nbt != null && nbt.hasKey("turretUUID", Constants.NBT.TAG_STRING) ) {
            String oldTurretTypeStr = nbt.getString("turretUUID");
            UUID oldTurretType = UuidUtils.isStringUuid(oldTurretTypeStr) ? UUID.fromString(oldTurretTypeStr) : null;
            if( OLD_TURRET_MAPPINGS.containsKey(oldTurretType) ) {
                ItemStack stack = TurretRegistry.INSTANCE.getItem(OLD_TURRET_MAPPINGS.get(oldTurretType));
                stack.setCount(oldStack.getCount());
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
