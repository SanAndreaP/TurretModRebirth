package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSSnowball;

public class TUpgStopMove extends TurretUpgrades {

	public TUpgStopMove() {
		this.upgName = "upgrades.nameStopMove";
		this.upgDesc = "upgrades.descStopMove";
		this.upgItem = new ItemStack(Blocks.obsidian);

		this.turrets.add(EntityTurret_TSSnowball.class);

		this.requiredUpg = TUpgSlowdownII.class;
	}

}
