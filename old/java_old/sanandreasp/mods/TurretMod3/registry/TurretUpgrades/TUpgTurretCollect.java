package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSCollector;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TUpgTurretCollect extends TurretUpgrades {

	public TUpgTurretCollect() {
		this.upgName = "upgrades.nameTCollect";
		this.upgDesc = "upgrades.descTCollect";
		this.upgItem = new ItemStack(TM3ModRegistry.httm);

		this.turrets.add(EntityTurret_TSCollector.class);
	}
}
