package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_TSForcefield;

public class TUpgShieldRngI extends TurretUpgrades {

	public TUpgShieldRngI() {
		this.upgName = "upgrades.nameShieldRngI";
		this.upgDesc = "upgrades.descShieldRngI";
		this.upgItem = new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE);

		this.turrets.add(EntityTurret_TSForcefield.class);
	}
}
