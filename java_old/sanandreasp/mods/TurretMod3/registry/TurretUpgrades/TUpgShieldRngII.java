package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;

public class TUpgShieldRngII extends TurretUpgrades {

	public TUpgShieldRngII() {
		this.upgName = "upgrades.nameShieldRngII";
		this.upgDesc = "upgrades.descShieldRngII";
		this.upgItem = new ItemStack(Blocks.gold_block);

		this.requiredUpg = TUpgShieldRngI.class;

		this.turrets.add(EntityTurret_TSForcefield.class);
	}
}
