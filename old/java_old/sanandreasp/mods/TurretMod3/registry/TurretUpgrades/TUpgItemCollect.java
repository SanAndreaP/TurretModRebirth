package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSCollector;

public class TUpgItemCollect extends TurretUpgrades {

	public TUpgItemCollect() {
		this.upgName = "upgrades.nameICollect";
		this.upgDesc = "upgrades.descICollect";
		this.upgItem = new ItemStack(Blocks.chest);

		this.turrets.add(EntityTurret_TSCollector.class);

		this.requiredUpg = TUpgTurretCollect.class;
	}
}
