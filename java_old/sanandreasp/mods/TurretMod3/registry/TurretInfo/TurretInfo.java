package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class TurretInfo {

	private static Map<Class<? extends EntityTurret_Base>, TurretInfo> turrets = Maps.newHashMap();
	private static Map<Integer, Class<? extends EntityTurret_Base>> turretList = Maps.newHashMap();

	private String name;
    private int infoID = -1;
	protected String desc;
	protected Object[] crafting;
	protected int maxAmmo;
	protected int maxHealth;
	protected int damage;
	protected int maxEXP;
	protected double lowerRangeY;
	protected double upperRangeY;
	protected double rangeX;
	protected String itemIcon;
	protected Map<ItemStack, Integer> ammoItems = Maps.newHashMap();
	protected Map<Integer, String> ammoTypeNames = Maps.newHashMap();
	protected Map<Integer, List<ItemStack>> ammoTypeItems = Maps.newHashMap();
	protected Map<ItemStack, Integer> healItems = Maps.newHashMap();

	public static TurretInfo getTurretInfo(Class par0TurretCls) {
		return turrets.get(par0TurretCls);
	}

    public void setName(String partName){
        name = "entity."+partName+".name";
    }

	public String getIconFile() {
		return this.itemIcon;
	}

	public int getDamage() {
		return this.damage;
	}

	public int getMaxXP() {
		return this.maxEXP;
	}

	public static Class<? extends EntityTurret_Base> getTurretClass(int id) {
		return turretList.get(id);
	}

	public static int getTurretCount() {
		return turrets.size();
	}

	public Map<ItemStack, Integer> getAmmoItems() {
		return Maps.newHashMap(this.ammoItems);
	}

	public Map<ItemStack, Integer> getHealthItems() {
		return Maps.newHashMap(this.healItems);
	}

	public int getAmmoFromItem(ItemStack is) {
		for (Entry<ItemStack, Integer> ent : this.ammoItems.entrySet()) {
			if (TM3ModRegistry.areStacksEqualWithWildcard(ent.getKey(), is))
				return ent.getValue();
		}
		return 0;
	}

	protected int registerNewAmmoType(String name) {
		int i = this.ammoTypeNames.size();
		this.ammoTypeNames.put(i, name);
		this.ammoTypeItems.put(i, new ArrayList<ItemStack>());
		return i;
	}

	protected void addAmmo(int ammoType, ItemStack ammo, int ammoCount) {
		this.ammoItems.put(ammo, ammoCount);
		this.ammoTypeItems.get(ammoType).add(ammo);
	}

	public int getAmmoTypeFromItem(ItemStack is) {
		for (Entry<Integer, List<ItemStack>> ent : this.ammoTypeItems.entrySet()) {
			for (ItemStack entIS : ent.getValue()) {
				if (TM3ModRegistry.areStacksEqualWithWildcard(entIS, is))
					return ent.getKey();
			}
		}
		// for (Entry<Integer, ItemStack> ent : this.ammoTypes.entrySet()) {
		// if (ent.getValue().isItemEqual(is) || (ent.getValue().getItemDamage()
		// < 0 && ent.getValue().itemID == is.itemID))
		// return ent.getKey();
		// }
		return 0;
	}

	public String getAmmoTypeNameFromIndex(int ind) {
		for (Entry<Integer, String> ent : this.ammoTypeNames.entrySet()) {
			if (ent.getKey() == ind)
				return StatCollector.translateToLocal(ent.getValue());
		}
		return "";
	}

	public ItemStack getAmmoTypeItemWithLowestScore(int amtype) {
		int min = -1;
		ItemStack curIS = null;
		if (ammoTypeItems == null || ammoTypeItems.size() < 1)
			return null;
		for (ItemStack is : ammoTypeItems.get(amtype)) {
			int cur = this.getAmmoFromItem(is);
			if (cur < min || min < 0) {
				min = cur;
				curIS = is;
			}
		}
		return curIS;
	}

	public int getHealthFromItem(ItemStack is) {
		for (Entry<ItemStack, Integer> ent : this.healItems.entrySet()) {
			if (TM3ModRegistry.areStacksEqualWithWildcard(ent.getKey(), is))
				return ent.getValue();
		}
		return 0;
	}

	public String getTurretName() {
		return StatCollector.translateToLocal(this.name);
	}

	public String getTurretDesc() {
		return StatCollector.translateToLocal(this.desc);
	}

	public ItemStack getTurretItem() {
		return new ItemStack(TM3ModRegistry.turretItem, 1, this.infoID);
	}

	public int getInfoID() {
		return this.infoID;
	}

	public Object[] getCrafting() {
		return this.crafting.clone();
	}

	public int getMaxAmmo() {
		return this.maxAmmo;
	}

	public int getMaxHealth() {
		return this.maxHealth;
	}

	public double getYRangeLow() {
		return this.lowerRangeY;
	}

	public double getYRangeHigh() {
		return this.upperRangeY;
	}

	public double getXRange() {
		return this.rangeX;
	}

	public static void addTurretInfo(Class<? extends EntityTurret_Base> tClass, TurretInfo tInf) {
        tInf.setName(TM3ModRegistry.modID+"."+EntityRegistry.instance().lookupModSpawn(tClass, false).getEntityName());
		turretList.put(tInf.infoID = turretList.size(), tClass);
		turrets.put(tClass, tInf);
	}
}
