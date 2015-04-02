package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_T5Artillery;

public class TUpgPrecision extends TurretUpgrades {

	public TUpgPrecision() {
		this.upgName = "upgrades.namePrec";
		this.upgDesc = "upgrades.descPrec";
		this.upgItem = new ItemStack(Items.magma_cream);

		this.turrets.add(EntityTurret_T5Artillery.class);
	}

}
