/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoArrow;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoBullet;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoCryoCell;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoFireTank;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoFluxCell;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoMinigunShell;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoShotgunShell;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import java.util.UUID;

public final class TurretAssemblyRecipes
{
    public static final UUID TURRET_MK1_CB    = UUID.fromString("21F88959-C157-44E3-815B-DD956B065052");
    public static final UUID TURRET_MK1_SG    = UUID.fromString("870EA4DD-0C1E-44B1-BE91-4DD33FC00EF8");
    public static final UUID TURRET_MK1_CL    = UUID.fromString("6743974B-5552-45F7-9124-FDCF844BB56C");
    public static final UUID TURRET_MK2_RV    = UUID.fromString("1A207F83-26E1-405A-A9A1-4AB6BB1C4C3A");
    public static final UUID TURRET_MK2_MG    = UUID.fromString("7D21F126-56B5-44DB-A511-CFFADC0782F0");
    public static final UUID TURRET_MK3_LR    = UUID.fromString("94676B1E-8279-490C-A3F6-10983566FE3A");
    public static final UUID TURRET_MK3_FT    = UUID.fromString("AFC33ABF-B973-4A91-A86E-24D0A78E58B2");
    public static final UUID ARROW_SNG        = UUID.fromString("1A011825-2E5B-4F17-925E-F734E6A732B9");
    public static final UUID ARROW_MTP        = UUID.fromString("C079D29A-E6E2-4BE8-8478-326BDFEDE08B");
    public static final UUID SGSHELL_SNG      = UUID.fromString("AB37D601-993D-41FE-B698-8AAC99D296EA");
    public static final UUID SGSHELL_MTP      = UUID.fromString("D17EC4A1-BDAA-4C80-B1F5-0C111EC13954");
    public static final UUID BULLET_SNG       = UUID.fromString("9F528407-8134-49CB-8FA8-23CF88E8CE4A");
    public static final UUID BULLET_MTP       = UUID.fromString("2933D4D6-6111-45E5-AD09-09D81CF03DA9");
    public static final UUID CRYOCELL_1_SNG   = UUID.fromString("EBF1AEAA-C4EC-46CA-9B0F-B818FF7D0770");
    public static final UUID CRYOCELL_1_MTP   = UUID.fromString("5CBDE28A-52B2-45EB-B169-4A81F94EC690");
    public static final UUID CRYOCELL_2_SNG   = UUID.fromString("BACB5D75-B408-4D34-AF6C-2F6C4048B82C");
    public static final UUID CRYOCELL_2_MTP   = UUID.fromString("08528F4F-3D3E-4501-A1BE-A508E5C23DC5");
    public static final UUID CRYOCELL_3_SNG   = UUID.fromString("081CA2A5-FB0C-4749-9359-12D680B58FAC");
    public static final UUID CRYOCELL_3_MTP   = UUID.fromString("052D31C1-05AD-45AF-9C36-D380A78F7E87");
    public static final UUID MGSHELL_SNG      = UUID.fromString("EA5B683F-7D84-4BAE-BFC3-35F2EA48AB2B");
    public static final UUID MGSHELL_MTP      = UUID.fromString("C69B50D8-EB88-4CFC-BF9E-792C75924C22");
    public static final UUID FLUXCELL_SNG     = UUID.fromString("78BA8E56-B161-49A0-8053-710083A39133");
    public static final UUID FLUXCELL_MTP     = UUID.fromString("8B4E5B02-A833-49BF-9E7A-3DF5676E3218");
    public static final UUID FUELTANK_SNG     = UUID.fromString("3F1B7AE0-CEDD-46D0-B0F0-289446560F21");
    public static final UUID FUELTANK_MTP     = UUID.fromString("85F480FD-299B-45CE-9796-C3ECC8EF5868");
    public static final UUID TCU              = UUID.fromString("47B68BE0-30D6-4849-B995-74C147C8CC5D");
    public static final UUID TINFO            = UUID.fromString("5A8C8AE3-878A-4580-9F84-2C8602B4275D");
    public static final UUID HEAL_MK1         = UUID.fromString("816758D6-7F00-4ACB-BD94-F7A8A0F86016");
    public static final UUID HEAL_MK2         = UUID.fromString("39A1A9C8-CECA-40CA-BCF7-ABD2B1A26C82");
    public static final UUID HEAL_MK3         = UUID.fromString("A70314BE-1709-4AE4-8FF7-69F3A69ACCA2");
    public static final UUID HEAL_MK4         = UUID.fromString("6FD0927F-61E0-49A0-B615-4B3E28A63EE4");
    public static final UUID REGEN_MK1        = UUID.fromString("531F0B05-5BB8-45FC-A899-226A3F52D5B7");
    public static final UUID UPG_EMPTY        = UUID.fromString("BC775E0D-7732-4E4E-8FA3-33B299CAF19D");
    public static final UUID UPG_HEALTH_MK1   = UUID.fromString("EF5192F1-0422-444D-B2D3-98540D962AE9");
    public static final UUID UPG_HEALTH_MK2   = UUID.fromString("185AB41E-BD30-47C8-BD42-A9D5C8749ECF");
    public static final UUID UPG_HEALTH_MK3   = UUID.fromString("AF3E8F87-DD88-4665-B3A9-314D4077CD00");
    public static final UUID UPG_HEALTH_MK4   = UUID.fromString("E14C4925-0C35-44CE-9626-490A7774A9FD");
    public static final UUID UPG_STORAGE_1    = UUID.fromString("FAF60679-5BC6-4B82-BB34-323988B56FFA");
    public static final UUID UPG_STORAGE_2    = UUID.fromString("2F90D6BC-0869-45DE-9527-396CCE547ECE");
    public static final UUID UPG_STORAGE_3    = UUID.fromString("9B7BA1F9-286E-43E4-8147-891DA0C243DC");
    public static final UUID UPG_RELOAD_1     = UUID.fromString("A891752D-AA2E-40D1-8E22-50DF0AF43490");
    public static final UUID UPG_RELOAD_2     = UUID.fromString("72BDED08-78DC-4A25-9460-6F5B8AEEE3A5");
    public static final UUID UPG_AMMO_STG     = UUID.fromString("56546F99-5612-4052-9A77-B81A6F1EB5DF");
    public static final UUID UPG_SMART_TGT    = UUID.fromString("A4750C8C-A0A0-4E73-8378-59345124A1FA");
    public static final UUID UPG_ECONOMY_I    = UUID.fromString("D8144A93-870F-4FBC-88F3-94CF8212EF84");
    public static final UUID UPG_ECONOMY_II   = UUID.fromString("1BDE1C44-9165-4BBC-A79D-2003CCE969D9");
    public static final UUID UPG_ECONOMY_INF  = UUID.fromString("7C5E0A1F-1BC3-4F72-A4A0-BB28A595CA0D");
    public static final UUID UPG_ENDER_MEDIUM = UUID.fromString("C6D6FA9C-9B3A-4DDD-B0D8-92CF5C2555F8");
    public static final UUID UPG_FUEL_PURIFY  = UUID.fromString("042E0949-E137-4646-A227-0620D2FB6A4D");
    public static final UUID UPG_AT_AUTO      = UUID.fromString("40EEE46D-835D-42F8-8005-764A00C90365");
    public static final UUID UPG_AT_FILTER    = UUID.fromString("BD48EB98-94A2-4516-90E0-4DC20E843490");
    public static final UUID UPG_AT_SPEED     = UUID.fromString("DF388B34-64ED-4D94-BEE0-C1A4AAB8E701");

