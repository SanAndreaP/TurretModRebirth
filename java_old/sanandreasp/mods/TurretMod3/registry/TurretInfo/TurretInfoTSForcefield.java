package sanandreasp.mods.TurretMod3.registry.TurretInfo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TurretInfoTSForcefield extends TurretInfo {

	public TurretInfoTSForcefield() {
		this.maxAmmo = 0;
		this.maxHealth = 40;
		this.maxEXP = 0;
		this.damage = 0;
		this.lowerRangeY = 0F;
		this.upperRangeY = 16.5F;
		this.rangeX = 16.5F;
		this.desc = "turret.desctsf";
		this.crafting = new Object[] {
				"P P", "OEO", "WWW",
				'P', new ItemStack(Items.ender_eye),
				'E', new ItemStack(Blocks.redstone_block),
				'O', new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE),
				'W', new ItemStack(Blocks.obsidian)
		};
		this.itemIcon = "TurretMod3:turret_13";

		this.healItems.put(new ItemStack(Items.redstone), 1);
		this.healItems.put(new ItemStack(Blocks.redstone_block), 9);
		this.healItems.put(new ItemStack(Blocks.obsidian), 20);
		this.healItems.put(new ItemStack(Items.quartz), 2);
		this.healItems.put(new ItemStack(Blocks.quartz_block), 18);
	}
}
