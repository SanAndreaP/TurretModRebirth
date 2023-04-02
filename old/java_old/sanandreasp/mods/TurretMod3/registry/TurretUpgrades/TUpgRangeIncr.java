package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSCollector;

public class TUpgRangeIncr extends TurretUpgrades {

	public TUpgRangeIncr() {
		this.upgName = "upgrades.nameRangeIncr";
		this.upgDesc = "upgrades.descRangeIncr";
		this.upgItem = new ItemStack(Blocks.gold_block);

		this.turrets.add(EntityTurret_TSCollector.class);
	}
}
