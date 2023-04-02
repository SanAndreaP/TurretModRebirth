package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;

public class TUpgShieldMobPush extends TurretUpgrades {

	public TUpgShieldMobPush() {
		this.upgName = "upgrades.nameShieldPush";
		this.upgDesc = "upgrades.descShieldPush";
		this.upgItem = new ItemStack(Items.ghast_tear);

		this.turrets.add(EntityTurret_TSForcefield.class);
	}
}
