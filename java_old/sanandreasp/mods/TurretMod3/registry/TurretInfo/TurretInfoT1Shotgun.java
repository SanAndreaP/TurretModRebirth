package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretInfoT1Shotgun extends TurretInfo {

	public TurretInfoT1Shotgun() {
		this.maxAmmo = 256;
		this.maxHealth = 20;
		this.damage = 1;
		this.maxEXP = 256;
//		this.item = new ItemStack(TM3ModRegistry.turretItem, 1, 1);
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 16.5F;
		this.desc = "turret.desct1s";
		this.crafting = new Object[] {
				" D ", " W ", "CCC",
				'D', new ItemStack(Blocks.dispenser),
				'W', new ItemStack(Blocks.log, 0, OreDictionary.WILDCARD_VALUE),
				'C', new ItemStack(Blocks.stone)
		};
		this.itemIcon = "TurretMod3:turret_02";

		int i = this.registerNewAmmoType("turret.amtp0t1s");
		 this.addAmmo(i, new ItemStack(TM3ModRegistry.ammoItems, 1, 1), 1);
		 this.addAmmo(i, new ItemStack(Blocks.gravel), 8);

		this.healItems.put(new ItemStack(Blocks.stone), 10);
		this.healItems.put(new ItemStack(Blocks.cobblestone), 5);
		this.healItems.put(new ItemStack(Blocks.log, 1, OreDictionary.WILDCARD_VALUE), 2);
		this.healItems.put(new ItemStack(Blocks.planks, 1, OreDictionary.WILDCARD_VALUE), 1);
	}
}
