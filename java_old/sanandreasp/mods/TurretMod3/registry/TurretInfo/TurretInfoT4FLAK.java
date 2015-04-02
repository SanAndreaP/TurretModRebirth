package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT4FLAK extends TurretInfo {

	public TurretInfoT4FLAK() {
		this.maxAmmo = 256;
		this.maxHealth = 80;
		this.damage = 2;
		this.maxEXP = 256;
		this.lowerRangeY = 0.0F;
		this.upperRangeY = 50.5F;
		this.rangeX = 50.5F;
		this.desc = "turret.desct4f";
		this.crafting = new Object[] {
				"SDL", " L ", "OOO",
				'S', new ItemStack(TM3ModRegistry.httm),
				'D', new ItemStack(Blocks.dispenser),
				'L', new ItemStack(Blocks.nether_brick),
				'O', new ItemStack(Blocks.obsidian)
		};
		this.itemIcon = "TurretMod3:turret_08";

		int i = this.registerNewAmmoType("turret.amtp0t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 0), 1);
		i = this.registerNewAmmoType("turret.amtp1t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 1), 1);
		i = this.registerNewAmmoType("turret.amtp2t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 2), 1);
		i = this.registerNewAmmoType("turret.amtp3t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 3), 1);
		i = this.registerNewAmmoType("turret.amtp4t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 4), 1);
		i = this.registerNewAmmoType("turret.amtp5t4f");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.rocket, 1, 5), 1);

		this.healItems.put(new ItemStack(Blocks.obsidian), 40);
		this.healItems.put(new ItemStack(Blocks.nether_brick), 20);
	}
}
