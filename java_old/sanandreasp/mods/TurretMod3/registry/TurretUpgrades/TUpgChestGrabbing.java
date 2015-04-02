package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.*;

public class TUpgChestGrabbing extends TurretUpgrades {

	public TUpgChestGrabbing() {
		this.upgName = "upgrades.nameChest";
		this.upgDesc = "upgrades.descChest";
		this.upgItem = new ItemStack(Blocks.chest);

		this.turrets.add(EntityTurret_T1Arrow.class);
		this.turrets.add(EntityTurret_T1Shotgun.class);
		this.turrets.add(EntityTurret_T2Minigun.class);
		this.turrets.add(EntityTurret_T2Revolver.class);
		this.turrets.add(EntityTurret_T3Laser.class);
		this.turrets.add(EntityTurret_T3Flamethrower.class);
		this.turrets.add(EntityTurret_T4Sniper.class);
		this.turrets.add(EntityTurret_T4FLAK.class);
		this.turrets.add(EntityTurret_T5Railgun.class);
		this.turrets.add(EntityTurret_T5Artillery.class);
		this.turrets.add(EntityTurret_TSSnowball.class);
		this.turrets.add(EntityTurret_TSHealer.class);
	}
}
