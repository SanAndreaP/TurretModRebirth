### 1.12.2-4.0.0-beta.4.1
##### This needs at least SanLib 1.6.1 and Minecraft Forge 14.23.5.2847 or higher!

* fixed death message not translating correctly (Note: Scaled Health's Blight entities have their message cut off, because they instanciate their own TextComponentTranslation and it's missing parameters. This is not fixable on my end)
* added loads of entities to the target categories of the config (**Note: delete the config in order to have those new entities show up**)
* Ender Gain Medium and Fuel Purifier upgrades now correctly prohibit the DamageSource being flammable, thus being able to damage fire-resistant entities now
* CraftTweaker support for the Turret Assembly Table and Electrolyte Generator
* fixed Turret Assembly Table not consuming ingredients correctly with certain recipes
* fixed Turret Assembly Table crashing when no recipe is registered
* fixed Electrolyte Generator not extracting treasure


### 1.12.2-4.0.0-beta.4
##### This needs at least SanLib 1.6.0 and Minecraft Forge 14.23.5.2847 or higher!

###### _Additions_
* TCU can now be bound to a turret for remote access
* added the Turret Crate, a special storage unit for dismantling turrets
* added the Ammo Cartridge, a backpack-like storage item replacing the ammo packs, it can hold 27 stacks of one type of ammo
* added new turret: Harpoon Turret, that can only be placed in water and only targets marine entities
* added tipped crossbow bolts
* added new upgrade: Ender Toxin I and II, damages Enderman without them teleporting, 2nd level also allows damaging the ender dragon
* added new upgrade: Shield Colorizer, allows you to customize the color of the forcefield for the Forcefield Turret
* added new upgrade: Turret Safe, which saves the turret from complete destruction by dismantling the Turret before a lethal blow
* added new upgrade: Remote Access, which allows the TCU to move items inbetween player and turret
* added new upgrade: Leveling, which allows a turret to gain experience and increase its stats
* added turret variants: depending on materials used, the turret can adopt that materials visuals
* added new Turret Assembly Table upgrade: Redstone Awareness, which allows enabling and disabling the crafting process
* new GUI opened via right-clicking a turret with an empty hand that shows stats and allows you to enable/disable the turret and toggle range visibility

###### _Changes_
* Turret Info Tablet is now the Turret Lexicon and now requires Patchouli in order for it to work
* turrets now have 3 targeting categories: anti-air, anti-ground and anti-marine
  - the Minigun Turret is now anti-air, thus can only attack flying entities like Ghasts and the Ender Dragon
  - the new Harpoon Turret is anti-marine
  - the Forcefield Turret is still able to target everything
  - every other turret will be anti-ground
  - _every living entity can be configured to be put into any of those categories_
* Turret Assembly Table can now insert into item containers, given they expose the IItemHandler capability, like the Ammo Cartridge does; Once full, the item is moved to a dedicated output slot
* significantly boosted output of ammo recipes
* turrets now have custom death messages when killing named entities/players
* more configuration options (turret health, fire rate, etc. now configurable)
* rebalanced turret values
* removed ability to place turrets upside-down, instead there will be a custom turret type that can be mounted on walls/ceilings in the future
* new textures! Also redone texture work on the forcefield, now supports recolorization - resource pack makers can now configure the texture, move speed and direction of each layer, remove and/or define new layers
* Electrolyte Generator recipes can now define the chances of trash and/or treasure
    - bumped up trash chance from 10% to 20%
    - bumped up treasure chance from 1% to 2%
* Electrolyte Generator "Effectiveness" has been renamed to "Efficiency"
* Chests no longer work for dismantling a turret
* redone the Turret Assembly Table GUI, it now has an item grid instead of a list; Recipe groups are now navigateable via up/down button to save space.

###### _Bugfixes_
* Turret Assembly Table now returns the items used in the crafting process when canceling it
* turret entity now has a name, so informational mods like WAILA/WAWLA/The One Probe can read those accurately
* fix lag with TCU target settings when lots of mods are installed
* Electrolyte Generator and Turret Assembly Table now prevent snow, torches and similar blocks from being placed on top of them
* Turret Assembly Table doesn't consume resources when output slot is full, basically deleting items
* Turret items now store the turret's health and custom name upon dismantling it
  
###### _Internal Changes_
* lots of code outsourced to SanLib (Config, GUIs)
* TileEntities, Turrets, Upgrades, Ammo and Repair Kits are now registered with a ResourceLocation
* upgrades can now define which tier range and/or what turrets specifically are allowed to be upgraded with it
* projectiles are now one entity class with different internal types, just like turrets
* targeting now uses ResourceLocations instead of entity classes (does not affect player targeting)
* all items are now prepared for the 1.13 / 1.14 flattening
* recoded Turret Assembly Table and Electrolyte Generator recipes to match structure of the new 1.13/1.14 crafting registry, also making them more robust
* Electrolyte Generator recipes can now be registered via the API
