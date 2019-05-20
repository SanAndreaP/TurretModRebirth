**1.12.2-4.0.0-beta.4**

_Additions_
* new upgrade: Ender Toxin I and II, damages Enderman without them teleporting, 2nd level also
  allows damaging the ender dragon
* new upgrade: Shield Colorizer, allows you to customize the color of the forcefield for the
  Forcefield Turret
* added the Ammo Cartridge, a backpack-like storage item replacing the ammo packs, it can hold
  27 stacks of one type of ammo, the item texture/model will match the content

_Changes & Bugfixes_
* turrets now have 3 targeting categories: anti-air, anti-ground and anti-marine
    - the Minigun Turret is now anti-air, thus can only attack flying entities like Ghasts and
      the Ender Dragon
    - the Forcefield Turret is still able to target everything
    - every other turret will be anti-ground
    - _every entity can be configured to be put into any of those categories_
* Turret Assembly Table now returns the items used in the crafting process when canceling it
* more configuration options (turret health, fire rate, etc. now configurable)
* rebalanced turret values
* turret entity now has a name, so informational mods like WAILA/WAWLA/The One Probe can read
  those accurately
* fix lag with TCU target settings when lots of mods are installed
* turrets now have custom death messages when killing named entities/players
* removed ability to place turrets upside-down, instead there will be a custom turret type that
  can be mounted on walls/ceilings in the future
* redone texture work on the forcefield, now also supports recolorization - resource pack
  makers can now configure the texture, move speed and direction of each layer, remove and/or
  define new layers
* Electrolyte Generator and Turret Assembly Table now prevent snow, torches and similar blocks
  from being placed on top of them

_Internal Changes_
* lots of code outsourced to SanLib (Config, Turret Info Tablet, GUIs)
* TileEntities are now registered with a ResourceLocation
* upgrades can now define which tier range and/or what turrets specifically are allowed to be
  upgraded with it
* projectiles are now one entity class with different internal types, just like turrets
* targeting now uses ResourceLocations instead of entity classes (does not affect player
  targeting)
* all items are now prepared for the 1.13 / 1.14 flattening