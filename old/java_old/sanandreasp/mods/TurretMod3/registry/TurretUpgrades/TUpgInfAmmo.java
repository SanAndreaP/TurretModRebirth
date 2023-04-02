package sanandreasp.mods.TurretMod3.registry.TurretUpgrades;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.entity.turret.*;

public class TUpgInfAmmo extends TurretUpgrades {

	public TUpgInfAmmo() {
		this.upgName = "upgrades.nameInfbow";
		this.upgDesc = "upgrades.descInfbow";
		this.upgItem = new ItemStack(Items.bow);
		this.upgEnchantment = Enchantment.infinity;

		this.requiredUpg = TUpgEconomy.class;

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