    public static void initialize(ITurretAssemblyRegistry registry) {
        IRecipeGroup miscGroup = registry.registerGroup("group0", new ItemStack(ItemRegistry.turret_control_unit));
        IRecipeGroup turretGroup = registry.registerGroup("group1", ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretCrossbow.class)));
        IRecipeGroup ammoGroup = registry.registerGroup("group2", ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.QUIVER_UUID)));
        IRecipeGroup repKitGroup = registry.registerGroup("group3", ItemRegistry.repair_kit.getRepKitItem(1, RepairKitRegistry.INSTANCE.getType(RepairKits.STANDARD_MK1)));
        IRecipeGroup upgradeGroup = registry.registerGroup("group4", UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY));

        ItemStack res;
        RecipeEntryItem[] ingredients;

        //region misc
        {
            // TURRET INFO
            res = new ItemStack(ItemRegistry.turret_info, 1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(Items.BOOK)};
            registry.registerRecipe(TurretAssemblyRecipes.TINFO, miscGroup, res, 7_500, 10, ingredients);

            // TURRET CONTROL UNIT
            res = new ItemStack(ItemRegistry.turret_control_unit, 1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(5).put("ingotIron"),
                                                 new RecipeEntryItem(2).put(Items.REDSTONE),
                                                 new RecipeEntryItem(1).put("paneGlass")};
            registry.registerRecipe(TurretAssemblyRecipes.TCU, miscGroup, res, 10, 180, ingredients);
        }
        //endregion

        //region turrets
        {
            // T1 - CROSSBOW
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretCrossbow.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(12).put("cobblestone"),
                                                 new RecipeEntryItem(1).put(Items.BOW),
                                                 new RecipeEntryItem(4).put("dustRedstone"),
                                                 new RecipeEntryItem(4).put("plankWood")};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK1_CB, turretGroup, res, 10, 100, ingredients);

            // T1 - SHOTGUN
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretShotgun.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(12).put("stone"),
                                                 new RecipeEntryItem(2).put("ingotIron"),
                                                 new RecipeEntryItem(4).put("dustRedstone"),
                                                 new RecipeEntryItem(1).put("logWood")};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK1_SG, turretGroup, res, 10, 100, ingredients);

            // TS - CRYOLATOR
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretCryolator.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(12).put(Blocks.SNOW),
                                                 new RecipeEntryItem(1).put(Items.BOW),
                                                 new RecipeEntryItem(4).put("dustRedstone"),
                                                 new RecipeEntryItem(4).put("plankWood"),
                                                 new RecipeEntryItem(2).put(Blocks.ICE)};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK1_CL, turretGroup, res, 10, 100, ingredients);

            // T2 - REVOLVER
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretRevolver.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("blockIron"),
                                                 new RecipeEntryItem(1).put("ingotGold"),
                                                 new RecipeEntryItem(4).put("dustRedstone"),
                                                 new RecipeEntryItem(2).put(Blocks.STONEBRICK),
                                                 new RecipeEntryItem(4).put(new ItemStack(Blocks.STONE_SLAB, 1, 0))};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK2_RV, turretGroup, res, 15, 100, ingredients);

            // T2 - MINIGUN
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretMinigun.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("blockIron"),
                                                 new RecipeEntryItem(4).put("ingotGold"),
                                                 new RecipeEntryItem(4).put("dustRedstone"),
                                                 new RecipeEntryItem(2).put(Blocks.STONEBRICK),
                                                 new RecipeEntryItem(4).put(new ItemStack(Blocks.STONE_SLAB, 1, 0))};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK2_MG, turretGroup, res, 15, 100, ingredients);

            // T3 - LASER
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretLaser.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put("obsidian"),
                                                 new RecipeEntryItem(1).put("blockRedstone"),
                                                 new RecipeEntryItem(1).put("blockIron"),
                                                 new RecipeEntryItem(2).put("ingotGold"),
                                                 new RecipeEntryItem(1).put("gemDiamond")};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK3_LR, turretGroup, res, 20, 100, ingredients);

            // T3 - FLAMETHROWER
            res = ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretFlamethrower.class));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put("obsidian"),
                                                 new RecipeEntryItem(1).put("blockRedstone"),
                                                 new RecipeEntryItem(1).put("blockIron"),
                                                 new RecipeEntryItem(4).put("ingotGold"),
                                                 new RecipeEntryItem(1).put("blockQuartz")};
            registry.registerRecipe(TurretAssemblyRecipes.TURRET_MK3_FT, turretGroup, res, 20, 100, ingredients);
        }
        //endregion

        //region ammo
        {
            // ARROWS
            res = ItemRegistry.turret_ammo.getAmmoItem(4, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(Items.ARROW)};
            registry.registerRecipe(TurretAssemblyRecipes.ARROW_SNG, ammoGroup, res, 5, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.QUIVER_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.ARROW_MTP, ammoGroup, res, 5, 120, ingredients);

            // SHOTGUN SHELLS
            res = ItemRegistry.turret_ammo.getAmmoItem(12, AmmoRegistry.INSTANCE.getType(TurretAmmoShotgunShell.SHELL_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put("ingotIron"),
                                                 new RecipeEntryItem(1).put(Blocks.GRAVEL),
                                                 new RecipeEntryItem(1).put(Items.GUNPOWDER).put("dustGunpowder"),
                                                 new RecipeEntryItem(1).put("dustRedstone")};
            registry.registerRecipe(TurretAssemblyRecipes.SGSHELL_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoShotgunShell.PACK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoShotgunShell.SHELL_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.SGSHELL_MTP, ammoGroup, res, 5, 120, ingredients);

            // REVOLVER BULLETS
            res = ItemRegistry.turret_ammo.getAmmoItem(6, AmmoRegistry.INSTANCE.getType(TurretAmmoBullet.BULLET_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(3).put("ingotIron"),
                                                 new RecipeEntryItem(1).put(Items.GUNPOWDER).put("dustGunpowder"),
                                                 new RecipeEntryItem(1).put("dustRedstone")};
            registry.registerRecipe(TurretAssemblyRecipes.BULLET_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoBullet.PACK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoBullet.BULLET_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.BULLET_MTP, ammoGroup, res, 5, 120, ingredients);

            // CRYO CELLS
            res = ItemRegistry.turret_ammo.getAmmoItem(4, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK1_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("blockGlass"),
                                                 new RecipeEntryItem(1).put(Blocks.SNOW),
                                                 new RecipeEntryItem(1).put(Items.GUNPOWDER).put("dustGunpowder"),
                                                 new RecipeEntryItem(1).put("dustRedstone")};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_1_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.PACK_MK1_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK1_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_1_MTP, ammoGroup, res, 5, 120, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK2_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK1_UUID))),
                                                 new RecipeEntryItem(1).put(Blocks.SNOW)};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_2_SNG, ammoGroup, res, 5, 40, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.PACK_MK2_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK2_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_2_MTP, ammoGroup, res, 5, 120, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK3_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK2_UUID))),
                                                 new RecipeEntryItem(1).put(Blocks.SNOW)};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_3_SNG, ammoGroup, res, 5, 40, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.PACK_MK3_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoCryoCell.CELL_MK3_UUID))),
                                                 new RecipeEntryItem(1).put(Items.LEATHER)};
            registry.registerRecipe(TurretAssemblyRecipes.CRYOCELL_3_MTP, ammoGroup, res, 5, 120, ingredients);

            // MINIGUN SHELLS
            res = ItemRegistry.turret_ammo.getAmmoItem(2, AmmoRegistry.INSTANCE.getType(TurretAmmoMinigunShell.SHELL_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("ingotIron"),
                                                 new RecipeEntryItem(2).put("seed*").put(Items.WHEAT_SEEDS).put(Items.MELON_SEEDS).put(Items.PUMPKIN_SEEDS),
                                                 new RecipeEntryItem(1).put(Items.GUNPOWDER).put("dustGunpowder"),
                                                 new RecipeEntryItem(1).put("dustRedstone")};
            registry.registerRecipe(TurretAssemblyRecipes.MGSHELL_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoMinigunShell.PACK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoMinigunShell.SHELL_UUID))),
                                                 new RecipeEntryItem(1).put(Items.IRON_INGOT)};
            registry.registerRecipe(TurretAssemblyRecipes.MGSHELL_MTP, ammoGroup, res, 5, 120, ingredients);

            // FLUX CELLS
            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFluxCell.CELL_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("blockGlass"),
                                                 new RecipeEntryItem(1).put("ingotIron"),
                                                 new RecipeEntryItem(1).put("dustRedstone")};
            registry.registerRecipe(TurretAssemblyRecipes.FLUXCELL_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFluxCell.PACK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFluxCell.CELL_UUID))),
                                                 new RecipeEntryItem(1).put(Items.IRON_INGOT)};
            registry.registerRecipe(TurretAssemblyRecipes.FLUXCELL_MTP, ammoGroup, res, 5, 120, ingredients);

            // FUEL TANKS
            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFireTank.TANK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(Items.LAVA_BUCKET),
                                                 new RecipeEntryItem(1).put("dustRedstone"),
                                                 new RecipeEntryItem(1).put(Items.RABBIT_HIDE)};
            registry.registerRecipe(TurretAssemblyRecipes.FUELTANK_SNG, ammoGroup, res, 10, 60, ingredients);

            res = ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFireTank.PACK_UUID));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(16).put(ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoFireTank.TANK_UUID))),
                                                 new RecipeEntryItem(1).put(Items.IRON_INGOT)};
            registry.registerRecipe(TurretAssemblyRecipes.FUELTANK_MTP, ammoGroup, res, 5, 120, ingredients);
        }
        //endregion

        //region repairkits
        {
            //noinspection ConstantConditions
            ItemStack healingPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionType.getPotionTypeForName("healing"));
            //noinspection ConstantConditions
            ItemStack regenPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionType.getPotionTypeForName("regeneration"));

            res = ItemRegistry.repair_kit.getRepKitItem(3, RepairKits.STANDARD_MK1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(Items.LEATHER),
                                                 new RecipeEntryItem(1).put(healingPotion.copy()).drawTooltip()};
            registry.registerRecipe(TurretAssemblyRecipes.HEAL_MK1, repKitGroup, res, 25, 600, ingredients);

            res = ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK2);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(Items.LEATHER),
                                                 new RecipeEntryItem(1).put(healingPotion.copy()).drawTooltip(),
                                                 new RecipeEntryItem(1).put(ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK1))};
            registry.registerRecipe(TurretAssemblyRecipes.HEAL_MK2, repKitGroup, res, 25, 600, ingredients);

            res = ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK3);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(Items.LEATHER),
                                                 new RecipeEntryItem(1).put(healingPotion.copy()).drawTooltip(),
                                                 new RecipeEntryItem(1).put(ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK2))};
            registry.registerRecipe(TurretAssemblyRecipes.HEAL_MK3, repKitGroup, res, 25, 600, ingredients);

            res = ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK4);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(Items.LEATHER),
                                                 new RecipeEntryItem(1).put(healingPotion.copy()).drawTooltip(),
                                                 new RecipeEntryItem(1).put(ItemRegistry.repair_kit.getRepKitItem(1, RepairKits.STANDARD_MK3))};
            registry.registerRecipe(TurretAssemblyRecipes.HEAL_MK4, repKitGroup, res, 25, 600, ingredients);

            res = ItemRegistry.repair_kit.getRepKitItem(6, RepairKits.REGEN_MK1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(2).put(Items.LEATHER),
                                                 new RecipeEntryItem(1).put(regenPotion.copy()).drawTooltip()};
            registry.registerRecipe(TurretAssemblyRecipes.REGEN_MK1, repKitGroup, res, 25, 600, ingredients);
        }
        //endregion

        //region upgrades
        {
            res = new ItemStack(ItemRegistry.assembly_upg_auto, 1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("gemEmerald"),
                                                 new RecipeEntryItem(1).put("nuggetGold"),
                                                 new RecipeEntryItem(1).put(Items.COMPARATOR)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_AT_AUTO, upgradeGroup, res, 60, 300, ingredients);

            res = new ItemStack(ItemRegistry.assembly_upg_filter, 1);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("gemEmerald"),
                                                 new RecipeEntryItem(1).put("nuggetGold"),
                                                 new RecipeEntryItem(1).put("dustRedstone"),
                                                 new RecipeEntryItem(1).put(Blocks.HOPPER)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_AT_FILTER, upgradeGroup, res, 60, 300, ingredients);

            res = new ItemStack(ItemRegistry.assembly_upg_speed, 1);
            //noinspection ConstantConditions
            ItemStack speedPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionType.getPotionTypeForName("swiftness"));
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("gemEmerald"),
                                                 new RecipeEntryItem(1).put("nuggetGold"),
                                                 new RecipeEntryItem(1).put("dustRedstone"),
                                                 new RecipeEntryItem(1).put(speedPotion).drawTooltip()};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_AT_SPEED, upgradeGroup, res, 60, 300, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY);
            res.stackSize = 3;
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put("paneGlass"),
                                                 new RecipeEntryItem(2).put("dustRedstone"),
                                                 new RecipeEntryItem(1).put("ingotGold"),
                                                 new RecipeEntryItem(1).put(new ItemStack(Blocks.STONE_SLAB, 1, 0))};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_EMPTY, upgradeGroup, res, 80, 400, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.HEALTH_I);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(8).put(Items.SPECKLED_MELON)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_HEALTH_MK1, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.HEALTH_II);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(8).put(Items.SPECKLED_MELON),
                                                 new RecipeEntryItem(1).put(Items.GOLDEN_APPLE)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_HEALTH_MK2, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.HEALTH_III);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(16).put(Items.SPECKLED_MELON),
                                                 new RecipeEntryItem(2).put(Items.GOLDEN_APPLE)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_HEALTH_MK3, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.HEALTH_IV);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(16).put(Items.SPECKLED_MELON),
                                                 new RecipeEntryItem(4).put(Items.GOLDEN_APPLE)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_HEALTH_MK4, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.UPG_STORAGE_I);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.TRAPPED_CHEST)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_STORAGE_1, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.UPG_STORAGE_II);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.TRAPPED_CHEST),
                                                 new RecipeEntryItem(1).put(Items.GOLD_INGOT)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_STORAGE_2, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.UPG_STORAGE_III);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.TRAPPED_CHEST),
                                                 new RecipeEntryItem(1).put(Items.DIAMOND)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_STORAGE_3, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.RELOAD_I);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.ICE)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_RELOAD_1, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.RELOAD_II);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.PACKED_ICE)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_RELOAD_2, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.AMMO_STORAGE);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.HOPPER)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_AMMO_STG, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.SMART_TGT);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Items.SPIDER_EYE),
                                                 new RecipeEntryItem(1).put(Items.ENDER_PEARL)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_SMART_TGT, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.ECONOMY_I);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Items.EMERALD)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_ECONOMY_I, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.ECONOMY_II);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.GOLD_BLOCK)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_ECONOMY_II, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.ECONOMY_INF);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(new RecipeEntryItem.EnchantmentEntry(new ItemStack(Items.BOW), Enchantments.INFINITY)).drawTooltip()};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_ECONOMY_INF, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.ENDER_MEDIUM);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(4).put(Items.ENDER_PEARL)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_ENDER_MEDIUM, upgradeGroup, res, 20, 600, ingredients);

            res = UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.FUEL_PURIFY);
            ingredients = new RecipeEntryItem[] {new RecipeEntryItem(1).put(UpgradeRegistry.INSTANCE.getUpgradeItem(UpgradeRegistry.EMPTY)).drawTooltip(),
                                                 new RecipeEntryItem(1).put(Blocks.LAPIS_BLOCK),
                                                 new RecipeEntryItem(2).put(Items.MAGMA_CREAM)};
            registry.registerRecipe(TurretAssemblyRecipes.UPG_FUEL_PURIFY, upgradeGroup, res, 20, 600, ingredients);
        }
        //endregion
    }
}
