package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSSnowball;

public class TUpgSlowdownII extends TurretUpgrades {

	public TUpgSlowdownII() {
		this.upgName = "upgrades.nameSlowII";
		this.upgDesc = "upgrades.descSlowII";
		this.upgItem = new ItemStack(Blocks.ice);

		this.turrets.add(EntityTurret_TSSnowball.class);
	}

}
