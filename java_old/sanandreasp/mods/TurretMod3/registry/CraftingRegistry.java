package sanandreasp.mods.TurretMod3.registry;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;

public final class CraftingRegistry {
	public static void initCraftings() {
		// Turret Items
		for (int i = 0; i < TurretInfo.getTurretCount(); i++) {
			TurretInfo tinf = TurretInfo.getTurretInfo(TurretInfo.getTurretClass(i));
			GameRegistry.addRecipe(tinf.getTurretItem(), tinf.getCrafting());
		}

		// laptops
		for (int i = 0; i < 2; i++) {
			GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.laptop, 1, i*8),
					"  #", "QRT", "###",
					'#', i == 0 ? new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE) : new ItemStack(Blocks.obsidian),
					'Q', new ItemStack(Items.quartz),
					'R', new ItemStack(Items.redstone),
					'T', new ItemStack(Blocks.redstone_torch)
			);
		}

		// Arrow Pack
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 0),
				"###", "#+#", "###",
				'#', new ItemStack(Items.arrow),
				'+', new ItemStack(Items.leather)
		);

		// Ender Pearl Bundle
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 8),
				"###", "#+#", "###",
				'#', new ItemStack(Items.ender_pearl),
				'+', new ItemStack(Items.string)
		);

		// Glistering Melon Bundle
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 7),
				"###", "###", "###",
				'#', new ItemStack(Items.speckled_melon)
		);

		// Bullets
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 2, 3),
				new ItemStack(Items.iron_ingot),
				new ItemStack(Items.gunpowder)
		);

		// Bullet Pack
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 4),
				"###", "#+#", "###",
				'#', new ItemStack(TM3ModRegistry.ammoItems, 1, 3),
				'+', new ItemStack(Items.gold_nugget)
		);

		// Fuel Tank
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 5),
				new ItemStack(Blocks.nether_brick),
				new ItemStack(Items.lava_bucket)
		);

		// Fuel Tank Pack
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 6),
				"###", "#+#", "###",
				'#', new ItemStack(TM3ModRegistry.ammoItems, 1, 5),
				'+', new ItemStack(Items.string)
		);

		// Rockets
		ItemStack[] is = new ItemStack[] {new ItemStack(Items.coal), new ItemStack(Items.iron_ingot), new ItemStack(Items.gold_ingot)};
		for (int i = 0; i < 3; i++) {
			GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.rocket, 2, i),
				" T ", "IGI",
				'T', is[i],
				'I', new ItemStack(Items.iron_ingot),
				'G', new ItemStack(Items.gunpowder)
			);
			GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.rocket, 2, i+3),
					" T ", "IGI", " E ",
					'T', is[i],
					'I', new ItemStack(Items.iron_ingot),
					'G', new ItemStack(Items.gunpowder),
					'E', new ItemStack(Items.ender_pearl)
			);
		}

		// Artillery Shells
		  // Art. Shell
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 0),
				" + ", "+F+", " + ",
				'+', new ItemStack(Items.gunpowder),
				'F', new ItemStack(Items.fire_charge)
		);
		  // Big Art. Shell
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 1),
				"+++", "+F+", "+++",
				'+', new ItemStack(Items.gunpowder),
				'F', new ItemStack(Items.fire_charge)
		);
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 1),
				" + ", "+F+", " + ",
				'+', new ItemStack(Items.gunpowder),
				'F', new ItemStack(TM3ModRegistry.artilleryBall, 1, 0)
		);
		  // Napalm Art. Shell
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 2),
				new ItemStack(TM3ModRegistry.artilleryBall, 1, 0),
				new ItemStack(Items.blaze_powder)
		);
		  // Big Napalm Art. Shell
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 3),
				new ItemStack(TM3ModRegistry.artilleryBall, 1, 1),
				new ItemStack(Items.blaze_powder)
		);
		  // Frag Art. Shell
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 4),
				new ItemStack(TM3ModRegistry.artilleryBall, 1, 0),
				new ItemStack(Items.glowstone_dust)
		);
		  // Griefing Art. Shells
		for (int i = 0; i < 5; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.artilleryBall, 1, 5+i),
					new ItemStack(TM3ModRegistry.artilleryBall, 1, i),
					new ItemStack(Items.magma_cream)
			);
		}

		// Turret Info Book
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.tInfoBook, 1),
				new ItemStack(Blocks.dispenser),
				new ItemStack(Items.book),
				new ItemStack(Items.dye, 1, 0)
		);

		// TCU
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.tcu, 1),
				" | ", "RBR", "III",
				'|', new ItemStack(Items.stick),
				'R', new ItemStack(Items.redstone),
				'B', new ItemStack(Items.gold_ingot),
				'I', new ItemStack(Items.iron_ingot)
		);

		// HT Turret Module
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.httm, 1),
				"RDR", "GGG", "  E",
				'R', new ItemStack(Items.redstone),
				'D', new ItemStack(Items.diamond),
				'G', new ItemStack(Items.gold_ingot),
				'E', new ItemStack(Items.ender_pearl)
		);

		// Mobile Base
		GameRegistry.addRecipe(new ItemStack(TM3ModRegistry.mobileBase, 1),
				"IBI", "B B", "IBI",
				'I', new ItemStack(Items.stick),
				'B', new ItemStack(Blocks.stone)
		);

		// Pebble -> Pebbles
		Object[] objArr = new ItemStack[5];
		for (int i = 0; i < 5; i++) objArr[i] = new ItemStack(TM3ModRegistry.ammoItems, 1, 2);
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 1, 1), objArr);

		// Pebbles -> Gravel
		objArr = new ItemStack[8];
		for (int i = 0; i < 8; i++) objArr[i] = new ItemStack(TM3ModRegistry.ammoItems, 1, 1);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.gravel, 1), objArr);

		// Arrow Pack -> Arrows
		GameRegistry.addShapelessRecipe(new ItemStack(Items.arrow, 8), new ItemStack(TM3ModRegistry.ammoItems, 1, 0));

		// Gravel -> Pebbles
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 8, 1), new ItemStack(Blocks.gravel));

		// Bullet Pack -> Bullets
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 8, 3), new ItemStack(TM3ModRegistry.ammoItems, 1, 4));

		// Tank Pack -> Fuel Tank
		GameRegistry.addShapelessRecipe(new ItemStack(TM3ModRegistry.ammoItems, 8, 5), new ItemStack(TM3ModRegistry.ammoItems, 1, 6));

		// Glister Melon Bundle -> Glister Melon
		GameRegistry.addShapelessRecipe(new ItemStack(Items.speckled_melon, 8), new ItemStack(TM3ModRegistry.ammoItems, 1, 7));

		// Ender Pearl Bundle -> Ender Pearls
		GameRegistry.addShapelessRecipe(new ItemStack(Items.ender_pearl, 8), new ItemStack(TM3ModRegistry.ammoItems, 1, 8));
	}
}
