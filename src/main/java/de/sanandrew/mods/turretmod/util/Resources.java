/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;

public enum Resources
{
    TURRET_T1_BASE_MODEL("models/entity/turret_base.json"),
    TURRET_T1_CROSSBOW("textures/entities/turrets/t1_crossbow.png"),
    TURRET_T1_CROSSBOW_GLOW("textures/entities/turrets/t1_crossbow_glow.png"),
    TURRET_T1_SHOTGUN("textures/entities/turrets/t1_shotgun.png"),
    TURRET_T1_SHOTGUN_GLOW("textures/entities/turrets/t1_shotgun_glow.png"),
    TURRET_T1_SHOTGUN_MODEL("models/entity/turret_shotgun.json"),
    TURRET_T1_SNOWBALL("textures/entities/turrets/t1_snowball.png"),
    TURRET_T1_SNOWBALL_GLOW("textures/entities/turrets/t1_snowball_glow.png"),
    TURRET_T2_REVOLVER("textures/entities/turrets/t2_revolver.png"),
    TURRET_T2_REVOLVER_GLOW("textures/entities/turrets/t2_revolver_glow.png"),
    TURRET_T2_REVOLVER_MODEL("models/entity/turret_revolver.json"),
    TURRET_T2_SHIELDGEN("textures/entities/turrets/t2_shieldgen.png"),
    TURRET_T2_SHIELDGEN_GLOW("textures/entities/turrets/t2_shieldgen_glow.png"),
    TURRET_T2_SHIELDGEN_MODEL("models/entity/turret_shieldgen.json"),
    TURRET_T2_MINIGUN("textures/entities/turrets/t2_minigun.png"),
    TURRET_T2_MINIGUN_EE("textures/entities/turrets/t2_minigun_sc.png"),
    TURRET_T2_MINIGUN_GLOW("textures/entities/turrets/t2_minigun_glow.png"),
    TURRET_T2_MINIGUN_MODEL("models/entity/turret_minigun.json"),
    TURRET_T3_LASER("textures/entities/turrets/t3_laser.png"),
    TURRET_T3_LASER_GLOW("textures/entities/turrets/t3_laser_glow.png"),
    TURRET_T3_LASER_MODEL("models/entity/turret_laser.json"),
    TURRET_T3_FTHROWER("textures/entities/turrets/t3_flamethrower.png"),
    TURRET_T3_FTHROWER_GLOW("textures/entities/turrets/t3_flamethrower_glow.png"),
    TURRET_T3_FTHROWER_MODEL("models/entity/turret_flamethrower.json"),

    PROJECTILE_BULLET("textures/entities/projectiles/bullet.png"),
    PROJECTILE_FLAME_RED("textures/entities/projectiles/flame_red.png"),
    PROJECTILE_FLAME_BLUE("textures/entities/projectiles/flame_blue.png"),

    GUI_BUTTONS("textures/gui/buttons.png"),
    GUI_TCU_BUTTONS("textures/gui/tcu/buttons.png"),
    GUI_TCU_TARGETS("textures/gui/tcu/page_targets.png"),
    GUI_TCU_UPGRADES("textures/gui/tcu/page_upgrades.png"),
    GUI_TCU_INFO("textures/gui/tcu/page_info.png"),
    GUI_TCU_SMARTTGT("textures/gui/tcu/page_smart.png"),
    GUI_TCU_CAM_NA("textures/gui/tcu/cam_unavailable.png"),
//    GUI_TCU_HUD("textures/gui/tcu/hud.png"),
    GUI_ASSEMBLY_CRF("textures/gui/turretassembly/manual.png"),
    GUI_ASSEMBLY_FLT("textures/gui/turretassembly/filter.png"),
    GUI_POTATOGEN("textures/gui/potatogen.png"),
    GUI_TURRETINFO("textures/gui/turretinfo/backg.png"),

    TINFO_GRP_AMMO("textures/gui/lexicon/group_ammo.png"),
    TINFO_GRP_INFO("textures/gui/lexicon/group_info.png"),
    TINFO_GRP_MISC("textures/gui/lexicon/group_misc.png"),
    TINFO_GRP_TURRET("textures/gui/lexicon/group_turrets.png"),
    TINFO_GRP_UPGRADE("textures/gui/lexicon/group_upgrades.png"),
    TINFO_GRP_STENCIL("textures/gui/turretinfo/stencil_grp.png"),
    TINFO_GRP_SRC("textures/gui/lexicon/group_search.png"),
    TINFO_ELEMENTS("textures/gui/lexicon/elements.png"),

    TURRET_FORCEFIELD_P1("textures/entities/shield_1.png"),
    TURRET_FORCEFIELD_P2("textures/entities/shield_2.png"),
    TURRET_FORCEFIELD_P3("textures/entities/shield_3.png"),
    TILE_ITEM_TRANSMITTER("textures/blocks/item_transmitter.png"),
    TILE_ITEM_TRANSMITTER_GLOW("textures/blocks/item_transmitter_glow.png"),
    TILE_TURRET_ASSEMBLY("textures/blocks/turret_assembly.png"),
    TILE_TURRET_ASSEMBLY_MODEL("models/block/turret_assembly_tile.json"),
//    TILE_ELECTROLYTE_GEN("textures/blocks/electrolyte_gen.png"),
    TILE_ELECTROLYTE_GEN_WIRE("textures/blocks/electrolyte_gen_cable.png"),

    SHADER_CATEGORY_BUTTON_FRAG("shader/categorybtn.frag"),
    SHADER_GRAYSCALE_FRAG("shader/grayscale.frag"),
    SHADER_ALPHA_OVERRIDE_FRAG("shader/alpha_override.frag"),

    JEI_ASSEMBLY_BKG("textures/gui/turretassembly/jei_crafting.png");

    public final ResourceLocation resource;

    Resources(String location) {
        this.resource = new ResourceLocation(TmrConstants.ID, location);
    }

    @Override
    public String toString() {
        return this.resource.toString();
    }
}
