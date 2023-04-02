package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_T2Minigun;

public class TUpgPiercing extends TurretUpgrades {

	public TUpgPiercing() {
		this.upgName = "upgrades.nameGold";
		this.upgDesc = "upgrades.descGold";
		this.upgItem = new ItemStack(Items.gold_nugget);

		this.turrets.add(EntityTurret_T2Minigun.class);
	}

}
