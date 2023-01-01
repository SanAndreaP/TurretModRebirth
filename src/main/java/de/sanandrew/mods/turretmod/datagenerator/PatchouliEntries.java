package de.sanandrew.mods.turretmod.datagenerator;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class PatchouliEntries
{
    private PatchouliEntries() { }

    //region Turrets
    static void registerTurrets(Consumer<PatchouliBuilder> consumer) {
        PatchouliBuilder.withIcon("Turret Basics", Resources.PATCHOULI_CAT_TURRETS, ItemRegistry.TURRET_LEXICON.getRegistryName()).priority(true)
                .page(new PatchouliPages.Text("Once you craft a turret, you can then place it on a solid surface (or in water in case of buoyant turrets).$(br)Turrets are stationary, but can be pushed by pistons or moved in a minecart.$(br2)They have a certain amount of health and if damaged, can be repaired either with instant health potions or with $(l:misc/repair_kits)repair kits$(/l)."))
                .page(new PatchouliPages.Text("Turrets need ammo to operate (which types of ammo a turret can accept is shown within their respective lexicon entry).$(br)To give them ammo, right-click it with a compatible ammo item (or a non-empty $(l:ammo/ammo_ammo_cartridge)ammo cartridge$(/l)).$(br2)Both amounts of ammo and health can be seen on the turret base, where you can see two \"antennas\", a red one for health and a blue one for ammo."))
                .page(new PatchouliPages.Text("If they're upright, the turret is full with health/ammo and will appear to \"fall down\" once the amount of ammo/health decreases.$(br2)By default a turret only targets hostile creatures. Be careful, hostiles automatically try to defend themselves from turrets and attempt to damage them.$(br2)Right-clicking a turret with an empty hand will show an info page listing the upgrades and turret values."))
                .page(new PatchouliPages.Text("Here you can also (de-)activate it and toggle range visibility.$(br2)Turrets can be fitted with upgrades to further enhance their capabilities. To upgrade one, just right-click it with the upgrade in hand.$(br2)For further configuration, like custom targeting settings, dismantling the turret, etc., you need the $(l:misc/tcu)Turret Control Unit$(/l)."))
                .build(consumer);

        PatchouliBuilder.withIcon("Crossbow Turret", Resources.PATCHOULI_CAT_TURRETS, Turrets.CROSSBOW.getId())
                .page(new PatchouliPages.TurretInfo(Turrets.CROSSBOW.getId())
                              .title("Crossbow Turret")
                              .text("Meant for basic defense and beginners. Not very strong but one of the cheapest to make.")
                              .scale(1.1F))
                .page(new PatchouliPages.TurretRange(Turrets.CROSSBOW.getId())
                              .text("I was an adventurer once..."))
                .page(new PatchouliPages.AssemblyRecipe(new ResourceLocation(TmrConstants.ID, "assembly/turrets_crossbow_turret")))
                .build(consumer);
    }
    //endregion

    //region Ammo
    static void registerAmmo(Consumer<PatchouliBuilder> consumer) {
        PatchouliBuilder.withIcon("Ammunition Cartridges", Resources.PATCHOULI_CAT_AMMO, ItemRegistry.AMMO_CARTRIDGE.getRegistryName()).priority(true)
                        .page(new PatchouliPages.Spotlight(ItemRegistry.AMMO_CARTRIDGE)
                                      .title("Ammunition Cartridges")
                                      .text("Carry more rounds with the ammo cartridge! Use $(bold)$(k:use)$() to open a chest-like UI and have access to its inventory. Do that whilst pointing towards a turret, the cartridge will attempt to fill that turret up, as long as it can accept the type of ammo.$(br2)$(italic)More important notes on the next pages!$()"))
                        .page(new PatchouliPages.AssemblyRecipe(new ResourceLocation(TmrConstants.ID, "assembly/ammo_ammo_cartridge"))
                                      .text("*slaps cartridge* This boy can fit so many rounds in it"))
                        .page(new PatchouliPages.Text("$(bold)Note #1$(): A cartridge can only hold one type of ammo! Once you put crossbow bolts in it, for example, it'll no longer accept any other kind, like bullets, until it is empty once again."))
                        .page(new PatchouliPages.Text("$(bold)Note #2$(): If you have a cartridge in your inventory that isn't full and you pick up ammo, the cartridge will attempt to store it first thing before it goes into your inventory, given it can fit inside the cartridge (see $(italic)Note #1$())."))
                        .build(consumer);

        registerBolts(consumer);
    }

    private static void registerBolts(Consumer<PatchouliBuilder> consumer) {
        ItemStack[]     bolt          = s(AmmunitionRegistry.INSTANCE.getItem(Ammunitions.BOLT));
        List<ItemStack> potionBottles = new ArrayList<>();
        List<ItemStack> tippedBolts   = new ArrayList<>();
        ForgeRegistries.POTION_TYPES.forEach(p -> {
            if( p.getEffects().isEmpty() ) {
                return;
            }

            potionBottles.add(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), p));
            tippedBolts.add(AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT, Objects.requireNonNull(p.getRegistryName()).toString(), 8));
        });

        PatchouliBuilder.withIcon("Crossbow Bolts", Resources.PATCHOULI_CAT_AMMO, Ammunitions.BOLT.getId()).sort(1)
                        .page(new PatchouliPages.AmmoInfo("Crossbow Bolts")
                                      .text("Bolts act like arrows, just smaller, to fit into the turret.$(br2)As with regular arrows, these can be tipped with potions.$(br2)$(5)$(o)Small knockback effect")
                                      .type(Ammunitions.BOLT.getId()).type(Ammunitions.TIPPED_BOLT.getId())
                                      .turret("turrets/crossbow_turret", "Crossbow Turret"))
                        .page(new PatchouliPages.AssemblyRecipe(new ResourceLocation(TmrConstants.ID, "assembly/ammo_crossbow_bolt")))
                        .page(new PatchouliPages.CustomCrafting(bolt, bolt, bolt,
                                                                bolt, s(potionBottles), bolt,
                                                                bolt, bolt, bolt,
                                                                s(tippedBolts))
                                      .title("Tipped Crossbow Bolts")
                                      .text("The potions here are just examples. There may be other potions that can be used to tip bolts as well."))
                        .build(consumer);
    }
    //endregion

    //region Upgrades
    public static void registerUpgrades(Consumer<PatchouliBuilder> consumer) {
        newUpgrade(consumer, true, "Upgrade Base",
                   new UpgradeData(UpgradeRegistry.EMPTY_UPGRADE, "Upgrade Base",
                                   "Used as a crafting ingredient for all other upgrades. Cannot be applied to anything, as it would do nothing.",
                                   "empty_upgrade"));
        newUpgrade(consumer, false, "Ammo Storage Upgrade",
                   new UpgradeData(Upgrades.AMMO_STORAGE, "Ammo Storage Upgrade",
                                   "Doubles the amount of rounds a turret can hold by adding an additional storage component.",
                                   "ammo_storage_upgrade"));
        newUpgrade(consumer, false, "Economy Upgrades",
                   new UpgradeData(Upgrades.ECONOMY_I, "Economy I Upgrade",
                                   "A turret fitted with this upgrade has a 15% chance of not consuming ammo by occasionally doubling the item inside the turret container before it is shot.",
                                   "economy_1_upgrade"),
                   new UpgradeData(Upgrades.ECONOMY_II, "Economy II Upgrade",
                                   "This upgrade increases the chance of not consuming ammo by 35% (in total: 50%).$(br2)$(5)$(o)Requires Economy I$()",
                                   "economy_2_upgrade"),
                   new UpgradeData(Upgrades.ECONOMY_INF, "Inf. Economy Upgrade",
                                   "An unlimited ammo supply is guaranteed, given the turret is filled with rounds.$(br)Failing that, the turret resorts to the other 2 Economy upgrades.$(br2)$(5)$(o)Requires Economy II$()",
                                   "infinite_economy_upgrade"));
        newUpgrade(consumer, false, "Ender Toxin Upgrades",
                   new UpgradeData(Upgrades.ENDER_TOXIN_I, "Ender Toxin I Upgrade",
                                   "Allows damaging ender-based creatures like Endermen by augmenting the projectiles themselves with a sticky fluid. It also prevents Endermen and Shulkers from teleporting away when hit.",
                                   "ender_toxin_1_upgrade"),
                   new UpgradeData(Upgrades.ENDER_TOXIN_II, "Ender Toxin II Upgrade",
                                   "The turret will be able to damage huge ender-based creatures like the Ender Dragon due to further projectile augmentation with explosives.$(br)Coincidentally, the Wither also seems to be affected by this...$(br2)$(5)$(o)Requires Ender Toxin I$()",
                                   "ender_toxin_2_upgrade"));
        newUpgrade(consumer, false, "Health Upgrades",
                   new UpgradeData(Upgrades.HEALTH_I, "Health Upgrades",
                                   "Strengthens the structure of the turret chassis to gain 25% additional maximum health with each tier.$(br2)$(o)Note: These types of upgrades will not increase the turrets health automatically! You must heal it afterwards.$()")
                           .recipe("health_1_upgrade", "Health I Upgrade")
                           .recipe("health_2_upgrade", "Health II Upgrade", "$(5)$(italic)Requires Health I$()")
                           .recipe("health_3_upgrade", "Health III Upgrade", "$(5)$(italic)Requires Health II$(br)$(3)Only applicable to tiers 1-4$()")
                           .recipe("health_4_upgrade", "Health IV Upgrade", "$(5)$(italic)Requires Health III$(br)$(3)Only applicable to tiers 1-3$()"));
        newUpgrade(consumer, false, "Leveling Upgrade",
                   new UpgradeData(Upgrades.LEVELING, "Leveling Upgrade",
                                   "Level up your turrets with this. Once a turret kills an enemy, it'll gather the resulting XP as if the target would've been killed by a player.$(br2)Adds an additional tab to the $(l:misc/tcu)TCU$() UI for managing this upgrade (described in the upcoming pages).",
                                   "leveling_upgrade")
                           .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/tab_leveling.png").title("TCU Tab").text("To manage this upgrade, the TCU is provided with an additional tab."))
                           .page(new PatchouliPages.Text("The top progress bar represents the amount of XP until the next level-up and the current turret level.$(br2)Below is the total amount of XP and - in case the maximum level is reached - the excess XP inside brackets.$(br2)After that is a list of Modifiers gained by leveling up (more on that on the upcoming pages).$(br2)Last, but not least, there is a button to retrieve excess XP."))
                           .page(new PatchouliPages.Text("Once a turret reaches a certain level, it may gain additional modifiers to its stats. On the right you can see a list of all possible level-ups.$(br2)Modifiers are additive, meaning if, for example, on level 10 a turret gains +10% health and on level 20 +15% health, the total health modifier will be +25%.")));
    }

    private static void newUpgrade(Consumer<PatchouliBuilder> consumer, boolean priority, String title, UpgradeData... data) {
        PatchouliBuilder b = PatchouliBuilder.withIcon(title, Resources.PATCHOULI_CAT_UPGRADES, data[0].upg.getId()).priority(priority);
        for( UpgradeData d : data ) {
            b.page(new PatchouliPages.Spotlight(UpgradeRegistry.INSTANCE.getItem(d.upg))
                           .title(d.title)
                           .text(d.text));
            d.additPages.forEach(b::page);
        }
        b.build(consumer);
    }

    private static final class UpgradeData
    {
        IUpgrade upg;
        String title;
        String text;
        final List<PatchouliBuilder.Page<?>> additPages = new ArrayList<>();

        UpgradeData(IUpgrade upg, String title, String text, String... recipe) {
            this.upg = upg;
            this.title = title;
            this.text = text;
            Arrays.stream(recipe).forEach(r -> this.recipe(r, "", ""));
        }

        UpgradeData recipe(String recipe, String title) {
            return recipe(recipe, title, "");
        }

        UpgradeData recipe(String recipe, String title, String text) {
            PatchouliPages.AssemblyRecipe p = new PatchouliPages.AssemblyRecipe(new ResourceLocation(TmrConstants.ID, String.format("assembly/upgrades_%s", recipe)));
            if( !Strings.isNullOrEmpty(title) ) {
                p.title(title);
            }
            if( !Strings.isNullOrEmpty(text) ) {
                p.text(text);
            }
            this.additPages.add(p);

            return this;
        }

        UpgradeData page(PatchouliBuilder.Page<?> page) {
            this.additPages.add(page);

            return this;
        }
    }
    //endregion

    //region Misc
    public static void registerMisc(Consumer<PatchouliBuilder> consumer) {
        PatchouliBuilder.withIcon("Turret Control Unit", Resources.PATCHOULI_CAT_MISC, ItemRegistry.TURRET_CONTROL_UNIT.getRegistryName())
                .page(new PatchouliPages.Spotlight(ItemRegistry.TURRET_CONTROL_UNIT)
                              .text("An essential item to control any aspect of a turret.$(br)Pointing at a turret whilst holding this will show you some basic stats of the turret.$(br2)Right-click a turret with it will show you an UI for turret configuration.$(br)Certain upgrades may add pages, explained in the respective upgrade entry."))
                .page(new PatchouliPages.AssemblyRecipe(new ResourceLocation(TmrConstants.ID, "assembly/misc_turret_control_unit"))
                              .text("The next pages explain the tab pages of the UI"))
                .page(new PatchouliPages.Spotlight(Items.BOOK).title("Turret Info")
                              .text("This tab will show you general information (a POV camera, turret name (editable), etc.). $(italic)Hovering over the icon on the left shows you what it is.$()$(br2)At the bottom are control buttons to dismantle the turret (requires a $(l:misc/turret_crate)Turret Crate$(/l)),$(br)de-/activate it as well as toggle a display showing its range."))
                .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/tab_info.png")
                              .text("$(italic)An example of the info page for a $(l:turrets/crossbow_turret)Crossbow Turret$(/l)$()"))
                .page(new PatchouliPages.Spotlight(Items.ZOMBIE_HEAD).title("Entity Target Settings")
                              .text("Here you can configure which types of entities the turret can target by either individually selecting entries in the list or selecting groups of entities (all or none; monsters, animals, other)$(br2)It also features a Blacklist/Whitelist mode. The default is a whitelist."))
                .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/tab_tgt_creatures.png")
                              .text("$(italic)The target page with monsters selected, in 'allow list' mode$()"))
                .page(new PatchouliPages.Spotlight(Items.PLAYER_HEAD).title("Player Target Settings")
                              .text("Just like in the Entity Target Settings tab, you can configure which players can be targeted by the turret, including yourself!$(br2)It also features a Blacklist/Whitelist mode. The default is a whitelist."))
                .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/tab_tgt_players.png")
                              .text("$(italic)The target page with no players selected, in 'allow list' mode$()"))
                .page(new PatchouliPages.Spotlight(UpgradeRegistry.INSTANCE.getItem(UpgradeRegistry.EMPTY_UPGRADE)).title("Upgrades")
                              .text("Add or remove upgrades with ease here, similar to a chest.$(br2)Greyed out slots are locked and you'll need the $(l:upgrades/upgrade_storage_upgrade)Upgrade Storage upgrades$() in order to unlock them."))
                .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/tab_upgrades.png")
                              .text("$(italic)The upgrades page with some upgrades applied$()"))
                .page(new PatchouliPages.Text("The TCU is capable of being bound to a specific turret. To do so you need to right-click a turret with the TCU whilst sneaking. Once that is done, you can access the TCU UI without being close to that turret. The only caveat is that you need $(l:upgrades/remote_access_upgrade)an upgrade$(/l) in order to dismantle, manage upgrades or anything else involving item transfer.$(br)To unbind a bound TCU, just right-click it whilst sneaking, not pointing to a turret.").title("Remote Access"))
                .page(new PatchouliPages.Image("sapturretmod:textures/gui/lexicon/tcu/remote_access_highlight.png").border(true)
                              .text("$(italic)When holding a bound TCU, the turret is highlighted while the TCU is in your hands.$()"))
                .build(consumer);
    }
    //endregion

    private static ItemStack[] s(Item... items) {
        return Arrays.stream(items).map(ItemStack::new).toArray(ItemStack[]::new);
    }

    private static ItemStack[] s(ItemStack... stacks) {
        return stacks;
    }

    private static ItemStack[] s(Collection<ItemStack> stacks) {
        return stacks.toArray(new ItemStack[0]);
    }
}
