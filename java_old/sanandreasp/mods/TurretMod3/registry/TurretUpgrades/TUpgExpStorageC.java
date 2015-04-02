package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSCollector;

public class TUpgExpStorageC extends TurretUpgrades {

	public TUpgExpStorageC() {
		this.upgName = "upgrades.nameMoreXP";
		this.upgDesc = "upgrades.descMoreXP";
		this.upgItem = new ItemStack(Items.ender_eye);

		this.turrets.add(EntityTurret_TSCollector.class);
	}
}
