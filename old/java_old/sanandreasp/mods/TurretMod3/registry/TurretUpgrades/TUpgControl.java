package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.*;

public class TUpgControl extends TurretUpgrades {

	public TUpgControl() {
		this.upgName = "upgrades.nameControl";
		this.upgDesc = "upgrades.descControl";
		this.upgItem = new ItemStack(Items.saddle);

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
	}
}
