package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TUpgShieldRepairIncr extends TurretUpgrades {

	public TUpgShieldRepairIncr() {
		this.upgName = "upgrades.nameShieldRepIncr";
		this.upgDesc = "upgrades.descShieldRepIncr";
		this.upgItem = new ItemStack(TM3ModRegistry.httm);

		this.requiredUpg = TUpgShieldPointsIncr.class;

		this.turrets.add(EntityTurret_TSForcefield.class);
	}
}
