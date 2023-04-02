/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TurretItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmoItem;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

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
    private static final Comparator<ItemStack> COMPARATOR = (is1, is2) -> {
        Item i1 = is1.getItem();
        Item i2 = is2.getItem();
        if( i1 == ItemRegistry.TURRET_LEXICON || i2 == ItemRegistry.TURRET_LEXICON ) {
            return i1 == ItemRegistry.TURRET_LEXICON ? -1 : 1;
        }
        if( i1 == ItemRegistry.TURRET_CONTROL_UNIT || i2 == ItemRegistry.TURRET_CONTROL_UNIT ) {
            return i1 == ItemRegistry.TURRET_CONTROL_UNIT ? -1 : 1;
        }
//        if( ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_ASSEMBLY) || ItemStackUtils.isBlock(is2, BlockRegistry.TURRET_ASSEMBLY) ) {
//            return ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_ASSEMBLY) ? -1 : 1;
//        }
        if( ItemStackUtils.isBlock(is1, BlockRegistry.ELECTROLYTE_GENERATOR) || ItemStackUtils.isBlock(is2, BlockRegistry.ELECTROLYTE_GENERATOR) ) {
            return ItemStackUtils.isBlock(is1, BlockRegistry.ELECTROLYTE_GENERATOR) ? -1 : 1;
        }
//        if( ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_CRATE) || ItemStackUtils.isBlock(is2, BlockRegistry.TURRET_CRATE) ) {
//            return ItemStackUtils.isBlock(is1, BlockRegistry.TURRET_CRATE) ? -1 : 1;
//        }

        if( i1 instanceof TurretItem || i2 instanceof TurretItem ) {
            if( i1 instanceof TurretItem && i2 instanceof TurretItem ) {
                ITurret t1 = ((TurretItem) i1).getTurret();
                ITurret t2 = ((TurretItem) i2).getTurret();

                int tier1 = t1.getTier();
                int tier2 = t2.getTier();

                if( tier1 != tier2 ) {
                    return Integer.compare(tier1, tier2);
                }
            } else {
                return i1 instanceof TurretItem ? -1 : 1;
            }
        } else if( i1 instanceof AmmoItem || i2 instanceof AmmoItem ) {
            if( i1 instanceof AmmoItem && i2 instanceof AmmoItem ) {
                ITurret t1 = ((AmmoItem) i1).getAmmo().getApplicableTurret();
                ITurret t2 = ((AmmoItem) i2).getAmmo().getApplicableTurret();

                int tier1 = t1.getTier();
                int tier2 = t2.getTier();

                if( tier1 != tier2 ) {
                    return Integer.compare(tier1, tier2);
                }
            } else {
                return i1 instanceof AmmoItem ? -1 : 1;
            }
        } else if( i1 instanceof AmmoCartridgeItem || i2 instanceof AmmoCartridgeItem ) {
            if( i1 instanceof AmmoCartridgeItem && i2 instanceof AmmoCartridgeItem ) {
                AmmoCartridgeInventory a1 = AmmoCartridgeItem.getInventory(is1);
                AmmoCartridgeInventory a2 = AmmoCartridgeItem.getInventory(is2);
                if( a1 == null || a2 == null ) {
                    return a1 == null ? (a2 == null ? 0 : -1) : 1;
                }

                ITurret t1 = a1.getAmmoType().getApplicableTurret();
                ITurret t2 = a2.getAmmoType().getApplicableTurret();

                int tier1 = t1.getTier();
                int tier2 = t2.getTier();

                if( tier1 == tier2 ) {
                    ItemStack isa1 = AmmunitionRegistry.INSTANCE.getItem(a1.getAmmoType(), a1.getAmmoSubtype());
                    ItemStack isa2 = AmmunitionRegistry.INSTANCE.getItem(a2.getAmmoType(), a1.getAmmoSubtype());

                    return isa1.getDisplayName().getString().compareTo(isa2.getDisplayName().getString());
                } else {
                    return Integer.compare(tier1, tier2);
                }
            } else {
                return i1 instanceof AmmoCartridgeItem ? -1 : 1;
            }
        } //else if( i1 instanceof ItemRepairKit || i2 instanceof ItemRepairKit ) {
//            if( i1 instanceof ItemRepairKit && i2 instanceof ItemRepairKit ) {
//                IRepairKit r1 = ((ItemRepairKit) i1).kit;
//                IRepairKit r2 = ((ItemRepairKit) i2).kit;
//
//                if( r1 instanceof RepairKitStandard ) {
//                    if( !(r2 instanceof RepairKitStandard) ) {
//                        return -1;
//                    }
//                } else if( r1 instanceof RepairKitRegeneration ) {
//                    if( !(r2 instanceof RepairKitRegeneration) ) {
//                        return r2 instanceof RepairKitStandard ? 1 : -1;
//                    }
//                } else {
//                    if( r2 instanceof RepairKitStandard || r2 instanceof RepairKitRegeneration ) {
//                        return 1;
//                    }
//                }
//
//                return Float.compare(r1.getHealAmount(), r2.getHealAmount());
//            } else {
//                return i1 instanceof ItemRepairKit ? -1 : 1;
//            }
//        } else if( i1 instanceof ItemAssemblyUpgrade || i2 instanceof ItemAssemblyUpgrade ) {
//            if( i1 instanceof ItemAssemblyUpgrade && i2 instanceof ItemAssemblyUpgrade ) {
//                return is1.getDisplayName().compareTo(is2.getDisplayName());
//            } else {
//                return i1 instanceof ItemAssemblyUpgrade ? -1 : 1;
//            }
//        } else if( i1 instanceof ItemUpgrade || i2 instanceof ItemUpgrade ) {
//            if( i1 instanceof ItemUpgrade && i2 instanceof ItemUpgrade ) {
//                IUpgrade u1 = ((ItemUpgrade) i1).upgrade;
//                IUpgrade u2 = ((ItemUpgrade) i2).upgrade;
//
//                if( u1 == UpgradeRegistry.EMPTY_UPGRADE || u2 == UpgradeRegistry.EMPTY_UPGRADE ) {
//                    return u1 == UpgradeRegistry.EMPTY_UPGRADE ? -1 : 1;
//                }
//            } else {
//                return i1 instanceof ItemUpgrade ? -1 : 1;
//            }
//        }

        return is1.getDisplayName().getString().compareTo(is2.getDisplayName().getString());
    };

    public static Comparator<ItemStack> getComparator() {
        return COMPARATOR;
    }
}
