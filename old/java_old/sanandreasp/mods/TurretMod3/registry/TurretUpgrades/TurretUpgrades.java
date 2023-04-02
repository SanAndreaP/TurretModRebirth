package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TurretUpgrades {
	protected int id;
	protected ItemStack upgItem;
	protected Enchantment upgEnchantment = null;
	protected String upgName;
	protected String upgDesc;
	protected List<Class<? extends EntityTurret_Base>> turrets = new ArrayList<Class<? extends EntityTurret_Base>>();
	protected static Map<Integer, TurretUpgrades> upgradeListINT = Maps.newHashMap();
	protected static Map<Class, TurretUpgrades> upgradeListCLT = Maps.newHashMap();
	protected Class<? extends TurretUpgrades> requiredUpg = null;

	public TurretUpgrades() { }

	public ItemStack getItem() {
		return this.upgItem.copy();
	}

	public Enchantment getEnchantment() {
		return this.upgEnchantment;
	}

	public boolean hasRequiredUpgrade(Map<Integer, ItemStack> upgMap) {
        return this.requiredUpg == null || hasUpgrade(this.requiredUpg, upgMap);
    }

	public boolean hasRequiredUpgrade(List<ItemStack> upgMap) {
        return this.requiredUpg == null || hasUpgrade(this.requiredUpg, upgMap);
    }

	public String getReqUpgradeName() {
		return this.requiredUpg != null ? TurretUpgrades.upgradeListCLT.get(this.requiredUpg).getName() : "";
	}

	public String getName() {
		return StatCollector.translateToLocal(this.upgName);
	}

	public String getDesc() {
		return StatCollector.translateToLocal(this.upgDesc);
	}

	public List<Class<? extends EntityTurret_Base>> getTurrets() {
		return new ArrayList<Class<? extends EntityTurret_Base>>(this.turrets);
	}

	public static boolean isUpgradeForTurret(TurretUpgrades upg, Class<? extends EntityTurret_Base> turretCls) {
		if (turretCls == null)
			return false;
		for (Class<? extends EntityTurret_Base> upgCls : upg.getTurrets()) {
			if (upgCls.isAssignableFrom(turretCls))
				return true;
		}
		return false;
	}

	public static void addUpgrade(TurretUpgrades upg) {
		upg.id = upgradeListINT.size();
		upgradeListINT.put(upg.id, upg);
		upgradeListCLT.put(upg.getClass(), upg);
	}

	public static int getUpgradeCount() {
		return upgradeListINT.size();
	}

	public static TurretUpgrades getUpgradeFromID(int id) {
		return upgradeListINT.get(id);
	}

	public static TurretUpgrades getUpgradeFromItem(ItemStack stack, Class<? extends EntityTurret_Base> turretCls) {
		if (stack == null) return null;
		for (int i = 0; i < upgradeListINT.size(); i++) {
			TurretUpgrades upg = getUpgradeFromID(i);
			if (TM3ModRegistry.areStacksEqualWithWildcard(upg.getItem(), stack) && (turretCls == null || isUpgradeForTurret(upg, turretCls)))
				return getUpgradeFromID(i);
		}
		return null;
	}

	public int getUpgradeID() {
		return this.id;
	}

	public static boolean hasUpgrade(Class<? extends TurretUpgrades> tUpg, Map<Integer, ItemStack> upgMap) {
		if (upgMap == null || tUpg == null)
			return false;

		TurretUpgrades chkUpg = upgradeListCLT.get(tUpg);

		if (!upgMap.containsKey(chkUpg.getUpgradeID()) || (upgMap.containsKey(chkUpg.getUpgradeID()) && upgMap.get(chkUpg.getUpgradeID()) == null))
			return false;

		if (upgMap.containsKey(chkUpg.getUpgradeID()) && upgMap.get(chkUpg.getUpgradeID()) != null) {
			ItemStack is = upgMap.get(chkUpg.getUpgradeID());
			if (TM3ModRegistry.areStacksEqualWithWildcard(chkUpg.getItem(), is)) {
				if (is.isItemEnchanted() && chkUpg.getEnchantment() != null) {
					NBTTagList ench = is.getEnchantmentTagList();
					for (int j = 0; j < ench.tagCount(); ++j) {
						NBTTagCompound var4 = ench.getCompoundTagAt(j);
						if (var4.getShort("id") == chkUpg.getEnchantment().effectId) {
							return true;
						}
					}
				} else if (chkUpg.getEnchantment() == null) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasUpgrade(Class<? extends TurretUpgrades> tUpg, List<ItemStack> upgMap) {
		if (upgMap == null || tUpg == null)
			return false;

		TurretUpgrades chkUpg = upgradeListCLT.get(tUpg);

		for (ItemStack is : upgMap) {
			if (TM3ModRegistry.areStacksEqualWithWildcard(chkUpg.getItem(), is)) {
				if (is.isItemEnchanted() && chkUpg.getEnchantment() != null) {
					NBTTagList ench = is.getEnchantmentTagList();
					for (int j = 0; j < ench.tagCount(); ++j) {
						NBTTagCompound var4 = ench.getCompoundTagAt(j);
						if (var4.getShort("id") == chkUpg.getEnchantment().effectId) {
							return true;
						}
					}
				} else if (chkUpg.getEnchantment() == null) {
					return true;
				}
			}
		}
		return false;
	}
}
