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
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class ItemRemapper
{
    private static final ResourceLocation OLD_AMMO_ID = new ResourceLocation(TmrConstants.ID, "turret_ammo");
    private static final Map<UUID, ResourceLocation> OLD_AMMO_MAPPINGS = new HashMap<UUID, ResourceLocation>() {
        private static final long serialVersionUID = 95937088410596615L;
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
    public static final Map<UUID, ITurret> OLD_TURRET_MAPPINGS = Collections.unmodifiableMap(new HashMap<UUID, ITurret>() {
        private static final long serialVersionUID = -8711592707424745524L;
        {
            this.put(UUID.fromString("50E1E69C-395C-486C-BB9D-41E82C8B22E2"), Turrets.CROSSBOW);
            this.put(UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5"), Turrets.SHOTGUN);
            this.put(UUID.fromString("3AF4D8C3-FCFC-42B0-98A3-BFB669AA7CE6"), Turrets.CRYOLATOR);
            this.put(UUID.fromString("4449D836-F122-409A-8E6C-D7B7438FD08C"), Turrets.REVOLVER);
            this.put(UUID.fromString("97E1FB65-EE36-43BA-A900-583B4BD7973A"), Turrets.MINIGUN);
            this.put(UUID.fromString("95C3D0DC-000E-4E2D-9551-C9C897E072DC"), Turrets.FORCEFIELD);
            this.put(UUID.fromString("F6196022-3F9D-4D3F-B3C1-9ED644DB436B"), Turrets.LASER);
            this.put(UUID.fromString("0C61E401-A5F9-44E9-8B29-3A3DC7762C73"), Turrets.FLAMETHROWER);
        }
    });

    @SubscribeEvent
    public static void onMissingItem(RegistryEvent.MissingMappings<Item> event) {
        List<RegistryEvent.MissingMappings.Mapping<Item>> list = event.getMappings();
        for( RegistryEvent.MissingMappings.Mapping<Item> map : list ) {
            if( map.key.equals(OLD_AMMO_ID) ) {
                ItemRegistry.TURRET_AMMO.values().stream().findFirst().ifPresent(map::remap);
            } else if( map.key.equals(OLD_TURRET_ID) ) {
                ItemRegistry.TURRET_PLACERS.values().stream().findFirst().ifPresent(map::remap);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoaded(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        chunk.getTileEntityMap().values().forEach(te ->  {
            IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            if( handler instanceof IItemHandlerModifiable ) {
                replaceOldItems((IItemHandlerModifiable) handler);
            }
        });

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
            }
        }));
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if( event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote ) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
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
                }

                if( ItemStackUtils.isValid(stack) ) {
                    stack.setCount(oldStack.getCount());
                    handler.setStackInSlot(i, stack);
                }
            }
        }
    }

    private static ItemStack getNewAmmoStack(ItemStack oldStack) {
        NBTTagCompound nbt = oldStack.getTagCompound();
        if( nbt != null && nbt.hasKey("ammoType", Constants.NBT.TAG_STRING) ) {
            String oldAmmoTypeStr = nbt.getString("ammoType");
            UUID oldAmmoType = UuidUtils.isStringUuid(oldAmmoTypeStr) ? UUID.fromString(oldAmmoTypeStr) : null;
            if( OLD_AMMO_MAPPINGS.containsKey(oldAmmoType) ) {
                ItemStack stack = AmmunitionRegistry.INSTANCE.getAmmoItem(OLD_AMMO_MAPPINGS.get(oldAmmoType));
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
                ItemStack stack = TurretRegistry.INSTANCE.getTurretItem(OLD_TURRET_MAPPINGS.get(oldTurretType));
                stack.setCount(oldStack.getCount());
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
