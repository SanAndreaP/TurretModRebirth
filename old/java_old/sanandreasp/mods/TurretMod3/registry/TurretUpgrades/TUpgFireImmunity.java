package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.*;

public class TUpgFireImmunity extends TurretUpgrades {

	public TUpgFireImmunity() {
		this.upgName = "upgrades.nameFireImmune";
		this.upgDesc = "upgrades.descFireImmune";
		this.upgItem = new ItemStack(Blocks.netherrack);

		this.turrets.add(EntityTurret_T1Arrow.class);
		this.turrets.add(EntityTurret_T1Shotgun.class);
		this.turrets.add(EntityTurret_T2Minigun.class);
		this.turrets.add(EntityTurret_T2Revolver.class);
		this.turrets.add(EntityTurret_T3Laser.class);
		this.turrets.add(EntityTurret_TSSnowball.class);
		this.turrets.add(EntityTurret_TSCollector.class);
		this.turrets.add(EntityTurret_TSForcefield.class);
		this.turrets.add(EntityTurret_TSHealer.class);
	}
}
