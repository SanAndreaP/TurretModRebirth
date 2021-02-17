#CraftTweaker support

##Turret Assembly Table
```zs
import mods.sapturretmod.TurretAssembly
```
In order to manipulate the recipes used by the turret assembly table, you'll need to import this CraftTweaker class first.

###removing
Removing recipes automatically removes a group if that group has no recipes.

#####remove recipes
```zs
removeRecipeByName(String id)
```
removes a recipe with the given ID.

---

```zs
removeRecipeByOutput(IItemStack output)
```
and
```zs
removeRecipeByOutput(IItemStack output, String group)
```
remove any and all recipes by looking it up through an output item. The 2nd command only removes recipes within the specified group.

#####remove all recipes
```zs
removeAllRecipes()
```
and
```zs
removeAllRecipes(String group)
```
remove all turret assembly table recipes, where the 2nd command only removes those within a specified group.

###adding
Adding recipes automatically registers a new group, if that group is not yet available. You're advised to set an icon for the new group, though.

#####add new recipes
```zs
addRecipe(String group, IIngredient[] inputs, IItemStack output, int fluxPerTick, int processTime)
```
and
```zs
addRecipe(String id, String group, IIngredient[] inputs, IItemStack output, int fluxPerTick, int processTime)
```
register a new recipe to a (new) group within the turret assembly table with an array of inputs, the resulting output, how much flux per tick is consumed and the duration (in ticks) of the crafting operation.

With the 2nd command, you can define your own ID for the recipe.

#####setting a group icon
```zs
setGroupIcon(String group, IItemStack item)
```
sets the icon of the group to the specified item. Used by the turret assembly table GUI.

#####setting a group ordinal number
```zs
setGroupOrder(String group, int ordinal)
```
sets the ordinal number of the group used by the turret assembly table GUI.

The sorting for the standard groups is as follows:
0. turrets
1. misc
2. ammo
3. upgrades
4. repair_kit

##Electrolyte Generator
```zs
import mods.sapturretmod.ElectrolyteGenerator;
```
In order to manipulate the fuels used by the electrolyte generator, you'll need to import this CraftTweaker class first.

###removing

#####remove fuel
```zs
removeFuelByName(String id)
```
removes an electrolyte generator recipe with the given ID.

```zs
removeFuelsByInput(IIngredient input)
```
looks up any and all recipes which can match the given input and removes those.

#####remove all fuels
```zs
removeAllFuels()
```
removes all electrolyte generator recipes.

###add new fuels
There are 4 commands for adding electrolyte fuel:

```zs
addFuel(IIngredient input, IItemStack trash, IItemStack treasure, float efficiency, int processingTime)
```
```zs
addFuel(IIngredient input, IItemStack trash, float trashChance, IItemStack treasure, float treasureChance, float efficiency, int processingTime)
```
```zs
addFuel(String id, IIngredient input, IItemStack trash, IItemStack treasure, float efficiency, int processingTime)
```
and
```zs
addFuel(String id, IIngredient input, IItemStack trash, float trashChance, IItemStack treasure, float treasureChance, float efficiency, int processingTime)
```
The 1st and 2nd command create a recipe with an input, a trash and treasure item, an efficiency multiplier and processing time, with the second command also taking the chance of both trash and treasure items.

The 3rd and 4th command is like the 1st and 2nd one, but additionally, you can define your own ID for the recipe.

##Example script
```zs
import mods.sapturretmod.ElectrolyteGenerator;
import mods.sapturretmod.TurretAssembly;

### Electrolyte Generator
# remove the fuel with the ID "sapturretmod:carrot", which is the
# carrot fuel this mod comes with by default
ElectrolyteGenerator.removeFuelByName("sapturretmod:carrot");

# remove any fuel whose input is registered as "cropPotato" within
# the OreDictionary
ElectrolyteGenerator.removeFuelsByInput(<ore:cropPotato>);

# add any item that is registered as "chest" within the
# OreDictionary as fuel with the following parameters:
# - strings as trash item with a 50% chance
# - iron ingots as treasure item with a 30% chance
# - efficiency of 100%
# - 500 ticks (25 seconds) of processing time
ElectrolyteGenerator.addFuel(<ore:chest>, <minecraft:string>, 0.5, <minecraft:iron_ingot>, 0.3, 1.0, 500);
# (also works with just single items, e.g. <minecraft:dirt> instead
#  of <ore:chest> as input)
ElectrolyteGenerator.addFuel(<minecraft:dirt>, <minecraft:string>, 0.5, <minecraft:iron_ingot>, 0.3, 1.0, 500);

### Turret Assembly Table
# remove all recipes belonging to the "repair_kit" group,
# subsequently removing the group itself (except if you add new
# items to it)
TurretAssembly.removeAllRecipes("repair_kit");

# remove the recipe with the ID "sapturretmod:turrets/crossbow",
# which is the Crossbow Turret recipe this mod comes with by default
TurretAssembly.removeRecipeByName("sapturretmod:turrets/crossbow");

# remove the recipe whose output is the Revolver Turret bullets
# (also works with OreDictionary names)
TurretAssembly.removeRecipesByOutput(<sapturretmod:ammo_bullet>);

# adds a new recipe within a new group called "boop", producing 1
# diamond with 128 cocoa beans in 30 seconds (600 ticks), consuming
# 1200 RF/t
TurretAssembly.addRecipe("boop", [<minecraft:dye:3>*128], <minecraft:diamond>, 1200, 600);

# sets a stick as the icon for the group "boop" within the assembly
# GUI
TurretAssembly.setGroupIcon("boop", <minecraft:stick>);

# puts the group "boop" at the 5th position within the assembly GUI
TurretAssembly.setGroupOrder("boop", 5);
```