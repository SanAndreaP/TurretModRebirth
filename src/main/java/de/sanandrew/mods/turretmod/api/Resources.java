/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.ResourceLocation;

public final class Resources
{
    public static final ResourceLocation NULL = new ResourceLocation("null");

    public static final ResourceLocation SOUND_RICOCHET_SPLASH    = rl("ricochet_splash");
    public static final ResourceLocation SOUND_RICOCHET_BULLET    = rl("ricochet_bullet");
    public static final ResourceLocation SOUND_RICOCHET_ARROW     = new ResourceLocation("entity.arrow.hit");
    public static final ResourceLocation SOUND_SHOOT_CROSSBOW     = new ResourceLocation("block.dispenser.launch");
    public static final ResourceLocation SOUND_SHOOT_CRYOLATOR    = rl("cryolator_shot");
    public static final ResourceLocation SOUND_SHOOT_SHOTGUN      = rl("shotgun_shot");
    public static final ResourceLocation SOUND_SHOOT_REVOLVER     = rl("revolver_shot");
    public static final ResourceLocation SOUND_SHOOT_MINIGUN      = rl("minigun_shot");
    public static final ResourceLocation SOUND_SHOOT_LASER        = rl("laser_shot");
    public static final ResourceLocation SOUND_SHOOT_FLAMETHROWER = rl("flamethrower_shot");
    public static final ResourceLocation SOUND_TURRET_EMPTY       = new ResourceLocation("block.dispenser.fail");
    public static final ResourceLocation SOUND_TURRET_HIT         = rl("turret_hurt");
    public static final ResourceLocation SOUND_TURRET_DEATH       = rl("turret_death");
    public static final ResourceLocation SOUND_TURRET_PICKUP      = rl("turret_pickup");
    public static final ResourceLocation SOUND_TURRET_IDLE        = rl("turret_idle");

    public static final ResourceLocation MODEL_ENTITY_BASE                = rl("models/entity/turret_base.json");
    public static final ResourceLocation MODEL_ENTITY_BASE_BUOY           = rl("models/entity/turret_base_buoy.json");
    public static final ResourceLocation MODEL_ENTITY_SHOTGUN             = rl("models/entity/turret_shotgun.json");
    public static final ResourceLocation MODEL_ENTITY_REVOLVER            = rl("models/entity/turret_revolver.json");
    public static final ResourceLocation MODEL_ENTITY_FORCEFIELD          = rl("models/entity/turret_shieldgen.json");
    public static final ResourceLocation MODEL_ENTITY_MINIGUN             = rl("models/entity/turret_minigun.json");
    public static final ResourceLocation MODEL_ENTITY_LASER               = rl("models/entity/turret_laser.json");
    public static final ResourceLocation MODEL_ENTITY_FTHROWER            = rl("models/entity/turret_flamethrower.json");
    public static final ResourceLocation MODEL_TILE_TURRET_ASSEMBLY_MODEL = rl("models/entity/turret_assembly.json");

    public static final ResourceLocation GUI_ASSEMBLY             = rl("guis/assembly.json");
    public static final ResourceLocation GUI_ASSEMBLY_FILTER      = rl("guis/assembly_filter.json");
    public static final ResourceLocation GUI_ELECTROLYTE          = rl("guis/electrolyte_generator.json");
    public static final ResourceLocation GUI_CARTRIDGE            = rl("guis/ammo_cartridge.json");
    public static final ResourceLocation GUI_TCRATE               = rl("guis/turret_crate.json");
    public static final ResourceLocation GUI_TCU_BASE             = rl("guis/tcu/base.json");
    public static final ResourceLocation GUI_TCU_INFO             = rl("guis/tcu/info.json");
    public static final ResourceLocation GUI_TCU_TARGET_CREATURES = rl("guis/tcu/target_creatures.json");
    public static final ResourceLocation GUI_TCU_TARGET_PLAYERS   = rl("guis/tcu/target_players.json");
    public static final ResourceLocation GUI_TCU_TARGET_SMART     = rl("guis/tcu/target_smart.json");
    public static final ResourceLocation GUI_TCU_UPGRADES         = rl("guis/tcu/upgrades.json");
    public static final ResourceLocation GUI_TCU_COLORIZER        = rl("guis/tcu/colorizer.json");
    public static final ResourceLocation GUI_TCU_LEVELS           = rl("guis/tcu/levels.json");
    public static final ResourceLocation GUI_TCU_REMOTE_ACCESS    = rl("guis/tcu/remote_access.json");
    public static final ResourceLocation GUI_TINFO                = rl("guis/turret_info.json");
    public static final ResourceLocation GUI_TINFO_FORCEFIELD     = rl("guis/turret_info_forcefield.json");

