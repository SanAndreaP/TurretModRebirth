package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;

public class TUpgShieldPointsIncr extends TurretUpgrades {

	public TUpgShieldPointsIncr() {
		this.upgName = "upgrades.nameShieldPtsIncr";
		this.upgDesc = "upgrades.descShieldPtsIncr";
		this.upgItem = new ItemStack(Blocks.obsidian);

		this.requiredUpg = TUpgShieldRngI.class;

		this.turrets.add(EntityTurret_TSForcefield.class);
	}
}
