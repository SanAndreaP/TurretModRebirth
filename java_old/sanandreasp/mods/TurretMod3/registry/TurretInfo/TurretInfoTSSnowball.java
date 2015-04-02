package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TurretInfoTSSnowball extends TurretInfo {

	public TurretInfoTSSnowball() {
		this.maxAmmo = 256;
		this.maxHealth = 20;
		this.maxEXP = 256;
		this.damage = 0;
		this.lowerRangeY = 5.5F;
		this.upperRangeY = 5.5F;
		this.rangeX = 16.5F;
		this.desc = "turret.desctss";
		this.crafting = new Object[] {
				" DS", " W ", "CSC",
				'D', new ItemStack(Blocks.dispenser),
				'S', new ItemStack(Blocks.snow_layer),
				'W', new ItemStack(Blocks.planks, 0, OreDictionary.WILDCARD_VALUE),
				'C', new ItemStack(Blocks.cobblestone)
		};
		this.itemIcon = "TurretMod3:turret_11";

		int i = this.registerNewAmmoType("turret.amtp0tss");
		 this.addAmmo(i, new ItemStack(Items.snowball), 1);
		 this.addAmmo(i, new ItemStack(Blocks.snow, 1), 2);
		 this.addAmmo(i, new ItemStack(Blocks.snow_layer, 1), 4);

		this.healItems.put(new ItemStack(Blocks.mossy_cobblestone), 10);
		this.healItems.put(new ItemStack(Blocks.cobblestone), 5);
		this.healItems.put(new ItemStack(Blocks.log, 1, OreDictionary.WILDCARD_VALUE), 2);
		this.healItems.put(new ItemStack(Blocks.planks, 1, OreDictionary.WILDCARD_VALUE), 1);
	}
}