    public static final String           TEXTURE_ENTITY_CROSSBOW_BASE      = tx("textures/entities/turrets/t1_crossbow/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_CROSSBOW_GLOW      = rl("textures/entities/turrets/t1_crossbow/glow.png");
    public static final ResourceLocation TEXTURE_ENTITY_CROSSBOW_BOLT      = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final String           TEXTURE_ENTITY_CRYOLATOR_BASE     = tx("textures/entities/turrets/t1_cryolator/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_CRYOLATOR_GLOW     = rl("textures/entities/turrets/t1_cryolator/glow.png");
    public static final String           TEXTURE_ENTITY_SHOTGUN_BASE       = tx("textures/entities/turrets/t1_shotgun/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_SHOTGUN_GLOW       = rl("textures/entities/turrets/t1_shotgun/glow.png");
    public static final String           TEXTURE_ENTITY_HARPOON_BASE       = tx("textures/entities/turrets/t1_harpoon/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_HARPOON_GLOW       = rl("textures/entities/turrets/t1_harpoon/glow.png");
    public static final String           TEXTURE_ENTITY_REVOLVER_BASE      = tx("textures/entities/turrets/t2_revolver/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_REVOLVER_GLOW      = rl("textures/entities/turrets/t2_revolver/glow.png");
    public static final ResourceLocation TEXTURE_ENTITY_FORCEFIELD_BASE    = rl("textures/entities/turrets/t2_forcefield.png");
    public static final ResourceLocation TEXTURE_ENTITY_FORCEFIELD_GLOW    = rl("textures/entities/turrets/t2_forcefield_glow.png");
    public static final String           TEXTURE_ENTITY_MINIGUN_BASE       = tx("textures/entities/turrets/t2_minigun/%s.png");
    public static final ResourceLocation TEXTURE_ENTITY_MINIGUN_GLOW       = rl("textures/entities/turrets/t2_minigun/glow.png");
    public static final ResourceLocation TEXTURE_ENTITY_LASER_BASE         = rl("textures/entities/turrets/t3_laser.png");
    public static final ResourceLocation TEXTURE_ENTITY_LASER_GLOW         = rl("textures/entities/turrets/t3_laser_glow.png");
    public static final ResourceLocation TEXTURE_ENTITY_FTHROWER_BASE      = rl("textures/entities/turrets/t3_flamethrower.png");
    public static final ResourceLocation TEXTURE_ENTITY_FTHROWER_GLOW      = rl("textures/entities/turrets/t3_flamethrower_glow.png");
    public static final ResourceLocation TEXTURE_ENTITY_BULLET             = rl("textures/entities/projectiles/bullet.png");
    public static final ResourceLocation TEXTURE_ENTITY_FLAME_RED          = rl("textures/entities/projectiles/flame_red.png");
    public static final ResourceLocation TEXTURE_ENTITY_FLAME_BLUE         = rl("textures/entities/projectiles/flame_blue.png");
    public static final ResourceLocation TEXTURE_GUI_BUTTONS               = rl("textures/gui/buttons.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_BUTTONS           = rl("textures/gui/tcu/buttons.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_TARGETS           = rl("textures/gui/tcu/page_targets.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_UPGRADES          = rl("textures/gui/tcu/page_upgrades.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_INFO              = rl("textures/gui/tcu/page_info.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_SMARTTGT          = rl("textures/gui/tcu/page_smart.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_COLORIZER         = rl("textures/gui/tcu/page_colorizer.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_CAM_NA            = rl("textures/gui/tcu/cam_unavailable.png");
    public static final ResourceLocation TEXTURE_GUI_ASSEMBLY_FLT          = rl("textures/gui/turretassembly/filter.png");
    public static final ResourceLocation TEXTURE_GUI_TURRETINFO            = rl("textures/gui/turretinfo/backg.png");
    public static final ResourceLocation TEXTURE_GUI_CARTRIDGE             = rl("textures/gui/ammo_cartridge.png");
    public static final ResourceLocation TEXTURE_GUI_TCU_HUD               = rl("textures/gui/tcu/hud.png");
    public static final ResourceLocation TEXTURE_GUI_LEXICON_ELEMENTS      = rl("textures/gui/lexicon/elements.png");
    public static final ResourceLocation TEXTURE_TILE_TRANSMITTER          = rl("textures/blocks/item_transmitter.png");
    public static final ResourceLocation TEXTURE_TILE_TRANSMITTER_GLOW     = rl("textures/blocks/item_transmitter_glow.png");
    public static final ResourceLocation TEXTURE_TILE_TURRET_ASSEMBLY      = rl("textures/blocks/turret_assembly.png");
    public static final ResourceLocation TEXTURE_TILE_ELECTROLYTE_GEN_WIRE = rl("textures/blocks/electrolyte_gen_cable.png");
    public static final ResourceLocation TEXTURE_JEI_ASSEMBLY_BKG          = rl("textures/gui/turretassembly/jei_crafting.png");

    public static final ResourceLocation PATCHOULI              = rl("turret_lexicon");
    public static final ResourceLocation PATCHOULI_CAT_TURRETS  = rl("turrets");
    public static final ResourceLocation PATCHOULI_CAT_AMMO     = rl("ammo");
    public static final ResourceLocation PATCHOULI_CAT_UPGRADES = rl("upgrades");
    public static final ResourceLocation PATCHOULI_CAT_MISC     = rl("misc");

    public static final ResourceLocation SHADER_GRAYSCALE_FRAG       = rl("shader/grayscale.frag");
    public static final ResourceLocation SHADER_ALPHA_OVERRIDE_FRAG  = rl("shader/alpha_override.frag");

    public static final ResourceLocation PROPERTY_FORCEFIELD = rl("textures/entities/shield/properties.json");

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(TmrConstants.ID, path);
    }

    private static String tx(String path) {
        return TmrConstants.ID + ':' + path;
    }
}
