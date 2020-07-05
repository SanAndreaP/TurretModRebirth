package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemRepairKit;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ResourceOrderer
{
    private ResourceOrderer() { }

    /*
    01. Turret Lexicon
    02. TCU
    03. Turret Assembly Table
    04. Electrolyte Generator
    05. Turret Crate
    06. turrets
    07. ammo
    08. ammo cartridges
    09. repair kits
    10. assembly upgrades
    11. empty upgrade
    12. upgrades
     */
    public static void orderItems(List<ItemStack> input) {
        input.sort((is1, is2) -> {
            Item i1 = is1.getItem();
            Item i2 = is2.getItem();
            if( i1 == ItemRegistry.TURRET_INFO || i2 == ItemRegistry.TURRET_INFO ) {
                return i1 == ItemRegistry.TURRET_INFO ? -1 : 1;
            }
            if( i1 == ItemRegistry.TURRET_CONTROL_UNIT || i2 == ItemRegistry.TURRET_CONTROL_UNIT ) {
                return i1 == ItemRegistry.TURRET_CONTROL_UNIT ? -1 : 1;
            }
            if( ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_ASSEMBLY) || ItemStackUtils.isBlock(is2, BlockRegistry.TURRET_ASSEMBLY) ) {
                return ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_ASSEMBLY) ? -1 : 1;
            }
            if( ItemStackUtils.isBlock(is1, BlockRegistry.ELECTROLYTE_GENERATOR) || ItemStackUtils.isBlock(is2, BlockRegistry.ELECTROLYTE_GENERATOR) ) {
                return ItemStackUtils.isBlock(is1, BlockRegistry.ELECTROLYTE_GENERATOR) ? -1 : 1;
            }
            if( ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_CRATE) || ItemStackUtils.isBlock(is2, BlockRegistry.TURRET_CRATE) ) {
                return ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_CRATE) ? -1 : 1;
            }

            if( i1 instanceof ItemTurret || i2 instanceof ItemTurret ) {
                if( i1 instanceof ItemTurret && i2 instanceof ItemTurret ) {
                    ITurret t1 = ((ItemTurret) i1).turret;
                    ITurret t2 = ((ItemTurret) i2).turret;

                    int tier1 = t1.getTier();
                    int tier2 = t2.getTier();

                    if( tier1 != tier2 ) {
                        return Integer.compare(tier1, tier2);
                    }
                } else {
                    return i1 instanceof ItemTurret ? -1 : 1;
                }
            } else if( i1 instanceof ItemAmmo || i2 instanceof ItemAmmo ) {
                if( i1 instanceof ItemAmmo && i2 instanceof ItemAmmo ) {
                    ITurret t1 = ((ItemAmmo) i1).ammo.getTurret();
                    ITurret t2 = ((ItemAmmo) i2).ammo.getTurret();

                    int tier1 = t1.getTier();
                    int tier2 = t2.getTier();

                    if( tier1 != tier2 ) {
                        return Integer.compare(tier1, tier2);
                    }
                } else {
                    return i1 instanceof ItemAmmo ? -1 : 1;
                }
            } else if( i1 instanceof ItemAmmoCartridge || i2 instanceof ItemAmmoCartridge ) {
                if( i1 instanceof ItemAmmoCartridge && i2 instanceof ItemAmmoCartridge ) {
                    AmmoCartridgeInventory a1 = ItemAmmoCartridge.getInventory(is1);
                    AmmoCartridgeInventory a2 = ItemAmmoCartridge.getInventory(is2);
                    if( a1 == null || a2 == null ) {
                        return a1 == null ? (a2 == null ? 0 : -1) : 1;
                    }

                    ITurret t1 = a1.getAmmoType().getTurret();
                    ITurret t2 = a2.getAmmoType().getTurret();

                    int tier1 = t1.getTier();
                    int tier2 = t2.getTier();

                    if( tier1 == tier2 ) {
                        ItemStack isa1 = AmmunitionRegistry.INSTANCE.getItem(a1.getAmmoType().getId(), a1.getAmmoSubtype());
                        ItemStack isa2 = AmmunitionRegistry.INSTANCE.getItem(a2.getAmmoType().getId(), a1.getAmmoSubtype());

                        return isa1.getDisplayName().compareTo(isa2.getDisplayName());
                    } else {
                        return Integer.compare(tier1, tier2);
                    }
                } else {
                    return i1 instanceof ItemAmmoCartridge ? -1 : 1;
                }
            } else if( i1 instanceof ItemRepairKit || i2 instanceof ItemRepairKit ) {
                if( i1 instanceof ItemRepairKit && i2 instanceof ItemRepairKit ) {
                    return Float.compare(((ItemRepairKit) i1).kit.getHealAmount(), ((ItemRepairKit) i2).kit.getHealAmount());
                } else {
                    return i1 instanceof ItemRepairKit ? -1 : 1;
                }
            } else if( i1 instanceof ItemAssemblyUpgrade || i2 instanceof ItemAssemblyUpgrade ) {
                if( i1 instanceof ItemAssemblyUpgrade && i2 instanceof ItemAssemblyUpgrade ) {
                    return is1.getDisplayName().compareTo(is2.getDisplayName());
                } else {
                    return i1 instanceof ItemAssemblyUpgrade ? -1 : 1;
                }
            } else if( i1 instanceof ItemUpgrade || i2 instanceof ItemUpgrade ) {
                if( i1 instanceof ItemUpgrade && i2 instanceof ItemUpgrade ) {
                    IUpgrade u1 = ((ItemUpgrade) i1).upgrade;
                    IUpgrade u2 = ((ItemUpgrade) i2).upgrade;

                    if( u1 == UpgradeRegistry.EMPTY_UPGRADE || u2 == UpgradeRegistry.EMPTY_UPGRADE ) {
                        return u1 == UpgradeRegistry.EMPTY_UPGRADE ? -1 : 1;
                    }
                } else {
                    return i1 instanceof ItemUpgrade ? -1 : 1;
                }
            }

            return is1.getDisplayName().compareTo(is2.getDisplayName());
        });
    }
}
